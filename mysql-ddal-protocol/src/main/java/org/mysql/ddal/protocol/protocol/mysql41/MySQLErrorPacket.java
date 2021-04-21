/**
 * @(#) MySQLErrorPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.MySQLMessageWriter;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

/**
 * @author win10
 *
 */
@ToString
@Getter
public class MySQLErrorPacket extends MySQLPacket {
	public static final byte FIELD_COUNT = (byte) 0xff;
	public static final byte SQLSTATE_MARKER = (byte) '#';
	public static final byte[] DEFAULT_SQLSTATE = "HY000".getBytes();

	private final int header;
	private final int errorCode;
	private final byte sqlStateMarker;
	private final String sqlState;
	private final String errorMessage;

	/**
	 * @param buffer
	 */
	public MySQLErrorPacket(int capabilityFlags, ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader in = getReader();
		this.header = in.readUB() & 0xff;
		this.errorCode = in.readUB2();
		if ((capabilityFlags & MySQLCapabilityFlags.CLIENT_PROTOCOL_41) != 0) {
			this.sqlStateMarker = in.readUB();
			this.sqlState = new String(in.readBytes(5));
		} else {
			this.sqlStateMarker = 0;
			this.sqlState = null;
		}
		this.errorMessage = new String(in.readBytesEndwithEof());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#getMySQLMessageWriter()
	 */
	@Override
	public MySQLMessageWriter getMySQLMessageWriter() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#calucatePacketLength()
	 */
	@Override
	public int calucatePacketLength() {
		return 0;
	}

}
