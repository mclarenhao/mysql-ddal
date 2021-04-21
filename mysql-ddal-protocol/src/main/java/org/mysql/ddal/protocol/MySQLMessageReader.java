/**
 * @(#) MySQL中间件
 */
package org.mysql.ddal.protocol;

import java.io.ByteArrayOutputStream;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * MySQL消息处理
 * 
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Getter
public class MySQLMessageReader {
	public static final long NULL_LENGTH = -1;
	private final ByteBuf buffer;

	public byte readUB() {
		return buffer.readByte();
	}

	public int readUB2() {
		byte[] buff = new byte[2];
		buffer.readBytes(buff);
		int position = 0;
		int i = buff[position++] & 0xff;
		i |= (buff[position++] & 0xff) << 8;
		return i;
	}

	public int readUB3() {
		byte[] buff = new byte[3];
		buffer.readBytes(buff);
		int position = 0;
		int i = buff[position++] & 0xff;
		i |= (buff[position++] & 0xff) << 8;
		i |= (buff[position++] & 0xff) << 16;
		return i;
	}

	public long readUB4() {
		byte[] buff = new byte[4];
		buffer.readBytes(buff);
		int position = 0;
		long i = buff[position++] & 0xff;
		i |= (buff[position++] & 0xff) << 8;
		i |= (buff[position++] & 0xff) << 16;
		i |= (buff[position++] & 0xff) << 24;
		return i;
	}

	public byte[] readBytesEndwithEof() {
		byte[] buf = new byte[buffer.readableBytes()];
		buffer.readBytes(buf);
		return buf;
	}

	public byte[] readBytes(int length) {
		byte[] buf = new byte[length];
		buffer.readBytes(buf);
		return buf;
	}

	public byte[] readBytesWithNull() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (true) {
			byte b = buffer.readByte();
			if (b == 0) {
				break;
			}
			out.write(b);
		}
		return out.toByteArray();
	}

	public String readStringWithNull() {
		return new String(readBytesWithNull());
	}

	public int readInt() {
		byte[] buff = new byte[4];
		buffer.readBytes(buff);
		int position = 0;
		int i = buff[position++] & 0xff;
		i |= (buff[position++] & 0xff) << 8;
		i |= (buff[position++] & 0xff) << 16;
		i |= (buff[position++] & 0xff) << 24;
		return i;
	}

	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public long readLong() {
		final byte[] b = new byte[8];
		buffer.readBytes(b);
		int position = 0;
		long l = (long) (b[position++] & 0xff);
		l |= (long) (b[position++] & 0xff) << 8;
		l |= (long) (b[position++] & 0xff) << 16;
		l |= (long) (b[position++] & 0xff) << 24;
		l |= (long) (b[position++] & 0xff) << 32;
		l |= (long) (b[position++] & 0xff) << 40;
		l |= (long) (b[position++] & 0xff) << 48;
		l |= (long) (b[position++] & 0xff) << 56;
		return l;
	}

	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public long readLength() {
		int length = buffer.readByte() & 0xff;
		switch (length) {
		case 251:
			return NULL_LENGTH;
		case 252:
			return readUB2();
		case 253:
			return readUB3();
		case 254:
			return readLong();
		default:
			return length;
		}
	}

	public byte[] readBytesWithLength() {
		int length = (int) readLength();
		byte[] data = new byte[length];
		buffer.readBytes(data);
		return data;
	}

	public String readStringWithLength() {
		return new String(readBytesWithLength());
	}

	public void skip(int count) {
		for (int i = 0; i < count; i++) {
			readUB();
		}
	}

	public long readIntLenenc() {
		int firstByte = readUB();
		if (firstByte < 0xfb) {
			return firstByte;
		}
		if (0xfb == firstByte) {
			return 0;
		}
		if (0xfc == firstByte) {
			return getBuffer().readShortLE();
		}
		if (0xfd == firstByte) {
			return getBuffer().readMediumLE();
		}
		return getBuffer().readLongLE();
	}

	public byte[] readStringLenencByBytes() {
		int length = (int) readIntLenenc();
		byte[] result = new byte[length];
		getBuffer().readBytes(result);
		return result;
	}

	public byte[] readStringFixByBytes(final int length) {
		byte[] result = new byte[length];
		getBuffer().readBytes(result);
		return result;
	}

	public final int getLength(long length) {
		if (length < 251) {
			return 1;
		} else if (length < 0x10000L) {
			return 3;
		} else if (length < 0x1000000L) {
			return 4;
		} else {
			return 9;
		}
	}

	public final int getLength(byte[] src) {
		int length = src.length;
		if (length < 251) {
			return 1 + length;
		} else if (length < 0x10000L) {
			return 3 + length;
		} else if (length < 0x1000000L) {
			return 4 + length;
		} else {
			return 9 + length;
		}
	}

	public boolean hasRemaining() {
		return buffer.readableBytes() > 0;
	}
}
