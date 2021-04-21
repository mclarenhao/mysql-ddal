/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal.auth;

import org.mysql.ddal.SPIFinder;

/**
 * 认证处理器
 * 
 * @author mclaren
 *
 */
public abstract class AuthenticationProvider {
	public static AuthenticationProvider getAuthenticationProvider(String pluginName) {
		return SPIFinder.find("authentication/plugins/" + pluginName, AuthenticationProvider.class, null);
	}

	public byte[] encodePassword(String rawPassword, byte[] seed) {
		return encodePassword(rawPassword.getBytes(), seed);
	}

	public abstract byte[] encodePassword(byte[] rawPassword, byte[] seed);
}
