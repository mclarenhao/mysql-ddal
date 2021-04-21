/**
 * @(#) MySQLEofPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.MySQLMessageWriter;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 智慧工厂@M
 * @see https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_basic_err_packet.html
 */
@Getter
@ToString
public class MySQLEofPacket extends MySQLPacket {
	public static final int DEFAULT_HEADER = 0xFE;
	private final int header;
	private final int warnings;
	private final int statusFlags;
	private final int capabilities;

	public MySQLEofPacket(int packetId, int warnings, int statusFlags, int capabilities) {
		super(0, packetId);
		this.header = DEFAULT_HEADER;
		this.warnings = warnings;
		this.statusFlags = statusFlags;
		this.capabilities = capabilities;
	}

	/**
	 * @param buffer
	 */
	public MySQLEofPacket(int capabilities, ByteBuf buffer) {
		super(buffer);
//		DUMP.dump("EOF", buffer);
		this.capabilities = capabilities;
		MySQLMessageReader reader = getReader();
		this.header = reader.readUB();
		if ((capabilities & MySQLCapabilityFlags.CLIENT_PROTOCOL_41) != 0) {
			this.warnings = reader.readUB2();
			this.statusFlags = reader.readUB2();
		} else {
			this.warnings = 0;
			this.statusFlags = 0;
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
		writer.writeUB3(this.calucatePacketLength());
		writer.writeUB((byte) this.getPacketId());
		if ((capabilities & MySQLCapabilityFlags.CLIENT_PROTOCOL_41) != 0) {
			writer.writeUB2(warnings);
			writer.writeUB2(statusFlags);
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
		int size = 4 + 1;
		if ((capabilities & MySQLCapabilityFlags.CLIENT_PROTOCOL_41) != 0) {
			size += 4;
		}
		return size;
	}
}
