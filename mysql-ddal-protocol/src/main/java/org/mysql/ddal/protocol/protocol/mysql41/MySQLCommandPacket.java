/**
 * @(#) MySQLCommandPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.MySQLMessageWriter;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class MySQLCommandPacket extends MySQLPacket {
	private final int action;
	private final String sql;

	public MySQLCommandPacket(int action, String sql) {
		super(0, 0);
		this.action = action;
		this.sql = sql;
	}

	/**
	 * @param buffer
	 */
	public MySQLCommandPacket(ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader reader = getReader();
		this.action = reader.readUB();
		this.sql = new String(reader.readBytesEndwithEof());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#getMySQLMessageWriter()
	 */
	@Override
	public MySQLMessageWriter getMySQLMessageWriter() {
		MySQLMessageWriter writer = new MySQLMessageWriter();
		writer.writeUB3(this.calucatePacketLength());
		writer.writeUB((byte) this.getPacketId());
		writer.writeUB((byte) this.action);
		writer.writeBytes(sql.getBytes());
		return writer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#calucatePacketLength()
	 */
	@Override
	public int calucatePacketLength() {
		return 1 + sql.getBytes().length;
	}

}
