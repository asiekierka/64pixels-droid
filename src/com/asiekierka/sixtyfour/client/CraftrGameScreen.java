package com.asiekierka.sixtyfour.client;
import com.asiekierka.sixtyfour.common.*;
public class CraftrGameScreen {
	public CraftrBlock[] blocks;

	public CraftrChatMsg[] chatarr;
	public int chatlen;
	public CraftrCanvas c;
	public CraftrPlayer players[] = new CraftrPlayer[256];
	
	public int CHATBOTTOM_X = 11;
	public int CHATBOTTOM_Y;
	public int drawType;
	public int[] drawChrA = new int[256];
	public int[] drawColA = new int[256];
	
	public boolean insideRect(int mx, int my, int x, int y, int w, int h)
	{
		if(mx >= x && my >= y && mx < x+w && my < y+h)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public void setCanvas(CraftrCanvas canvas)
	{
		c=canvas;
		if(c!=null)
		{
			blocks = new CraftrBlock[c.FULLGRID_W*c.FULLGRID_H];
			CHATBOTTOM_Y = (c.GRID_H*16)-17;
		}
	}

	public int gdrawChr()
	{
		return drawChrA[drawType<0?(0xFF&(int)((byte)drawType)):drawType];
	}
	public int gdrawCol()
	{
		return drawColA[drawType<0?(0xFF&(int)((byte)drawType)):drawType];
	}
	public void sdrawChr(int c)
	{
		drawChrA[drawType<0?(0xFF&(int)((byte)drawType)):drawType] = c;
	}
	public void sdrawCol(int c)
	{
		drawColA[drawType<0?(0xFF&(int)((byte)drawType)):drawType] = c;
	}
	public void addChatMsg(String msg)
	{
		// This fixes the case that there is TOO MUCH CHAT GOING ON BRO
		System.arraycopy(chatarr,0,chatarr,1,19);
		chatarr[0] = new CraftrChatMsg(msg);
		if(chatlen<20) chatlen++;
		
	}
	public int addPlayer(int id, int scrx, int scry, String name, byte ch, byte co)
	{
		players[id] = new CraftrPlayer(scrx,scry,ch,co,name);
		return id;
	}

	public void removePlayer(int id)
	{
		players[id] = null;
	}
}
