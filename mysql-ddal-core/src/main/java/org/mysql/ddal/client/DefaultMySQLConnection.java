/**
 * MySQL Distributed Data Access Layer Middleware
 * @Copyrights shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal.client;

import org.mysql.ddal.AuthData;
import org.mysql.ddal.MySQLConnection;
import org.mysql.ddal.MySQLHandler;
import org.mysql.ddal.query.MySQLQueryCommand;
import org.mysql.ddal.query.MySQLQueryHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认MySQL连接实现
 * 
 * @author 智慧工厂@M
 *
 */
@Slf4j
public class DefaultMySQLConnection extends MySQLConnection {
	@Setter
	private int currentPacketSequenceNumber;
	@Getter
	private volatile EventLoopGroup group = new NioEventLoopGroup();
	@Setter
	private AuthData authData;

	private Thread thread;

	@Setter
	@Getter
	private ChannelHandlerContext channelHandlerContext;

	@Setter
	@Getter
	private MySQLHandler mysqlHandler;

	@Setter
	@Getter
	private String errorMessage;

	/**
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 */
	public DefaultMySQLConnection(String hostname, int port, String username, String password) {
		super(hostname, port, username, password);
	}

	public DefaultMySQLConnection(String hostname, int port, String username, String password, String databaseName,
			String charsetName) {
		super(hostname, port, username, password, databaseName, charsetName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.Connection#getCurrentPacketSequenceNumber()
	 */
	@Override
	public int getCurrentPacketSequenceNumber() {
		return this.currentPacketSequenceNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.Connection#executeQuery(org.mysql.ddal.query.
	 * MySQLQueryCommand, org.mysql.ddal.query.MySQLQueryHandler)
	 */
	@Override
	public void executeQuery(MySQLQueryCommand command, MySQLQueryHandler handler) {
		if (this.isClosed()) {
			throw new RuntimeException("Has been closed");
		}

		this.mysqlHandler = handler;
		this.channelHandlerContext.writeAndFlush(command.getMySQLMessageWriter().getBuffer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() {
		this.setClosed(true);
		if (this.group != null) {
			log.debug("SHUTDOWN");
			this.group.shutdownGracefully();
		}
	}

	protected void init() {
		DefaultMySQLConnection.this.group = new NioEventLoopGroup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.MySQLConnection#doNativeConnect()
	 */
	@Override
	@SneakyThrows
	public void doNativeConnect() {
		thread = new Thread(() -> {
			try {
				Bootstrap client = new Bootstrap();
				client.group(group);
				client.channel(NioSocketChannel.class);

				client.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel ch) throws Exception {
						ch.pipeline().addLast(new MySQLClientMessageHandler(DefaultMySQLConnection.this));
					}
				});
				ChannelFuture future = client.connect(getHostname(), getPort()).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				log.error("XX", e);
			} finally {
				close();
			}
		});
		thread.setDaemon(true);
		thread.start();
		synchronized (this) {
			this.wait();
		}

		if (getErrorMessage() != null) {
			throw new RuntimeException(getErrorMessage());
		}
	}

}
