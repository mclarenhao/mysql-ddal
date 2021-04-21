/**
 * @(#) MySQLCapabilityFlags.java MySQL中间件
 */
package org.mysql.ddal.protocol.protocol.mysql41;

/**
 * @author win10
 *
 */
public interface MySQLCapabilityFlags {
	/**
	 * Use the improved version of Old Password Authentication. More...
	 */
	public static final int CLIENT_LONG_PASSWORD = 1;

	/**
	 * Send found rows instead of affected rows in EOF_Packet. More...
	 */
	public static final int CLIENT_FOUND_ROWS = 2;

	/**
	 * Get all column flags. More...
	 */
	public static final int CLIENT_LONG_FLAG = 4;

	/**
	 * Database (schema) name can be specified on connect in Handshake Response
	 * Packet. More...
	 */
	public static final int CLIENT_CONNECT_WITH_DB = 8;

	/**
	 * Don't allow database.table.column. More...
	 */
	public static final int CLIENT_NO_SCHEMA = 16;

	/**
	 * Compression protocol supported. More...
	 */
	public static final int CLIENT_COMPRESS = 32;

	/**
	 * Special handling of ODBC behavior. More...
	 */
	public static final int CLIENT_ODBC = 64;

	/**
	 * Can use LOAD DATA LOCAL. More...
	 */
	public static final int CLIENT_LOCAL_FILES = 128;

	/**
	 * Ignore spaces before '('. More...
	 */
	public static final int CLIENT_IGNORE_SPACE = 256;

	/**
	 * New 4.1 protocol. More...
	 */
	public static final int CLIENT_PROTOCOL_41 = 512;

	/**
	 * This is an interactive client. More...
	 */
	public static final int CLIENT_INTERACTIVE = 1024;

	/**
	 * Use SSL encryption for the session. More...
	 */
	public static final int CLIENT_SSL = 2048;

	/**
	 * Client only flag. More...
	 */
	public static final int CLIENT_IGNORE_SIGPIPE = 4096;

	/**
	 * Client knows about transactions. More...
	 */
	public static final int CLIENT_TRANSACTIONS = 8192;

	/**
	 * DEPRECATED: Old flag for 4.1 protocol. More...
	 */
	public static final int CLIENT_RESERVED = 16384;

	/**
	 * DEPRECATED: Old flag for 4.1 authentication \ CLIENT_SECURE_CONNECTION.
	 * More...
	 */
	public static final int CLIENT_RESERVED2 = 32768;

	/**
	 * Enable/disable mLti-stmt support. More...
	 */
	public static final long CLIENT_MLTI_STATEMENTS = (1L << 16);

	/**
	 * Enable/disable mLti-resLts. More...
	 */
	public static final long CLIENT_MLTI_RESLTS = (1L << 17);

	/**
	 * MLti-resLts and OUT parameters in PS-protocol. More...
	 */
	public static final long CLIENT_PS_MLTI_RESLTS = (1L << 18);

	/**
	 *
	 */
	public static final long CLIENT_PLUGIN_AUTH = (1L << 19);

	/**
	 * Client supports connection attributes. More...
	 */
	public static final long CLIENT_CONNECT_ATTRS = (1L << 20);

	/**
	 * Enable authentication response packet to be larger than 255 bytes. More...
	 */
	public static final long CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = (1L << 21);

	/**
	 * Don't close the connection for a user account with expired password. More...
	 */
	public static final long CLIENT_CAN_HANDLE_EXPIRED_PASSWORDS = (1L << 22);

	/**
	 * Capable of handling server state change information. More...
	 */
	public static final long CLIENT_SESSION_TRACK = (1L << 23);

	/**
	 * Client no longer needs EOF_Packet and will use OK_Packet instead. More...
	 */
	public static final long CLIENT_DEPRECATE_EOF = (1L << 24);

	/**
	 * Verify server certificate. More...
	 */
	public static final long CLIENT_SSL_VERIFY_SERVER_CERT = (1L << 30);

	/**
	 * The client can handle optional metadata information in the resLtset. More...
	 */
	public static final long CLIENT_OPTIONAL_RESLTSET_METADATA = (1L << 25);

	/**
	 * Compression protocol extended to support zstd compression method. More...
	 */
	public static final long CLIENT_ZSTD_COMPRESSION_ALGORITHM = (1L << 26);

	/**
	 * This flag will be reserved to extend the 32bit capabilities structure to
	 * 64bits. More...
	 */
	public static final long CLIENT_CAPABILITY_EXTENSION = (1L << 29);

	/**
	 * Don't reset the options after an unsuccessfL connect. More...
	 */
	public static final long CLIENT_REMEMBER_OPTIONS = (1L << 31);
}
