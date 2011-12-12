package com.asiekierka.sixtyfour.client;
import java.util.*;
import java.io.*;
import android.graphics.*;
import android.content.res.*;

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
	public Bitmap charsetImage[][];
	public Bitmap charsetImage2[][];
	public String chome;
	public CraftrScreen cs;
	public double scaleX;
	public double scaleY;
	public int sizeX = WIDTH;
	public int sizeY = HEIGHT;
	public CraftrCanvas(String home, AssetManager asset)
	{
		palette = new int[] {	0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
								0xAA0000, 0xAA00AA, 0xAAAA00, 0xAAAAAA,
								0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
								0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };
		cga = new byte[2048];
		chome = home;
		InputStream cis;
		try
		{
			cis = asset.open("rawcga.bin",AssetManager.ACCESS_BUFFER);
			cis.read(cga,0,2048);
			cis.close();
		}
		catch(Exception e)
		{
			throw new Error("rawcga derped");
		}
		RedrawCharset();
	}
	
	public void RedrawCharset()
	{
		charsetImage = new Bitmap[256][16];
		charsetImage2 = new Bitmap[256][16];
		int cgat = 0;
		int[] palcol = new int[16];
		for(int pt=0;pt<16;pt++)
				palcol[pt] = (0xFF000000 | palette[pt]);
		for(int c=0;c<256;c++)
		{
			int temp1 = (c<<3);
			if((c&31) == 0) System.out.println("[CANVAS] Preparing charset: " + ((c*100)>>8) + "%");
			for(int col=0;col<16;col++)
			{
				charsetImage[c][col] = Bitmap.createBitmap(8,8,Bitmap.Config.ARGB_8888);
				for(int t3=0;t3<8;t3++)
				{
					cgat = 255&(int)cga[temp1+t3];
					for(int t2=7;t2>=0;t2--)
					{
						if ((cgat&1) == 1 )
							charsetImage[c][col].setPixel(t2,t3, palcol[col]);
						else
							charsetImage[c][col].setPixel(t2,t3, 0);
						cgat>>=1;
					}
				}
				charsetImage2[c][col] = Bitmap.createScaledBitmap(charsetImage[c][col],16,16,false);
			}
		}
		System.out.println("[CANVAS] Preparing charset: 100% [DONE]");
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
