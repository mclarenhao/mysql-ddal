/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mclaren
 *
 */
@RequiredArgsConstructor
@Getter
public class AuthData {
	private final byte[] authPluginDataPart1;
	private final byte[] authPluginDataPart2;

	public final byte[] seed() {
		byte[] seed = new byte[authPluginDataPart1.length + authPluginDataPart2.length];
		System.arraycopy(authPluginDataPart1, 0, seed, 0, authPluginDataPart1.length);
		System.arraycopy(authPluginDataPart2, 0, seed, authPluginDataPart1.length, authPluginDataPart2.length);
		return seed;
	}
}
