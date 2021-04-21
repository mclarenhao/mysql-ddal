/**
 * @(#) MySQLRowDataPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import java.util.ArrayList;
import java.util.List;

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
public class MySQLRowDataPacket extends MySQLPacket {

	private static final byte NULL_MARK = (byte) 251;

	public final int fieldCount;
	public final List<byte[]> fieldValues;

	public MySQLRowDataPacket(int packetId, int fieldCount, List<byte[]> fieldValues) {
		super(0, packetId);
		this.fieldCount = fieldCount;
		this.fieldValues = fieldValues;
	}

	/**
	 * @param buffer
	 */
	public MySQLRowDataPacket(int fieldCount, ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader reader = getReader();
		fieldValues = new ArrayList<>();
		this.fieldCount = fieldCount;

		for (int i = 0; i < fieldCount; i++) {
			byte[] buf = reader.readBytesWithLength();
			fieldValues.add(buf);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#getMySQLMessageWriter()
	 */
	@Override
	public MySQLMessageWriter getMySQLMessageWriter() {
		MySQLMessageWriter writer = new MySQLMessageWriter();
		for (int i = 0; i < fieldCount; i++) {
			byte[] fv = fieldValues.get(i);
			if (fv == null || fv.length == 0) {
				writer.writeByte(NULL_MARK);
			} else {
				writer.writeLength(fv.length);
				writer.writeBytes(fv);
			}
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
		int size = 0;
		MySQLMessageReader reader = getReader();
		for (int i = 0; i < fieldCount; i++) {
			byte[] v = fieldValues.get(i);
			size += (v == null || v.length == 0) ? 1 : reader.getLength(v);
		}
		return size;
	}

}
