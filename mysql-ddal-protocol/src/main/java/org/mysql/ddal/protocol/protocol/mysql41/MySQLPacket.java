/**
 * @(#) MySQLPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.MySQLMessageWriter;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author 智慧工厂
 *
 */
@Getter
public abstract class MySQLPacket {
	private int packetLength;
	private int packetId;
	private final MySQLMessageReader reader;

	public MySQLPacket(int packetLength, int packetId) {
		super();
		this.packetLength = packetLength;
		this.packetId = packetId;
		this.reader = null;
	}

	public MySQLPacket(ByteBuf buffer) {
		this.reader = new MySQLMessageReader(buffer);
		this.packetLength = reader.readUB3();
		this.packetId = reader.readUB();
	}

	public abstract MySQLMessageWriter getMySQLMessageWriter();

	public abstract int calucatePacketLength();

}
