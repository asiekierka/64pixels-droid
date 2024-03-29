package com.asiekierka.sixtyfour.common;

import java.util.Random;

public class CraftrBlock
{
	public int x = 0;
	public int y = 0;
	private byte[] block = new byte[8];
	public static final int maxType = 22;
	public static final int invalidTypes = 2;
	public static Random rand = new Random();

	public CraftrBlock()
	{
	}
	public CraftrBlock(int ax, int ay)
	{
		this(ax,ay,0,0,0,0);
	}
	public CraftrBlock(int ax, int ay, int at, int ach, int aco)
	{
		this(ax,ay,at,0,ach,aco);
	}
	public CraftrBlock(int ax, int ay, int at, int ap, int ach, int aco)
	{
		x=ax;
		y=ay;
		block[0]=(byte)at;
		block[1]=(byte)ap;
		if(block[0]==(byte)-1)
		{
			block[4]=(byte)ach;
			block[5]=(byte)aco;
		}
		else
		{
			block[2]=(byte)ach;
			block[3]=(byte)aco;
		}
	}
	public CraftrBlock(int ax, int ay, byte[] bd)
	{
		x=ax;
		y=ay;
		block=bd;
	}
	
	public static int getParam(int type)
	{
		switch(type)
		{
			case 21:
				return rand.nextInt(10);
			case 22:
				return (rand.nextInt(7)<<4)|rand.nextInt(12);
			default:
				return 0;
		}
	}

	public boolean isEmpty()
	{
		if(isPushable()) return false;
		if(getBullet()==5 || getBullet()==6) return false;
		if(block[0]==0 || block[0]==2 || block[0]==5 || (block[0]==6 && (0x80&(int)block[1])!=0) || block[0]==8 || (block[0]>=21 && block[0]<=23)) return true;
		return false;
	}

	public boolean isPistonEmpty()
	{
		if(block[0]==0) return true;
		return isPushable();
	}

	public boolean isWiriumNeighbour()
	{
		if((block[0]>=2 && block[0]<=7) || (block[0]>=9 && block[0]<=13) || block[0]==15 || block[0]==17 || block[0]==20) return true;
		return false;
	}

	public boolean isPushable()
	{
		return block[5]!=0;
	}

	public boolean isBullet()
	{
		return block[6]!=0;
	}
	public boolean isPistonable()
	{
		return (isPushable() || !(block[0]==0 || block[0]==19 || block[0]==18 || block[0]==16 || (block[0]==17 && (0x01&block[1])!=0)));
	}

	public int getBullet()
	{
		return 0xFF&(int)block[6];
	}

	public void setBullet(byte t)
	{
		block[6]=t;
	}

	public int getBulletParam()
	{
		return 0xFF&(int)block[7];
	}

	public void setBulletParam(byte t)
	{
		block[7] = t;
	}

	public int getType()
	{
		return 0xFF&(int)block[0];
	}

	public int getTypeWithVirtual()
	{
		if(isPushable()) return -1;
		return 0xFF&(int)block[0];
	}

	public int getParam()
	{
		return 0xFF&(int)block[1];
	}

	public int getChar()
	{
		if(block[5]!=0) return 0xFF&(int)block[4];
		return 0xFF&(int)block[2];
	}

	public int getColor()
	{
		if(block[5]!=0) return 0xFF&(int)block[5];
		return 0xFF&(int)block[3];
	}

	public int getBlockChar()
	{
		return 0xFF&(int)block[2];
	}

	public int getBlockColor()
	{
		return 0xFF&(int)block[3];
	}

	public void setType(int type)
	{
		block[0]=(byte)type;
	}

	public void setParam(int param)
	{
		block[1]=(byte)param;
	}
	public void setChar(int ch)
	{
		if(isPushable()) block[4]=(byte)ch;
		else block[2]=(byte)ch;
	}
	public void setColor(int co)
	{
		if(isPushable()) block[5]=(byte)co;
		else block[3]=(byte)co;
	}

	public void setPChar(int ch)
	{
		block[4]=(byte)ch;
	}
	public void setPColor(int co)
	{
		block[5]=(byte)co;
	}


	public int getDrawnChar()
	{
		if(block[0]==8) return 0xFF&(int)block[2];
		else if(block[5]!=0) return 0xFF&(int)block[4];
		else return 0xFF&(int)block[2];
	}
	public int getDrawnColor()
	{
		if(block[0]==8) return 0xFF&(int)block[3];
		else if(block[5]!=0) return 0xFF&(int)block[5];
		else return 0xFF&(int)block[3];
	}

	public static int getBDSize()
	{
		return 8;
	}

	public byte[] getBlockData()
	{
		return block;
	}

	public static boolean isPlaceable(int t)
	{
		return !(t == 16 || t==18);
	}

	public boolean isPlaceable()
	{
		return isPlaceable(0xFF&(int)block[0]);
	}

	public static String getName(int t)
	{
		switch(t)
		{
			case 0:
				return "Floor";
			case 1:
				return "Wall";
			case 2:
				return "Wirium";
			case 3:
				return "P-NAND";
			case 4:
				return "Crossuh";
			case 5:
				return "Plate";
			case 6:
				return "Door";
			case 7:
				return "Meloder";
			case 8:
				return "Roofy";
			case 9:
				return "Pensor";
			case 10:
				return "Pumulty";
			case 11:
				return "Bodder";
			case 12:
				return "Cannona";
			case 13:
				return "Bullsor";
			case 14:
				return "Break";
			case 15:
				return "Extend";
			case 16:
				return "Opium";
			case 17:
				return "Pusher";
			case 18:
				return "PusherH";
			case 19:
				return "Stop";
			case 20:
				return "Dupe";
			case 21:
				return "Bear";
			case 22:
				return "Tiger";
			case -1:
				return "Pushium";
		}
		return "???????";
	}
	public static String getLongName(int t)
	{
		switch(t)
		{
			case 3:
				return "Powered-NAND";
			case 15:
				return "Extender";
			case 16:
				return "Gee's linctus";
			case 18:
				return "Pusher Head";
			case 20:
				return "Duplicator";
		}
		return getName(t);
	}
}
