/**
 * MySQL Distributed Data Access Layer Middleware
 * @Copyrights shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal.client;

import org.mysql.ddal.AuthData;
import org.mysql.ddal.DUMP;
import org.mysql.ddal.MySQLHandler;
import org.mysql.ddal.auth.AuthenticationProvider;
import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLErrorPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLHandshark41;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLHandshark41Response;
import org.mysql.ddal.query.MySQLQueryHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public class MySQLClientMessageHandler extends ChannelInboundHandlerAdapter {
	private final DefaultMySQLConnection connection;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		connection.setChannelHandlerContext(ctx);
		ByteBuf message = (ByteBuf) msg;
		if (connection.isClosed()) {
			MySQLMessageReader reader = new MySQLMessageReader(message);
			reader.skip(3);
			int sequenceNumber = reader.readUB();
			message.resetReaderIndex();
			if (sequenceNumber == 0) {
				/**
				 * 处理HANDSHARK包
				 */
				MySQLHandshark41 handshark41 = new MySQLHandshark41(message);
				log.debug("Handshark is ::" + handshark41);
				message.resetReaderIndex();
				DUMP.dump("Handshark", message);
				AuthData authData = new AuthData(handshark41.getAuthPluginDataPart1(),
						handshark41.getAuthPluginDataPart2());
				connection.setAuthData(authData);

				AuthenticationProvider authProvider = AuthenticationProvider
						.getAuthenticationProvider(handshark41.getAuthPluginName());
				if (authProvider == null) {
					ctx.channel().close();
					connection.setErrorMessage("could not find password plugin: " + handshark41.getAuthPluginName());
					synchronized (connection) {
						connection.notify();
					}
					return;
				}

				byte[] password = authProvider.encodePassword(connection.getPassword(), authData.seed());
				MySQLHandshark41Response handsharkResponse = new MySQLHandshark41Response(0, ++sequenceNumber,
						connection.CAPABILITIES, 16777216, 33, connection.getUsername(), password,
						connection.getDatabaseName(), handshark41.getAuthPluginName(), 0);
				ctx.writeAndFlush(handsharkResponse.getMySQLMessageWriter().getBuffer());
				return;
			}

			/**
			 * 过滤掉头部的Descriptor包
			 */
			reader.skip(4);
			int length = reader.readUB();
			reader.readBytes(length);
			if (!reader.hasRemaining()) {
				connection.close();
				connection.setErrorMessage("认证失败");
				synchronized (connection) {
					connection.notify();
				}
				return;
			}

			ByteBuf copys = message.copy();
			copys.skipBytes(4);
			int header = 0xFF & (copys.readByte());
			copys.release();
			if (header == 255) {
				MySQLErrorPacket err = new MySQLErrorPacket(connection.CAPABILITIES, message);
				connection.close();
				connection.setErrorMessage("连接失败,原因为:" + err.getErrorMessage());
				synchronized (connection) {
					connection.notify();
				}
			}

			if (header == 0 || header == 254) {
				log.debug("连接成功");
				connection.setErrorMessage(null);
				synchronized (connection) {
					connection.notify();
				}
				connection.setClosed(false);
			}

			return;
		}

		/**
		 * 接受MYSQL命令的返回值
		 */
		MySQLHandler handler = connection.getMysqlHandler();
		if (handler == null) {
			log.error("MySQL handler is null!");
			return;
		}

		if (handler instanceof MySQLQueryHandler) {
			MySQLQueryHandler queryHandler = (MySQLQueryHandler) handler;
			queryHandler.onReceiveMessage(connection.getCAPABILITIES(), message);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (!connection.isClosed()) {
			/**
			 * 自动重连
			 */
			log.debug("MySQL客户端正在断开重连");
			connection.doNativeConnect();
		}

	}

}
