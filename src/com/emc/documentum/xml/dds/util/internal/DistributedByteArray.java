/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.util.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DistributedByteArray {
    private static final int DEFAULT_BYTE_ARRAY_SIZE = 16384;
    private final List<byte[]> arrayList = new ArrayList<byte[]>();
    private final int byteArraySize;
    private long size;

    public DistributedByteArray() {
        this(16384);
    }

    public DistributedByteArray(int byteArraySize) {
        this.byteArraySize = byteArraySize;
        this.size = 0;
    }

    public void clear() {
        if (this.arrayList != null) {
            this.arrayList.clear();
            this.size = 0;
        }
    }

    public long size() {
        return this.size;
    }

    public Reader getReader() {
        return new Reader(){
            private long mark;
            private long position;

            @Override
            public int read() {
                int result = DistributedByteArray.this.doRead(this.position);
                if (result != -1) {
                    ++this.position;
                }
                return result;
            }

            @Override
            public void close() {
            }

            @Override
            public boolean ready() {
                return this.position < DistributedByteArray.this.size;
            }

            @Override
            public long skip(long n) {
                long skipped = Math.min(n, DistributedByteArray.this.size - this.position);
                this.position += skipped;
                return skipped;
            }

            @Override
            public int read(char[] cbuf) {
                return this.read(cbuf, 0, cbuf.length);
            }

            @Override
            public int read(char[] cbuf, int off, int len) {
                byte[] data = new byte[cbuf.length];
                int read = DistributedByteArray.this.doRead(this.position, data, off, len);
                if (read > 0) {
                    this.position += (long)read;
                }
                new String(data, StandardCharsets.UTF_8).getChars(off, len, cbuf, off);
                return read;
            }

            @Override
            public boolean markSupported() {
                return true;
            }

            @Override
            public void mark(int readlimit) {
                this.mark = this.position;
            }

            @Override
            public void reset() {
                this.position = this.mark;
            }
        };
    }

    public Writer getWriter() {
        return new Writer(){

            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void write(int c) {
                DistributedByteArray.this.doWrite(c);
            }

            @Override
            public void write(char[] cbuf) {
                this.write(cbuf, 0, cbuf.length);
            }

            @Override
            public void write(char[] cbuf, int off, int len) {
                DistributedByteArray.this.doWrite(new String(cbuf).getBytes(StandardCharsets.UTF_8), off, len);
            }

            @Override
            public void write(String str) {
                this.write(str, 0, str.length());
            }

            @Override
            public void write(String str, int off, int len) {
                DistributedByteArray.this.doWrite(str.getBytes(StandardCharsets.UTF_8), off, len);
            }
        };
    }

    public InputStream getInputStream() {
        return new InputStream(){
            private long mark;
            private long position;

            @Override
            public int read() {
                int result = DistributedByteArray.this.doRead(this.position);
                if (result != -1) {
                    ++this.position;
                }
                return result;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                int count = DistributedByteArray.this.doRead(this.position, b, off, len);
                if (count > 0) {
                    this.position += (long)count;
                }
                return count;
            }

            @Override
            public long skip(long n) {
                long skipped = Math.min(n, DistributedByteArray.this.size - this.position);
                this.position += skipped;
                return skipped;
            }

            @Override
            public boolean markSupported() {
                return true;
            }

            @Override
            public void mark(int readlimit) {
                this.mark = this.position;
            }

            @Override
            public void reset() {
                this.position = this.mark;
            }
        };
    }

    public OutputStream getOutputStream() {
        return new OutputStream(){

            @Override
            public void write(int b) {
                DistributedByteArray.this.doWrite(b);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                DistributedByteArray.this.doWrite(b, off, len);
            }
        };
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        Iterator<byte[]> iterator = this.arrayList.iterator();
        int max = (int)(this.size / (long)this.byteArraySize);
        for (int i = 0; i < max; ++i) {
            outputStream.write(iterator.next());
        }
        outputStream.write(iterator.next(), 0, (int)(this.size % (long)this.byteArraySize));
    }

    public String toString() {
        if (this.size == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer((int)this.size);
        int max = (int)(this.size / (long)this.byteArraySize);
        for (int i = 0; i < max; ++i) {
            try {
                buffer.append(new String(this.arrayList.get(i), "UTF-8"));
                continue;
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        int len = (int)(this.size % (long)this.byteArraySize);
        if (len != 0) {
            try {
                buffer.append(new String(this.arrayList.get(max), 0, len, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return buffer.toString();
    }

    private int doRead(long position) {
        byte[] data = this.getInputArray(position);
        if (data == null) {
            return -1;
        }
        return data[(int)(position % (long)this.byteArraySize)] & 255;
    }

    private int doRead(long position, byte[] buffer, int off, int len) {
        if (off < 0 || len < 0 || off + len > buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.size == 0 || position >= this.size) {
            return -1;
        }
        int count = (int)Math.min(this.size - position, (long)len);
        long max = position + (long)count;
        long pos = position;
        int offset = off;
        while (pos < max) {
            byte[] data = this.getInputArray(pos);
            int arrayPosition = (int)(pos % (long)this.byteArraySize);
            int copyCount = Math.min(count, this.byteArraySize - arrayPosition);
            System.arraycopy(data, arrayPosition, buffer, offset, copyCount);
            offset += copyCount;
            pos += (long)copyCount;
            count -= copyCount;
        }
        return (int)(pos - position);
    }

    private void doWrite(int b) {
        this.getOutputArray()[(int)(this.size % (long)this.byteArraySize)] = (byte)b;
        ++this.size;
    }

    private void doWrite(byte[] buffer, int off, int len) {
        if (off < 0 || len < 0 || off + len > buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        int length = len;
        int offset = off;
        while (length > 0) {
            byte[] data = this.getOutputArray();
            int arrayPosition = (int)(this.size % (long)this.byteArraySize);
            int copyCount = Math.min(length, this.byteArraySize - arrayPosition);
            System.arraycopy(buffer, offset, data, arrayPosition, copyCount);
            length -= copyCount;
            offset += copyCount;
            this.size += (long)copyCount;
        }
    }

    private byte[] getInputArray(long position) {
        if (this.size == 0 || position >= this.size) {
            return null;
        }
        int listPosition = (int)(position / (long)this.byteArraySize);
        return this.arrayList.get(listPosition);
    }

    private byte[] getOutputArray() {
        int listPosition = (int)(this.size / (long)this.byteArraySize);
        if (listPosition == this.arrayList.size()) {
            byte[] data = new byte[this.byteArraySize];
            this.arrayList.add(data);
            return data;
        }
        return this.arrayList.get(listPosition);
    }

}

