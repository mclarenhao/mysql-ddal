/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal.query;

import org.mysql.ddal.protocol.protocol.MySQLCommand;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLCommandPacket;

import io.netty.buffer.ByteBuf;

/**
 * @author mclaren
 *
 */
public class MySQLQueryCommand extends MySQLCommandPacket implements MySQLCommand {
	/**
	 * @param action
	 * @param sql
	 */
	public MySQLQueryCommand(String sql) {
		super(COM_QUERY, sql);
	}

	public MySQLQueryCommand(ByteBuf buffer) {
		super(buffer);
		if (this.getAction() != COM_QUERY) {
			throw new RuntimeException("错误的查询包");
		}
	}

}
