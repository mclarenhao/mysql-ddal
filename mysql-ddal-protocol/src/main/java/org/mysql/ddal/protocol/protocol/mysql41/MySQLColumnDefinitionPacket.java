/**
 * @(#) MySQLColumnDefinitionPacket.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

import org.mysql.ddal.protocol.MySQLMessageReader;
import org.mysql.ddal.protocol.MySQLMessageWriter;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 智慧工厂
 * @see https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_query_response_text_resultset_column_definition.html#sect_protocol_com_query_response_text_resultset_column_definition_41
 *
 */
@Getter
@ToString
public class MySQLColumnDefinitionPacket extends MySQLPacket {
	private final static String DEFAULT_CATALOG = "def";
	private final String catalog;
	private final String schema;
	private final String table;
	private final String orgTable;
	private final String name;
	private final String orgName;
	private final int charsetIndex;
	private final int columnLength;
	private final int type;
	private final int flags;
	private final int decimals;

	public MySQLColumnDefinitionPacket(int packetId, String schema, String table, String orgTable, String name,
			String orgName, int charsetIndex, int columnLength, int type, int flags, int decimals) {
		super(0, packetId);
		this.catalog = DEFAULT_CATALOG;
		this.schema = schema;
		this.table = table;
		this.orgTable = orgTable;
		this.name = name;
		this.orgName = orgName;
		this.charsetIndex = charsetIndex;
		this.columnLength = columnLength;
		this.type = type;
		this.flags = flags;
		this.decimals = decimals;
	}

	/**
	 * @param buffer
	 */
	public MySQLColumnDefinitionPacket(ByteBuf buffer) {
		super(buffer);
		MySQLMessageReader reader = getReader();
		this.catalog = reader.readStringWithLength();
		this.schema = reader.readStringWithLength();
		this.table = reader.readStringWithLength();
		this.orgTable = reader.readStringWithLength();
		this.name = reader.readStringWithLength();
		this.orgName = reader.readStringWithLength();
		reader.skip(1);
		this.charsetIndex = reader.readUB2();
		this.columnLength = (int) reader.readUB4();
		this.type = reader.readUB() & 0xFF;
		this.flags = reader.readUB2();
		this.decimals = (int) reader.readUB3();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.protocol.mysql41.MySQLPacket#getMySQLMessageWriter()
	 */
	@Override
	public MySQLMessageWriter getMySQLMessageWriter() {
		MySQLMessageWriter writer = new MySQLMessageWriter();
		byte nullable = (byte) 0;
		writer.writeUB3(this.calucatePacketLength());
		writer.writeUB((byte) this.getPacketId());
		writer.writeStringWithLength(this.catalog, nullable);
		writer.writeStringWithLength(this.schema, nullable);
		writer.writeStringWithLength(table, nullable);
		writer.writeStringWithLength(orgTable, nullable);
		writer.writeStringWithLength(name, nullable);
		writer.writeStringWithLength(orgName, nullable);
		writer.writeUB((byte) 0x0c);
		writer.writeUB2(this.charsetIndex);
		writer.writeUB4(this.columnLength);
		writer.writeUB((byte) this.type);
		writer.writeUB2(this.flags);
		writer.writeUB((byte) this.decimals);
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
		int size = (catalog == null ? 1 : reader.getLength(catalog.getBytes()));
		size += (schema == null ? 1 : reader.getLength(schema.getBytes()));
		size += (table == null ? 1 : reader.getLength(table.getBytes()));
		size += (orgTable == null ? 1 : reader.getLength(orgTable.getBytes()));
		size += (name == null ? 1 : reader.getLength(name.getBytes()));
		size += (orgName == null ? 1 : reader.getLength(orgName.getBytes()));
		size += 13;// 1+2+4+1+2+1+2
		return size;
	}
}
