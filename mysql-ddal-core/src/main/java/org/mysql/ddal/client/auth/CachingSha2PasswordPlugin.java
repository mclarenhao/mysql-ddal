/**
 * MySQL Distributed Data Access Layer Middleware
 * @Copyrights shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal.client.auth;

import org.mysql.ddal.auth.AuthenticationProvider;
import org.mysql.ddal.protocol.protocol.mysql41.Security;

import lombok.SneakyThrows;

/**
 * @author 智慧工厂@M
 *
 */
public class CachingSha2PasswordPlugin extends AuthenticationProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mysql.ddal.auth.AuthenticationProvider#encodePassword(byte[],
	 * byte[])
	 */
	@Override
	@SneakyThrows
	public byte[] encodePassword(byte[] rawPassword, byte[] seed) {
		return Security.scrambleCachingSha2(rawPassword, seed);
	}

}
