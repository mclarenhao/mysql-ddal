/**
 * @(#) MySQLMessageWriter.java MySQL中间件
 */
package org.mysql.ddal.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
public class MySQLMessageWriter {
	@Getter
	private final ByteBuf buffer = Unpooled.buffer();

	public void writeUB2(int i) {
		ByteBuf buffer = getBuffer();
		buffer.writeByte((byte) (i & 0xff));
		buffer.writeByte((byte) (i >> 8));
	}

	public void writeUB3(int i) {
		ByteBuf buffer = getBuffer();
		buffer.writeByte((byte) (i & 0xff));
		buffer.writeByte((byte) (i >> 8));
		buffer.writeByte((byte) (i >> 16));
	}

	public void writeUB4(int i) {
		ByteBuf buffer = getBuffer();
		buffer.writeByte((byte) (i & 0xff));
		buffer.writeByte((byte) (i >> 8));
		buffer.writeByte((byte) (i >> 16));
		buffer.writeByte((byte) (i >> 24));
	}

	public void writeInt(int i) {
		writeUB4(i);
	}

	public void writeFloat(float f) {
		writeInt(Float.floatToRawIntBits(f));
	}

	public void writeLong(long i) {
		ByteBuf buffer = getBuffer();
		buffer.writeByte((byte) (i & 0xff));
		buffer.writeByte((byte) (i >> 8));
		buffer.writeByte((byte) (i >> 16));
		buffer.writeByte((byte) (i >> 24));
		buffer.writeByte((byte) (i >> 32));
		buffer.writeByte((byte) (i >> 40));
		buffer.writeByte((byte) (i >> 48));
		buffer.writeByte((byte) (i >> 56));
	}

	public void writeDouble(double d) {
		writeLong(Double.doubleToRawLongBits(d));
	}

	public void writeBytesLength(byte[] src) {
		writeBytesLength(src.length);
		writeBytes(src);
	}

	public void writeBytesLength(long l) {
		ByteBuf buffer = getBuffer();
		if (l < 251) {
			buffer.writeByte((byte) l);
		} else if (l < 0x10000L) {
			buffer.writeByte((byte) 252);
			writeUB2((int) l);
		} else if (l < 0x1000000L) {
			buffer.writeByte((byte) 253);
			writeUB3((int) l);
		} else {
			buffer.writeByte((byte) 254);
			writeLong(l);
		}
	}

	public void writeByte(byte b) {
		writeUB(b);
	}

	public void writeUB(byte b) {
		getBuffer().writeByte(b);
	}

	public void writeBytesWithNull(byte[] bytes) {
		ByteBuf buffer = getBuffer();
		buffer.writeBytes(bytes);
		buffer.writeByte((byte) 0);
	}

	public void writeBytes(byte[] bytes) {
		getBuffer().writeBytes(bytes);
	}

	public void writeStringWithNull(String string) {
		writeBytesWithNull(string.getBytes());
	}

	public void writeStringWithLength(String string) {
		byte[] buffer = string.getBytes();
		writeInt(buffer.length);
		writeBytes(buffer);
	}

	public void writeStringWithLength(String string, byte nullable) {
		if (string == null) {
			writeByte(nullable);
			return;
		}
		byte[] buffer = string.getBytes();
		writeInt(buffer.length);
		writeBytes(buffer);
	}

	public void writeNull(int count) {
		for (int i = 0; i < count; i++) {
			writeByte((byte) 0);
		}
	}

	public final void writeLength(long l) {
		if (l < 251) {
			writeByte((byte) l);
		} else if (l < 0x10000L) {
			writeByte((byte) 252);
			writeUB2((int) l);
		} else if (l < 0x1000000L) {
			writeByte((byte) 253);
			writeUB3((int) l);
		} else {
			writeByte((byte) 254);
			writeLong(l);
		}
	}

	public final void writeWithLength(byte[] src) {
		int length = src.length;
		if (length < 251) {
			writeByte((byte) length);
		} else if (length < 0x10000L) {
			writeByte((byte) 252);
			writeUB2(length);
		} else if (length < 0x1000000L) {
			writeByte((byte) 253);
			writeUB3(length);
		} else {
			writeByte((byte) 254);
			writeLong(length);
		}
		writeBytes(src);
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

	public final void writeWithLength(byte[] src, byte nullValue) {
		if (src == null) {
			writeByte(nullValue);
		} else {
			writeWithLength(src);
		}
	}

	public void write(MessageWriter writer) {
		writer.writeAndFlush(getBuffer());
	}
}
