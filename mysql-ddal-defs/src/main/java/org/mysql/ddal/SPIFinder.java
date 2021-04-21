/**
 * MYSQL Distributed Data Access Layer Middleware
 * @Copyrigth shenzhen i-indos tech 2019-2020
 */
package org.mysql.ddal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mclaren
 *
 */
@Slf4j
public final class SPIFinder {
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public static <T> T find(String name, Class<T> type, T asDefault) {
		@Cleanup
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("META-INF/services/" + name);
		if (in == null) {
			log.error("could not find any provider implementation with: META-INF/services/" + name);
			return asDefault;
		}

		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while ((line = reader.readLine()) != null) {
			/**
			 * 忽略注释
			 */
			if (line.startsWith("#") || line.startsWith("--")) {
				continue;
			}
			break;
		}

		return (T) Thread.currentThread().getContextClassLoader().loadClass(line.trim()).newInstance();
	}
}
