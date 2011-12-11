package com.asiekierka.sixtyfour.client;
import com.asiekierka.sixtyfour.common.*;

public abstract class CraftrScreen
{
	public CraftrCanvas c;
	public CraftrScreen(CraftrCanvas cc)
	{
		c = cc;
	}
	public CraftrScreen()
	{
	
	}
	public abstract void paint();
	public void setCanvas(CraftrCanvas canvas)
	{
		c=canvas;
	}
}
