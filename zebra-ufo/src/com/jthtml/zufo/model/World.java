package com.jthtml.zufo.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class World {

		/** the blocks making up the world **/
		Array<Block> blocks = new Array<Block>();
		/** Our player controlled hero **/
		Zebra zebra;
		
		TiledMap map;
		
		/** The collision boxes **/
		Array<Rectangle> collisionRects = new Array<Rectangle>();
		
		// Getters --------
		public Array<Rectangle> getCollisionRects() {
			return collisionRects;
		}
		
		public Array<Block> getBlocks() {
			return blocks;
		}

		public Zebra getZebra() {
			return zebra;
		}
		
		public TiledMap getMap() {
			return map;
		}
		
		
		public World() {
			createDemoWorld();
		}

		private void createDemoWorld() {
			this.map = new TmxMapLoader().load("level1.tmx");
			zebra = new Zebra(new Vector2(7,4));
		}
}
