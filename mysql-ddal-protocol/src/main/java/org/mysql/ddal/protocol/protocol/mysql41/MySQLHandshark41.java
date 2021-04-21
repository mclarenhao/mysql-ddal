/**
 * @(#) MySQLHandshark41.java MySQL中间件
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
public class MySQLHandshark41 extends MySQLPacket {
	private final int protocolVersion;
	private final String serverVersion;
	private final int threadId;
	private final byte[] authPluginDataPart1;
	private final int capabilityFlags;
	private final int charsetIndex;
	private final int statusFlags;
	private final int authPluginDataLen;
	private final byte[] authPluginDataPart2;
	private final String authPluginName;

	/**
	 * @param buffer
	 */
	public MySQLHandshark41(ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader reader = getReader();
		this.protocolVersion = reader.readUB();
		this.serverVersion = reader.readStringWithNull();
		this.threadId = (int) reader.readUB4();
		this.authPluginDataPart1 = reader.readBytes(8);
		reader.skip(1);
		byte[] capability_flags_1 = reader.readBytes(2);
		this.charsetIndex = reader.readUB();
		this.statusFlags = reader.readUB2();
		byte[] capability_flags_2 = reader.readBytes(2);
		byte[] capabilitiesBuffer = new byte[4];
		capabilitiesBuffer[0] = capability_flags_2[0];
		capabilitiesBuffer[1] = capability_flags_2[1];
		capabilitiesBuffer[2] = capability_flags_1[0];
		capabilitiesBuffer[3] = capability_flags_1[1];
		int i = capabilitiesBuffer[0] & 0xff;
		i |= (capabilitiesBuffer[1] & 0xff) << 8;
		i |= (capabilitiesBuffer[2] & 0xff) << 16;
		i |= (capabilitiesBuffer[3] & 0xff) << 24;
		this.capabilityFlags = i;

		if ((this.capabilityFlags & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH) != 0) {
			this.authPluginDataLen = reader.readUB();
		} else {
			this.authPluginDataLen = 0;
		}
		reader.skip(10);
		if ((this.capabilityFlags & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH) != 0) {
			this.authPluginDataPart2 = reader.readBytesWithNull();
			// reader.skip(1);
			this.authPluginName = new String(reader.readBytesWithNull());
		} else {
			this.authPluginDataPart2 = reader.readBytesWithNull();
			this.authPluginName = null;
		}

		reader.skip(reader.getBuffer().readableBytes());
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
