/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal;

import org.mysql.ddal.query.MySQLQueryCommand;
import org.mysql.ddal.query.MySQLQueryHandler;

/**
 * 连接对象
 * 
 * @author mclaren
 *
 */
public interface Connection {
	/**
	 * 获得当前的序列号
	 * 
	 * @return
	 */
	public int getCurrentPacketSequenceNumber();

	/**
	 * 执行查询
	 * 
	 * @param command
	 * @return
	 */
	void executeQuery(MySQLQueryCommand command, MySQLQueryHandler handler);

	void close();
}
