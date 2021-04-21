/**
 * @(#) MySQLResultSetHeaderPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.MySQLMessageWriter;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
@ToString
public class MySQLResultSetHeaderPacket extends MySQLPacket {
	private final int fieldCount;
	private final long extra;

	/**
	 * @param buffer
	 */
	public MySQLResultSetHeaderPacket(ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader reader = getReader();
		this.fieldCount = (int) reader.readLength();
		this.extra = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#getMySQLMessageWriter()
	 */
	@Override
	public MySQLMessageWriter getMySQLMessageWriter() {
		MySQLMessageWriter writer = new MySQLMessageWriter();
		writer.writeLength(fieldCount);
		if (extra > 0) {
			writer.writeLength(extra);
		}
		return writer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#calucatePacketLength()
	 */
	@Override
	public int calucatePacketLength() {
		MySQLMessageReader reader = getReader();
		int size = reader.getLength(fieldCount);
		if (extra > 0) {
			size += reader.getLength(extra);
		}
		return size;
	}

}
