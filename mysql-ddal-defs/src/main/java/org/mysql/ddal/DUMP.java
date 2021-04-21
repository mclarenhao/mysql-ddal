/**
 * @(#) DUMP.java MySQL中间件
 */
package org.mysql.ddal;

import io.netty.buffer.ByteBuf;

/**
 * @author win10
 *
 */
public class DUMP {
	public static void dump(String name, ByteBuf buf) {
		ByteBuf copys = buf.copy();
		int total = copys.readableBytes();
		System.out.print("DUMP [" + name + "] ");
		for (int i = 0; i < total; i++) {
			System.out.print((0xff & copys.readByte()) + "\t");
		}
		System.out.println();
		System.out.println("Total:" + total + " bytes");
	}
}
