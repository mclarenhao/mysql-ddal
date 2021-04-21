/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal.query;

import java.util.ArrayList;
import java.util.List;

import org.mysql.ddal.MySQLHandler;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLColumnDefinitionPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLEofPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLErrorPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLResultSetHeaderPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLRowDataPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * MySQL查询回调器
 * 
 * @author mclaren
 *
 */
@Slf4j
public class MySQLQueryHandler implements MySQLHandler {
	private ByteBuf remaining = Unpooled.buffer();
	private MySQLResultSetHeaderPacket header = null;
	private List<MySQLColumnDefinitionPacket> columns = new ArrayList<>();
	private boolean columnsReady = false;
	private List<MySQLRowDataPacket> rows = new ArrayList<>();

	public MySQLQueryHandler() {

	}

	public void onReceiveMessage(int capabilities, ByteBuf buffer) {
		buffer.skipBytes(4);
		if ((buffer.readByte() & 0xff) == 255) {
			buffer.resetReaderIndex();
			this.onError(new MySQLErrorPacket(capabilities, buffer));
			return;
		}
		buffer.resetReaderIndex();
		remaining.writeBytes(buffer);
		if (header == null) {
			header = new MySQLResultSetHeaderPacket(remaining);
			this.onReceiveHeader(header);
		}

		while (remaining.readableBytes() > 0) {
			for (int i = columns.size(); i < header.getFieldCount(); i++) {
				int readerIndex = remaining.readerIndex();
				try {
					MySQLColumnDefinitionPacket column = new MySQLColumnDefinitionPacket(this.remaining);
					this.onReceiveColumnDefinitions(column);
					columns.add(column);
				} catch (Exception e) {
					this.remaining.readerIndex(readerIndex);
					return;
				}
			}

			if (!columnsReady) {
				int readerIndex = this.remaining.readerIndex();
				try {
					new MySQLEofPacket(capabilities, this.remaining);
					onReceiveColumnDefinitionsCompleted();
					columnsReady = true;
				} catch (Exception e) {
					this.remaining.readerIndex(readerIndex);
					return;
				}
			}

			int readerIndex = remaining.readerIndex();
			try {
				MySQLRowDataPacket row = new MySQLRowDataPacket(header.getFieldCount(), remaining);
				rows.add(row);
				this.onReceiveRowData(row);
			} catch (Exception e) {
				this.remaining.readerIndex(readerIndex);
				return;
			}

			if (this.remaining.readableBytes() == 9) {
				new MySQLEofPacket(capabilities, this.remaining);
				break;
			}
		}

		if (remaining.readableBytes() == 0) {
			log.debug("结束");
			this.remaining.release();
			this.onEnd();
		}
	}

	public void onError(MySQLErrorPacket error) {

	}

	public void onReceiveColumnDefinitionsCompleted() {

	}

	public void onReceiveHeader(MySQLResultSetHeaderPacket headerPacket) {
	}

	public void onReceiveColumnDefinitions(MySQLColumnDefinitionPacket definitionPacket) {
	}

	public void onReceiveRowData(MySQLRowDataPacket rowDataPacket) {
	}

	public void onEnd() {

	}
}
