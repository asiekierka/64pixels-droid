package com.asiekierka.sixtyfour.client;
import com.asiekierka.sixtyfour.common.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class CraftrGame
implements CraftrGameShim {
	private static final byte[] extendDir = { 30, 31, 16, 17 };
	public static Random rand = new Random();
	public CraftrMap map;
	public CraftrPlayer players[] = new CraftrPlayer[256];
	public CraftrGameScreen gs;
	public CraftrCanvas canvas;
	
	public boolean hasShot;
	public boolean blockChange = false;
	public boolean playerChange = false;
	public boolean mouseChange = false;
	public boolean netChange = false;
	public boolean multiplayer;
	public boolean isKick = false;
	public boolean isShift = false; // Shooting
	public CraftrMapThread cmt;
	public int cmtsp=30;
	public int overhead=0;
	public int health=5;
	public CraftrConfig config;
	public CraftrNet net;
	public String isKickS;
	public boolean skipConfig = false;
	public boolean muted = false;
	public boolean raycasting = false;
	public boolean gameOn = false;
	public int netThreadRequest = 0;
	private Date overdate;
	private Date told = new Date();
	private Date tnew;
	
	private int fps = 0;
	private long frame = 0;
	private long fold = 0;
	private int waitTime = 0;
	private int nagle=0;
	private CraftrGameThread gt;
	public CraftrGame()
	{
		gameOn = true;
		map = new CraftrMap(false,64);
		map.game = this;
		map.saveDir = ""; // TODO: Figure out the way Android saves things.
		players[255] = new CraftrPlayer(0,0);
		canvas = new CraftrCanvas();
		if(cmtsp>0) cmt.speed=(1000/cmtsp);
		else cmt.speed=0;
	}
	public String escapeSlashes(String orig)
	{
		char[] temp = orig.toCharArray();
		String newS = "";
		for(int i=0;i<temp.length;i++)
		{
			if(temp[i]=='\\') i++;
			if(i<temp.length) newS+=temp[i];
		}
		return newS;
	}
	public boolean fetchSList()
	{
		URL u1;
		InputStream is = null;
		FileOutputStream fos;
		try
		{
			u1 = new URL("http://admin.64pixels.org/serverlist.php?asie=1");
			is = u1.openStream();
			fos = new FileOutputStream(map.saveDir + "slist.txt");
			int count = 1;
			while(count>0)
			{
				byte[] t = new byte[64];
				count=is.read(t,0,64);
				if(count>0)
				{
					System.out.println("read " + count + " bytes");
					fos.write(t,0,count);
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); return false;}
		finally { try{is.close();}catch(Exception e){} }
		return true;
	}
	public void addChatMsg(String msg)
	{
		gs.addChatMsg(msg);
	}
	public static String getVersion()
	{
		return CraftrVersion.getVersionName();
	}
	public void kickOut(String msg)
	{
		isKick=true;
		isKickS=msg;
	}
	public void kill()
	{
		
	}
	public void playSample(int tx, int ty, int val)
	{
	}
	public void playSound(int tx, int ty, int val)
	{
		if(muted) return;
		if(val>=256)
		{
			playSample(tx,ty,val-256);
			return;
		}
	}
	public void setHealth(int hp)
	{
		health=hp;
	}
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
	public void shoot(int dir)
	{
		int sx=players[255].px+map.xMovement[dir];
		int sy=players[255].py+map.yMovement[dir];
		map.setBullet(sx,sy,(byte)(dir+1));
		blockChange=true;
		if(multiplayer)
		{
			net.shoot(sx,sy,(dir+1));
		}
		else
		{
			map.physics.addBlockToCheck(new CraftrBlockPos(sx,sy));
			for(int i=0;i<4;i++) map.physics.addBlockToCheck(new CraftrBlockPos(sx+map.xMovement[i],sy+map.yMovement[i]));
		}
	}
	public int movePlayer(int dpx, int dpy)
	{
		if(hasShot) return waitTime;
		int px = players[255].px+dpx;
		int py = players[255].py+dpy;
		CraftrBlock blockMoveTo=map.getBlock(px,py);
		if(isShift && blockMoveTo.isEmpty())
		{
			for(int i=0;i<4;i++)
			{
				int tx = players[255].px+map.xMovement[i];
				int ty = players[255].py+map.yMovement[i];
				if(tx==px && ty==py)
				{
					shoot(i);
					hasShot = true;
					waitTime=9;
				}
			}
		}
		else if(map.pushAttempt(px,py,dpx,dpy))
		{
			if(multiplayer)
			{
				net.playerPush(dpx,dpy);
			} else {
				map.setPlayer(players[255].px,players[255].py,0);
				map.setPlayer(px,py,1);
				map.setPlayer(px+dpx,py+dpy,1);
				players[255].move(px,py);
				playerChange = true;
			}
			return 2;
		}
		else if(blockMoveTo.isEmpty())
		{
			if(multiplayer) net.playerMove(dpx,dpy);
			else
			{
				map.setPlayer(players[255].px,players[255].py,0);
				map.setPlayer(px,py,1);
			}
			players[255].move(px,py);
			playerChange = true;
			return 2;
 		}
		return waitTime;
	}
	public void spawnPlayer(int cx, int cy, int id)
	{
		CraftrChunk pc;
		try
		{
			pc = map.grabChunk(cx,cy);
		}
		catch(Exception e)
		{
			System.out.println("spawnPlayer: no chunk memory found, most likely");
			return;
		}
		for(int i=0;i<64;i++)
		{
			for(int j=0;j<64;j++)
			{
				if(pc.getBlockType(i,j) == 0) { players[id].px = cx+i; players[id].py = cy+j; return; }
			}
		}
	}
	
	public void render()
	{
		int px = players[255].px;
		int py = players[255].py;
		int sx = px-(canvas.FULLGRID_W/2)+1;
		int sy = py-(canvas.FULLGRID_H/2)+1;
		try
		{
			if (!raycasting)
			{
				for(int iy=0;iy<canvas.FULLGRID_H;iy++)
				{
					for(int ix=0;ix<canvas.FULLGRID_W;ix++)
					{
						gs.blocks[(iy*canvas.FULLGRID_W)+ix] = map.getBlock(ix+sx,iy+sy);
					}
				}
			}
			else
			{
				for(int iy=0;iy<canvas.FULLGRID_H;iy++)
				{
					for(int ix=0;ix<canvas.FULLGRID_W;ix++)
					{
						gs.blocks[(iy*canvas.FULLGRID_W)+ix] = null;
					}
				}
				// this is the recursive route.
				gs.blocks[(((canvas.FULLGRID_H/2)-1)*canvas.FULLGRID_W)+(canvas.FULLGRID_W/2)-1] = map.getBlock(px,py);
				castRayPillars(px,py,-1, 0,-1,-1,-1, 1,(canvas.FULLGRID_W/2)+2);
				castRayPillars(px,py, 1, 0, 1,-1, 1, 1,(canvas.FULLGRID_W/2)+2);
				castRayPillars(px,py, 0,-1,-1,-1, 1,-1,(canvas.FULLGRID_H/2)+2);
				castRayPillars(px,py, 0, 1,-1, 1, 1, 1,(canvas.FULLGRID_H/2)+2);
			}
			for (int i=0;i<256;i++)
			{
				if(players[i] == null)
				{
					gs.removePlayer(i);
					continue;
				}
				int tx = (players[i].px-players[255].px)+(canvas.FULLGRID_W/2)-1;
				int ty = (players[i].py-players[255].py)+(canvas.FULLGRID_H/2)-1;
				gs.removePlayer(i);
				if(tx>=0 && ty>=0 && tx<canvas.FULLGRID_W && ty<canvas.FULLGRID_H && gs.blocks[(ty*canvas.FULLGRID_W)+tx] != null)
				{
					CraftrBlock blockAtPlayer = map.getBlock(players[i].px,players[i].py);
					if(blockAtPlayer.getType()!=8) gs.addPlayer(i,tx,ty,players[i].name,players[i].pchr,players[i].pcol);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("[SEVERE] render exception: " + e.toString() + " | " + e.getMessage() + " | " + e.getCause());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void castRayPillars(int sx, int sy, int dx, int dy, int x1, int y1, int x2, int y2, int maxtrace)
	{
		assert(x1 <= x2 && y1 <= y2);
		
		// TODO: make it aim for the block corners.
		//       that way we can get the FULL visibility,
		//       rather than a slightly clipped one.
		
		int ox1 = x1;
		int oy1 = y1;
		int ox2 = x2;
		int oy2 = y2;
		
		
		int adx = (dx < 0 ? -dx : dx);
		int ady = (dy < 0 ? -dy : dy);
		
		while(maxtrace > 0)
		{
			//System.out.printf("maxtrace %d %d %d: %d %d -> %d %d\n", maxtrace, dx, dy, x1, y1, x2, y2);
			boolean hitone = false;
			boolean hittingone = false;
			int x = x1, y = y1;
			
			// AFAIK this is pretty similar to Bresenham's thing.
			while(x <= x2 && y <= y2)
			{
				// RANGE CHECK!
				if(x >= 0-(canvas.FULLGRID_W/2)+1 && x < canvas.FULLGRID_W-((canvas.FULLGRID_W/2)-1) && y >= 0-(canvas.FULLGRID_H/2)+1 && y < canvas.FULLGRID_H-((canvas.FULLGRID_H/2)-1))
				{
					CraftrBlock t = map.getBlock(x+sx,y+sy);

					// first check: block behind is empty
					// second check: block is aligned with the axis
					// third check: block right/left is empty
					boolean antidiagcheck = 
						   map.getBlock(x+sx-dx,y+sy-dy).isEmpty()
						|| x == 0 || y == 0
						|| map.getBlock(x+sx-(x < 0 ? -ady : ady),y+sy-(y < 0 ? -adx : adx)).isEmpty();

					//if(antidiagcheck || !t.isEmpty()) // no corners for you - TODO: fix the "flicker"
					if(antidiagcheck)
						gs.blocks[((y+((canvas.FULLGRID_H/2)-1))*canvas.FULLGRID_W)+x+((canvas.FULLGRID_W/2)-1)] = t;
					
					if(!(t.isEmpty() && antidiagcheck))
					{
						if(!hittingone)
						{
							hitone = true;
							hittingone = true;
							// we must split this.
							if(x1 != x || y1 != y)
								castRayPillars(sx,sy,dx,dy,x1,y1,x-ady,y-adx,maxtrace);
						}
					} else if(hittingone) {
						hittingone = false;
						x1 = x;
						y1 = y;
					}
				}
				x += ady;
				y += adx;
			}
			
			// touch walls if necessary
			x = x1;
			y = y1;
			{
				CraftrBlock t2 = map.getBlock(x+sx,y+sy);

				if(t2.isEmpty())
				{
					x -= ady;
					y -= adx;
					if(x >= 0-(canvas.FULLGRID_W/2)+1 && x < canvas.FULLGRID_W-((canvas.FULLGRID_W/2)-1) && y >= 0-(canvas.FULLGRID_H/2)+1 && y < canvas.FULLGRID_H-((canvas.FULLGRID_H/2)-1))
					{
						CraftrBlock t = map.getBlock(x+sx,y+sy);

						if(!t.isEmpty())
							gs.blocks[((y+((canvas.FULLGRID_H/2)-1))*canvas.FULLGRID_W)+x+((canvas.FULLGRID_W/2)-1)] = t;		
					}
				}
			}
			
			x = x2;
			y = y2;
			{
				CraftrBlock t2 = map.getBlock(x+sx,y+sy);

				if(t2.isEmpty())
				{
					x += ady;
					y += adx;
					if(x >= 0-(canvas.FULLGRID_W/2)+1 && x < canvas.FULLGRID_W-((canvas.FULLGRID_W/2)-1) && y >= 0-(canvas.FULLGRID_H/2)+1 && y < canvas.FULLGRID_H-((canvas.FULLGRID_H/2)-1))
					{
						CraftrBlock t = map.getBlock(x+sx,y+sy);

						if(!t.isEmpty())
							gs.blocks[((y+((canvas.FULLGRID_H/2)-1))*canvas.FULLGRID_W)+x+((canvas.FULLGRID_W/2)-1)] = t;		
					}
				}
			}
			
			if(hitone)
			{
				if(!hittingone)
				{
					castRayPillars(sx,sy,dx,dy,x1,y1,x2,y2,maxtrace);
				}
				
				return;
			}
			
			if(dy == 0)
			{
				x1 += dx;
				x2 += dx;
			} else {
				y1 += dy;
				y2 += dy;
			}
			
			if(dy == 0)
			{
				y1 = (int)(oy1*x1/ox1);
				y2 = (int)(oy2*x2/ox2);
			} else {
				x1 = (int)(ox1*y1/oy1);
				x2 = (int)(ox2*y2/oy2);
			}
			
			maxtrace--;
		}
	}
	public void runOnce()
	{
		hasShot = false;
		if(isKick)
		{
			gt.isRunning=false;
			// TODO: Add kicking
		}
	}
}
