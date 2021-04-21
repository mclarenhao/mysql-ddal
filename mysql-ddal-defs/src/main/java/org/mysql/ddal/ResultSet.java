/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal;

import org.mysql.ddal.protocol.protocol.mysql41.MySQLResultSetHeaderPacket;

/**
 * 结果集
 * 
 * @author mclaren
 *
 */
public interface ResultSet {
	MySQLResultSetHeaderPacket getHeader();
}
