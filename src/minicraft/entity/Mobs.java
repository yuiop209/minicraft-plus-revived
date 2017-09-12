package minicraft.entity;

import minicraft.gfx.MobSprite;

public enum Mobs {
	
	/*
	 * Every Mob needs:
	 * - mobSprites array
	 * - colors for each level
	 * - drops
	 * - health determinant, somehow
	 * - 
	 */
	
	COW () {
		void setup() {
			
		}
	},
	
	SHEEP (),
	
	PIG (),
	
	ZOMBIE(),
	
	SKELETON(),
	
	CREEPER(),
	
	SLIME() {
		void setup() {
			MobSprite[] list = MobSprite.compileSpriteList(0, 18, 2, 2, 0, 2);
			sprites = new MobSprite[1][2];
			sprites[0] = list;
		}
	},
	
	SNAKE(),
	
	KNIGHT(),
	
	AIRWIZARD();
	
	
	private static MobSprite[][] sprites;
	private int[] lvlcols;
	
	Mobs() {}
	
	Mobs(int spriteX, int spriteY, int healthBase, int... cols) {
		
	}
	
	/**
	 * Constructors could get messy, so I'll do it this way instead.
	 */
	abstract void setup();
	
	/**
	 * The idea of this method is to use the given parameters to create a new mob.
	 * 
	 * @return a new MobAi instance to use.
	 */
	public MobAi spawn() {
		return null;
	}
}
