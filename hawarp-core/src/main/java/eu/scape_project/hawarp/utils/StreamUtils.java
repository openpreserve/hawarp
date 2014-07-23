package eu.scape_project.hawarp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class StreamUtils {
    public static InputStream newInputStream(final ByteBuffer buf) {
        return new InputStream() {
            public synchronized int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get();
            }



            public synchronized int read(byte[] bytes, int off, int len) throws IOException {
                // Read only what's left
                len = Math.min(len, buf.remaining());
                if (len == 0) {
                    return -1;
                } else {
                    buf.get(bytes, off, len);
                    return len;
                }
            }
        };
    }

    public static OutputStream newOutputStream(final ByteBuffer buf) {
        return new OutputStream() {
            public synchronized void write(int b) throws IOException {
                try {
                    buf.put((byte) b);
                } catch (BufferOverflowException e) {
                    throw new IOException(e);
                }
            }

            public synchronized void write(byte[] bytes, int off, int len) throws IOException {
                try {
                    buf.put(bytes, off, len);
                } catch (BufferOverflowException e) {
                    throw new IOException(e);
                }
            }
        };
    }
}
