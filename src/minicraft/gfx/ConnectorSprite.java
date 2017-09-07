package minicraft.gfx;

import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class ConnectorSprite {
	/**
		This class is meant for those tiles that look different when they are touching other tiles of their type; aka, they "connect" to them.
		
		Since I think connecting tile sprites tend to have three color categories, maybe this should have two extra colors..?
		
		This class will need to keep rack of the following sprites:
		-a sprite for each kind of intersection; aka a 3x3 grid of sprite pixels, that show the sprite for each position, totally surrounded, nothing of left, etc.
		
	*/
	
	private boolean configured = false;
	private Sprite current;
	
	public Sprite sparse, sides, full;
	private Class<? extends Tile> owner;
	private boolean checkCorners;
	
	public ConnectorSprite(Class<? extends Tile> owner, Sprite sparse, Sprite sides, Sprite full) {
		this(owner, sparse, sides, full, true);
	}
	public ConnectorSprite(Class<? extends Tile> owner, Sprite sparse, Sprite sides, Sprite full, boolean cornersMatter) {
		this.owner = owner;
		this.sparse = sparse;
		this.sides = sides;
		this.full = full;
		this.checkCorners = cornersMatter;
	}
	public ConnectorSprite(Class<? extends Tile> owner, Sprite sparse, Sprite full) {
		this(owner, sparse, sparse, full, false);
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		if(!configured)
			update(level, x, y, sparse.getColor(), sides.getColor(), full.getColor());
		
		current.render(screen, x, y);
	}
	
	private void update(Level level, int x, int y, int colsparse, int colside, int colfull) {
		//System.out.println("rendering sprite for tile " + owner);
		current = new SpriteBuilder().setSx(0).setSy(0).setSw(2).setSh(2).createSprite();
		
		configured = true;
		
		Tile ut = level.getTile(x, y - 1);
		Tile dt = level.getTile(x, y + 1);
		Tile lt = level.getTile(x - 1, y);
		Tile rt = level.getTile(x + 1, y);
		
		boolean u = connectsTo(ut, true);
		boolean d = connectsTo(dt, true);
		boolean l = connectsTo(lt, true);
		boolean r = connectsTo(rt, true);
		
		boolean ul = connectsTo(level.getTile(x - 1, y - 1), false);
		boolean dl = connectsTo(level.getTile(x - 1, y + 1), false);
		boolean ur = connectsTo(level.getTile(x + 1, y - 1), false);
		boolean dr = connectsTo(level.getTile(x + 1, y + 1), false);
		
		x = x << 4;
		y = y << 4;
		
		//full.render(screen, x, y);
		//int[] spc = Color.seperateEncodedSprite(colsparse); // TODO maybe the getConnectColor method decide the whole thing; just pass in the original color too?
		
		// full.renderPixel(0, 0, screen, x, y, colfull);
		// full.renderPixel(1, 0, screen, x+8, y, colfull);
		// full.renderPixel(0, 1, screen, x, y+8, colfull);
		// full.renderPixel(1, 1, screen, x+8, y+8, colfull);
		int orig = colsparse;
		
		colsparse = getSparseColor(ut, colsparse);
		colsparse = getSparseColor(lt, colsparse);
		
		if (u && l) {
			if (ul || !checkCorners) full.renderPixel(1, 1, screen, x, y, colfull);
			else sides.renderPixel(0, 0, screen, x, y, colside);
		} else
			sparse.renderPixel(u?1:2, l?1:2, screen, x, y, colsparse/*);Color.get(spc[0], spc[1], spc[2], Color.mixRGB(ut.getConnectColor(level), lt.getConnectColor(level)))*/);
		
		colsparse = orig;
		colsparse = getSparseColor(ut, colsparse);
		colsparse = getSparseColor(rt, colsparse);
		
		if (u && r) {
			if (ur || !checkCorners) full.renderPixel(1, 0, screen, x+8, y, colfull);
			else sides.renderPixel(0, 1, screen, x+8, y, colside);
		} else// if(!checkCorners)
			sparse.renderPixel(u?1:2, r?1:0, screen, x+8, y, colsparse/*);Color.get(spc[0], spc[1], spc[2], Color.mixRGB(ut.getConnectColor(level), rt.getConnectColor(level)))*/);
		//else // useful for trees
		
		colsparse = orig;
		colsparse = getSparseColor(dt, colsparse);
		colsparse = getSparseColor(lt, colsparse);
		
		if (d && l) {
			if (dl || !checkCorners) full.renderPixel(0, 1, screen, x, y+8, colfull);
			else sides.renderPixel(1, 0, screen, x, y+8, colside);
		} else
			sparse.renderPixel(d?1:0, l?1:2, screen, x, y+8, colsparse/*);Color.get(spc[0], spc[1], spc[2], Color.mixRGB(dt.getConnectColor(level), lt.getConnectColor(level)))*/);
			
		colsparse = orig;
		colsparse = getSparseColor(dt, colsparse);
		colsparse = getSparseColor(rt, colsparse);
			
		if (d && r) {
			if (dr || !checkCorners) full.renderPixel(0, 0, screen, x+8, y+8, colfull);
			else sides.renderPixel(1, 1, screen, x+8, y+8, colside);
		} else
			sparse.renderPixel(d?1:0, r?1:0, screen, x+8, y+8, colsparse/*);Color.get(spc[0], spc[1], spc[2], Color.mixRGB(dt.getConnectColor(level), rt.getConnectColor(level)))*/);
		
	}
	
	// it is expected that some tile classes will override this on class instantiation.
	public boolean connectsTo(Tile tile, boolean isSide) {
		//System.out.println("original connection check");
		return tile.getClass() == owner;
	}
	
	public int getSparseColor(Tile tile, int origCol) {
		return origCol;
	}
	
	public static Sprite makeSprite(int w, int h, int color, int mirror, boolean repeat, int... coords) {
		Sprite.Px[][] pixels = new Sprite.Px[h][w];
		int i = 0;
		for(int r = 0; r < h && i < coords.length; r++) {
			for(int c = 0; c < w && i < coords.length; c++) {
				int pos = coords[i];
				pixels[r][c] = new Sprite.Px(pos%32, pos/32, mirror);
				i++;
				if(i == coords.length && repeat) i = 0;
			}
		}
		
		return new SpriteBuilder().setPixels(pixels).setColor(color).createSprite();
	}
}
