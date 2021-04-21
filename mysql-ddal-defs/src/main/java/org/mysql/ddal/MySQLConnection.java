/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal;

import java.nio.charset.Charset;

import org.mysql.ddal.protocol.protocol.mysql41.MySQLCapabilityFlags;

import lombok.Getter;
import lombok.Setter;

/**
 * MySQL连接
 * 
 * @author mclaren
 *
 */
@Getter
public abstract class MySQLConnection implements Connection {
	private final String hostname;
	private final int port;
	private final String username;
	private final String password;
	private final String databaseName;
	private final String charsetName;

	@Setter
	private boolean closed = true;

	public final int CAPABILITIES = (int) (MySQLCapabilityFlags.CLIENT_PLUGIN_AUTH
			| MySQLCapabilityFlags.CLIENT_PROTOCOL_41 | MySQLCapabilityFlags.CLIENT_CONNECT_WITH_DB);

	public MySQLConnection(String hostname, int port, String username, String password) {
		this(hostname, port, username, password, null, Charset.defaultCharset().toString());
	}

	public MySQLConnection(String hostname, int port, String username, String password, String databaseName,
			String charsetName) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.databaseName = databaseName;
		this.charsetName = charsetName;

		init();
		doNativeConnect();
	}

	public abstract void doNativeConnect();

	protected void init() {

	}
}
