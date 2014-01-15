/**
 * This Code is taken from google's volley
 */
package com.flyn.volcano;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PoolingByteArrayOutputStream extends ByteArrayOutputStream
{
    private static final int    DEFAULT_SIZE = 256;

    private final ByteArrayPool mPool;

    public PoolingByteArrayOutputStream(ByteArrayPool pool)
    {
        this(pool, DEFAULT_SIZE);
    }

    public PoolingByteArrayOutputStream(ByteArrayPool pool, int size)
    {
        this.mPool = pool;
        this.buf = this.mPool.getBuf(Math.max(size, DEFAULT_SIZE));

    }

    @Override
    public void close() throws IOException
    {
        this.mPool.returnBuf(this.buf);
        this.buf = null;
        super.close();
    }

    @Override
    public void finalize()
    {
        this.mPool.returnBuf(this.buf);
    }

    private void expand(int i)
    {
        if (this.count + i <= this.buf.length)
        {
            return;
        }
        byte[] newbuf = this.mPool.getBuf((this.count + i) * 2);
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        this.mPool.returnBuf(this.buf);
        this.buf = newbuf;
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int len)
    {
        expand(len);
        super.write(buffer, offset, len);
    }

    @Override
    public synchronized void write(int oneByte)
    {
        expand(1);
        super.write(oneByte);
    }
}