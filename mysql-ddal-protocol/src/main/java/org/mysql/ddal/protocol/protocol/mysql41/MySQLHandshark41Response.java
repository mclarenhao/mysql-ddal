/**
 * @(#) MySQLHandshark41Response.java MySQL中间件
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
public class MySQLHandshark41Response extends MySQLPacket {
	public static final byte[] FILTER_23 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0 };
	private final int clientFlag;
	private final int maxPacketSize;
	private final int charsetIndex;
	private final String username;
	private final byte[] authResponse;
	private final String databaseName;
	private final String clientPluginName;
	private final int zstdCompressionLevel;

	public MySQLHandshark41Response(int packetLength, int packetId, int clientFlag, int maxPacketSize, int charsetIndex,
			String username, byte[] authResponse, String databaseName, String clientPluginName,
			int zstdCompressionLevel) {
		super(packetLength, packetId);
		this.clientFlag = clientFlag;
		this.maxPacketSize = maxPacketSize;
		this.charsetIndex = charsetIndex;
		this.username = username;
		this.authResponse = authResponse;
		this.databaseName = databaseName;
		this.clientPluginName = clientPluginName;
		this.zstdCompressionLevel = zstdCompressionLevel;
	}

	/**
	 * @param buffer
	 */
	public MySQLHandshark41Response(ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader reader = getReader();
		this.clientFlag = (int) reader.readUB2();
		this.maxPacketSize = (int) reader.readUB2();
		this.charsetIndex = reader.readUB();
		reader.skip(23);
		this.username = reader.readStringWithNull();

		if ((this.clientFlag & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
			this.authResponse = reader.readBytesWithLength();
		} else {
			int len = reader.readUB();
			this.authResponse = reader.readBytes(len);
		}

		if ((this.clientFlag & MySQLCapabilityFlags.CLIENT_CONNECT_WITH_DB) != 0) {
			this.databaseName = reader.readStringWithNull();
		} else {
			this.databaseName = null;
		}

		if ((this.clientFlag & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH) != 0) {
			this.clientPluginName = reader.readStringWithNull();
		} else {
			this.clientPluginName = null;
		}

		zstdCompressionLevel = 0;
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
		writer.writeUB4(this.clientFlag);
		writer.writeUB4(maxPacketSize);
		writer.writeUB((byte) charsetIndex);
		writer.writeBytes(FILTER_23);
		writer.writeStringWithNull(username);

		if ((clientFlag & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
			writer.writeBytesLength(authResponse);
		} else {
			writer.writeUB((byte) this.authResponse.length);
			writer.writeBytes(this.authResponse);
		}

		if ((clientFlag & MySQLCapabilityFlags.CLIENT_CONNECT_WITH_DB) != 0) {
			writer.writeStringWithNull(databaseName);
		}

		if ((clientFlag & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH) != 0) {
			writer.writeStringWithNull(clientPluginName);
		}
		// writer.writeUB((byte) zstdCompressionLevel);
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
		size += 32;
		size += this.getUsername().getBytes().length + 1;
		if ((this.clientFlag & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
			size += 4;
			size += this.authResponse.length;
		} else {
			size += 1;
			size += this.authResponse.length;
		}
		if ((this.getClientFlag() & MySQLCapabilityFlags.CLIENT_CONNECT_WITH_DB) != 0) {
			size += this.getDatabaseName().getBytes().length;
			size += 1;
		}
		if ((this.clientFlag & MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH) != 0) {
			size += this.getClientPluginName().length();
			size += 1;
		}
		return size;
	}

}
