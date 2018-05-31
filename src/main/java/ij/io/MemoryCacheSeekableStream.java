package ij.io;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * Copyright (C) 2017 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

import java.io.IOException;
import java.io.InputStream;

import gdsc.core.utils.TurboList;

/**
 * This class uses a memory cache to allow seeking within
 * an InputStream.
 * <p>
 * Entirely based on ij.io.RandomAccessStream which is itself based on the JAI MemoryCacheSeekableStream class.
 */
public final class MemoryCacheSeekableStream extends SeekableStream
{
	private static final int BLOCK_SIZE = 1024;
	private static final int BLOCK_MASK = 1023;
	private static final int BLOCK_SHIFT = 10;
	private InputStream src;
	private long pointer;
	private TurboList<byte[]> data;
	private long length;
	private boolean foundEOS;

	/**
	 * Constructs a MemoryCacheSeekableStream from an InputStream. Seeking
	 * backwards is supported using a memory cache.
	 *
	 * @param inputstream
	 *            the inputstream
	 */
	public MemoryCacheSeekableStream(InputStream inputstream)
	{
		if (inputstream == null)
			throw new NullPointerException();
		pointer = 0L;
		data = new TurboList<byte[]>();
		length = 0L;
		foundEOS = false;
		src = inputstream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.io.SeekableStream#getFilePointer()
	 */
	public long getFilePointer() throws IOException
	{
		return pointer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.io.SeekableStream#read()
	 */
	public int read() throws IOException
	{
		long l = pointer + 1L;
		long l1 = readUntil(l);
		if (l1 >= l)
		{
			byte abyte0[] = data.get((int) (pointer >> BLOCK_SHIFT));
			return abyte0[(int) (pointer++ & BLOCK_MASK)] & 0xff;
		}
		else
			return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.io.SeekableStream#read(byte[], int, int)
	 */
	public int read(byte[] bytes, int off, int len) throws IOException
	{
		if (bytes == null)
			throw new NullPointerException();
		if (off < 0 || len < 0 || off + len > bytes.length)
			throw new IndexOutOfBoundsException();
		if (len == 0)
			return 0;
		long l = readUntil(pointer + len);
		if (l <= pointer)
			return -1;
		else
		{
			byte abyte1[] = data.get((int) (pointer >> BLOCK_SHIFT));
			int k = Math.min(len, BLOCK_SIZE - (int) (pointer & BLOCK_MASK));
			System.arraycopy(abyte1, (int) (pointer & BLOCK_MASK), bytes, off, k);
			pointer += k;
			return k;
		}
	}

	/**
	 * Read until the given location.
	 *
	 * @param l
	 *            the location
	 * @return the long
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private long readUntil(long l) throws IOException
	{
		if (l < length)
			return l;
		if (foundEOS)
			return length;
		int i = (int) (l >> BLOCK_SHIFT);
		int j = (int) (length >> BLOCK_SHIFT);
		for (int k = j; k <= i; k++)
		{
			byte abyte0[] = new byte[BLOCK_SIZE];
			data.add(abyte0);
			int i1 = BLOCK_SIZE;
			int j1 = 0;
			while (i1 > 0)
			{
				int k1 = src.read(abyte0, j1, i1);
				if (k1 == -1)
				{
					foundEOS = true;
					return length;
				}
				j1 += k1;
				i1 -= k1;
				length += k1;
			}
		}
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.io.SeekableStream#seek(long)
	 */
	public void seek(long loc) throws IOException
	{
		if (loc < 0L)
			throw new IOException("Location is negative");
		pointer = loc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.io.SeekableStream#close()
	 */
	public void close() throws IOException
	{
		data.clear();
		src.close();
	}
}