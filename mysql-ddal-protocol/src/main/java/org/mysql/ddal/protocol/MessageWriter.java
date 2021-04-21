/**
 * @(#) MessageWriter.java MySQL中间件
 */
package org.mysql.ddal.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author 智慧工厂@M
 *
 */
@FunctionalInterface
public interface MessageWriter {
	void writeAndFlush(ByteBuf buffer);
}
