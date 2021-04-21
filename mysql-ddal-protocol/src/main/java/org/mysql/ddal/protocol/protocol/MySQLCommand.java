/**
 * 
 */
package org.mysql.ddal.protocol.protocol;

/**
 * @author win10
 *
 */
public interface MySQLCommand {
	int COM_SLEEP = 0x00;
	int COM_QUIT = 0x01;
	int COM_INIT_DB = 0x02;
	int COM_QUERY = 0x03;
	int COM_FIELD_LIST = 0x04;
	int COM_CREATE_DB = 0x05;
	int COM_DROP_DB = 0x06;
	int COM_REFRESH = 0x07;
	int COM_SHUTDOWN = 0x08;
	int COM_STATISTICS = 0x09;
	int COM_PROCESS_INFO = 0x0a;
	int COM_CONNECT = 0x0b;
	int COM_PROCESS_KILL = 0x0c;
	int COM_DEBUG = 0x0d;
	int COM_PING = 0x0e;
	int COM_TIME = 0x0f;
	int COM_DELAYED_INSERT = 0x10;
	int COM_CHANGE_USER = 0x11;
	int COM_BINLOG_DUMP = 0x12;
	int COM_TABLE_DUMP = 0x13;
	int COM_CONNECT_OUT = 0x14;
	int COM_REGISTER_SLAVE = 0x15;
	int COM_STMT_PREPARE = 0x16;
	int COM_STMT_EXECUTE = 0x17;
	int COM_STMT_SEND_LONG_DATA = 0x18;
	int COM_STMT_CLOSE = 0x19;
	int COM_STMT_RESET = 0x1a;
	int COM_SET_OPTION = 0x1b;
	int COM_STMT_FETCH = 0x1c;
}
