package com.asiekierka.sixtyfour.client;
import java.util.*;
public class CraftrCanvas {
	public int GRID_W = 32;
	public int GRID_H = 25;
	public int FULLGRID_W = GRID_W;
	public int FULLGRID_H = GRID_H;
	public int WIDTH = ((FULLGRID_W)*16);
	public int HEIGHT = (FULLGRID_H*16)+8;
	public boolean resizePlayfield = true;
	public static Random rand = new Random();
	public byte cga[];
	public int palette[];
	public String chome;
	public CraftrScreen cs;
	public double scaleX;
	public double scaleY;
	public int sizeX = WIDTH;
	public int sizeY = HEIGHT;
	public CraftrCanvas(String home)
	{
		palette = new int[] {	0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
								0xAA0000, 0xAA00AA, 0xAAAA00, 0xAAAAAA,
								0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
								0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };
		cga = new byte[2048];
		chome = home;	
	}
	
	public void scale(int newX, int newY)
	{
		scaleX = (double)newX/WIDTH;
		scaleY = (double)newY/HEIGHT;
		sizeX = newX;
		sizeY = newY;
		if(resizePlayfield)
		{
			remakeVariables();
			scaleX = (double)newX/WIDTH;
			scaleY = (double)newY/HEIGHT;
		}
	}

	public void remakeVariables()
	{
		WIDTH = sizeX-(sizeX%16);
		HEIGHT = sizeY-(sizeY%16);
		if(sizeY < HEIGHT) HEIGHT-=16;
		GRID_W = (WIDTH>>4);
		FULLGRID_W = GRID_W;
		FULLGRID_H = (HEIGHT>>4);
		GRID_H = FULLGRID_H;
		if(cs!=null) cs.setCanvas(this);
	}
}
