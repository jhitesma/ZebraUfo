package com.jthtml.zufo.controller;

import java.util.HashMap;
import java.util.Map;

import com.jthtml.zufo.model.Zebra;
import com.jthtml.zufo.model.Zebra.State;
import com.jthtml.zufo.model.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;


public class ZebraController {

	enum Keys {
		LEFT, RIGHT, JUMP, FIRE
	}

	private static final long LONG_JUMP_PRESS	=	150l;
	private static final float ACCELERATION	=	25f;
	private static final float GRAVITY	=	-25f;
	private static final float MAX_JUMP_SPEED	=	8f;
	private static final float DAMP	=	0.90f;
	private static final float MAX_VEL	=	5f;

	private World world;
	private Zebra zebra;
	private long 	jumpPressedTime;
	private boolean	jumpingPressed;
	private boolean grounded = false;

	// Rectangle pool used in collision detection
	// good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};

	private Array<Rectangle> tiles = new Array<Rectangle>();
	
	static Map<Keys, Boolean> keys = new HashMap<ZebraController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT,  false);
		keys.put(Keys.RIGHT,  false);
		keys.put(Keys.JUMP,  false);
		keys.put(Keys.FIRE,  false);
	}

	// Block we can collide with any given frame
	private Array<Rectangle> collidable = new Array<Rectangle>();
	
	
	public ZebraController(World world) {
		this.world = world;
		this.zebra = world.getZebra();
	}

	// ** Key presses and touches ********
	
	public void leftPressed() {
		keys.get(keys.put(Keys.LEFT, true));
	}

	public void rightPressed() {
		keys.get(keys.put(Keys.RIGHT,  true));
	}

	public void jumpPressed() {
		keys.get(keys.put(Keys.JUMP, true));
	}

	public void firePressed() {
		keys.get(keys.put(Keys.FIRE, true));
	}

	public void leftReleased() {
		keys.get(keys.put(Keys.LEFT, false));
	}

	public void rightReleased() {
		keys.get(keys.put(Keys.RIGHT,  false));
	}

	public void jumpReleased() {
		keys.get(keys.put(Keys.JUMP, false));
		jumpingPressed = false;
	}

	public void fireReleased() {
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	/** the main update method **/
	public void update(float delta) {
		processInput();

		// If we're grounded then reset the state to IDLE
		if (grounded && zebra.getState().equals(State.JUMPING)) {
			zebra.setState(State.IDLE);
		}
		
		// Set initial vertical acceleration
		zebra.getAcceleration().y = GRAVITY;

		// convert acceleration to frame time
		zebra.getAcceleration().scl(delta);

		// apply acceleration to change velocity
		zebra.getVelocity().add(zebra.getAcceleration().x, zebra.getAcceleration().y);

		// checking collisions with the surrounding blocks based on our velocity
		checkCollisionWithBlocks(delta);
		
		// apply damping to halt us nicely
		zebra.getVelocity().x *= DAMP;

		// ensure we don't exceed terminal velocity
		if (zebra.getVelocity().x > MAX_VEL) {
			zebra.getVelocity().x = MAX_VEL;
		}
		if (zebra.getVelocity().x < -MAX_VEL) {
			zebra.getVelocity().x = -MAX_VEL;
		}

		// update the state time
		zebra.update(delta);

	}

	/** Collision checking **/
	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units
		zebra.getVelocity().scl(delta);
		
		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle zebraRect = rectPool.obtain();
		// set the rectangle to our bounding box
		zebraRect.set(zebra.getBounds().x, zebra.getBounds().y, zebra.getBounds().width,zebra.getBounds().height);
	
		int startX, endX;
		int startY = (int) zebra.getBounds().y;
		int endY = (int) (zebra.getBounds().y + zebra.getBounds().height);

		// check movement on the horizontal X axis
		// if we're heading left then we check if we collide with the block on our left
		// otherwise check the block on our right
		if (zebra.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(zebra.getBounds().x + zebra.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(zebra.getBounds().x + zebra.getBounds().width + zebra.getVelocity().x);
		}


		// get the block(s) we can collide with
		TiledMap map = world.getMap();
		getTiles(startX, startY, endX, endY, tiles, map);

		// simulate our movement on the X
		zebraRect.x += zebra.getVelocity().x ;
		
		// clear collision boxes in world
		world.getCollisionRects().clear();
		
		// if we collide, make our horizontal velocity 0
		for (Rectangle tile : collidable) {
			if (tile == null) continue;
			if (zebraRect.overlaps(tile)) {
				zebra.getVelocity().x = 0;
				world.getCollisionRects().add(tile);
				break;
			}
		}
		

		
		// Same thing only on the vertical/Y axis instead
		startX = (int) zebra.getBounds().x;
		endX = (int) (zebra.getBounds().x + zebra.getBounds().width);
		if (zebra.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(zebra.getBounds().y + zebra.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(zebra.getBounds().y + zebra.getBounds().height + zebra.getVelocity().y);
		}


		zebraRect.y += zebra.getVelocity().y;
		for(Rectangle tile: tiles) {
			if(zebraRect.overlaps(tile)) {
				// we actually reset the koala y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if(zebra.getVelocity().y > 0) {
					zebra.getPosition().y = tile.y - zebra.getBounds().height;
					// we hit a block jumping upwards, let's destroy it!
					TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
					layer.setCell((int)tile.x, (int)tile.y, null);
				} else {
					zebra.getPosition().y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					grounded = true;
				}
				zebra.getVelocity().y = 0;
				break;
			}
		}
		
		// reset the x postion of the collision box
		zebraRect.x = zebra.getPosition().x;
		// reset the collision box's position on Y
		zebraRect.y = zebra.getPosition().y;
		
		// update our position
		zebra.getPosition().add(zebra.getVelocity());
		zebra.getBounds().x = zebra.getPosition().x;
		zebra.getBounds().y = zebra.getPosition().y;
	
		// un-scale velocity (not in frame time)
		zebra.getVelocity().scl(1 / delta);
		
	}

	
	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles, TiledMap map) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
		rectPool.freeAll(tiles);
		tiles.clear();
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
	}

	
	/** Change zebra's state and parameters based on user input **/
	private boolean processInput() {
		if (keys.get(Keys.FIRE)) {
			
		}
		if (keys.get(Keys.JUMP)) {
			if (!zebra.getState().equals(State.JUMPING)) {
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				zebra.setState(State.JUMPING);
				zebra.getVelocity().y = MAX_JUMP_SPEED;
				grounded = false;
			} else {
				if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						zebra.getVelocity().y = MAX_JUMP_SPEED;
					}
				}
			}
		}
		if (keys.get(Keys.LEFT)) {
			// left is pressed
			zebra.setFacingLeft(true);
			if (!zebra.getState().equals(State.JUMPING)) {
				zebra.setState(State.WALKING);
			}
			zebra.getAcceleration().x = -ACCELERATION;
		} else if (keys.get(Keys.RIGHT)) {
			// right is pressed
			zebra.setFacingLeft(false);
			if (!zebra.getState().equals(State.JUMPING)) {
				zebra.setState(State.WALKING);
			}
			zebra.getAcceleration().x = ACCELERATION;			
		} else {
			if (!zebra.getState().equals(State.JUMPING)) {
				zebra.setState(State.IDLE);
			}
			zebra.getAcceleration().x = 0;
		}
		return false;
	}
}