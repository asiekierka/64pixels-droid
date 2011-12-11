package com.asiekierka.sixtyfour.common;

public class NoChunkMemException extends Exception
{
	private static final long serialVersionUID = 1L;
	public NoChunkMemException() {}
	public NoChunkMemException(int x, int y)
	{
		super("No memory found for chunk: x=" + x + ", y=" + y + ".");
	}
}
