package com.jthtml.zufo.view;

import com.jthtml.zufo.model.Block;
import com.jthtml.zufo.model.Zebra;
import com.jthtml.zufo.model.World;
import com.jthtml.zufo.model.Zebra.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class WorldRenderer {

	private static final float CAMERA_WIDTH = 30f;
	private static final float CAMERA_HEIGHT = 20f;
	private static final float RUNNING_FRAME_DURATION = 0.06f;
	
	private World world;
	private OrthographicCamera cam;
	
	/** for debug rendering **/
	ShapeRenderer debugRenderer = new ShapeRenderer();
	
	/** Textures **/
	private TextureRegion zebraIdleLeft;
	private TextureRegion zebraIdleRight;
	private TextureRegion blockTexture;
	private TextureRegion zebraFrame;
	private TextureRegion zebraJumpLeft;
	private TextureRegion zebraFallLeft;
	private TextureRegion zebraJumpRight;
	private TextureRegion zebraFallRight;

	/** Animation **/
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	
	/**Tilemap**/
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	
	private BitmapFont font;
	
	
	private SpriteBatch spriteBatch;
	private boolean debug = false;
	private int width;
	private int height;
	private float ppuX; // pixels per unit on the X axis
	private float ppuY; // pixels per unit on the Y axis
	public void setSize (int w, int h) {
		this.width = w;
		this.height = h;
		ppuX = width / CAMERA_WIDTH;
		ppuY = height / CAMERA_HEIGHT;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public WorldRenderer(World world, boolean debug) {
		this.world = world;
		this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
		this.cam.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f,0);
		this.cam.update();
		this.debug = debug;
		this.map = world.getMap();
		this.renderer = new OrthogonalTiledMapRenderer(map, 1/16f);
		spriteBatch = renderer.getSpriteBatch();
		loadTextures();
	}

	private void loadTextures() {
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("images/textures/textures.pack"));
		zebraIdleLeft = atlas.findRegion("zebra-01");
		zebraIdleRight = new TextureRegion(zebraIdleLeft);
		zebraIdleRight.flip(true, false);
		blockTexture = atlas.findRegion("block");
		TextureRegion[] walkLeftFrames = new TextureRegion[5];
		for (int i = 0 ; i < 5 ; i++) {
			walkLeftFrames[i] = atlas.findRegion("zebra-0" + (i+2));
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);
		
		TextureRegion[] walkRightFrames = new TextureRegion[5];
		
		for (int i =0 ; i < 5 ; i++) {
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);
		zebraJumpLeft = atlas.findRegion("zebra-up");
		zebraJumpRight = new TextureRegion(zebraJumpLeft);
		zebraJumpRight.flip(true, false);
		zebraFallLeft = atlas.findRegion("zebra-down");
		zebraFallRight = new TextureRegion(zebraFallLeft);
		zebraFallRight.flip(true, false);
	}
	
	public void render() {
		// Load our font
		font = new BitmapFont();

		this.cam.position.set(world.getZebra().getPosition().x, world.getZebra().getPosition().y + 8, 0);
		this.cam.update();
		renderer.setView(this.cam);
		spriteBatch.begin();
		drawZebra();
		spriteBatch.end();
		renderer.render();

//		SpriteBatch batch = new SpriteBatch();
//		batch.begin();
//		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 2, 20);
//		batch.end();		
		System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
	}

	private void drawZebra() {
		Zebra zebra = world.getZebra();
		zebraFrame = zebra.isFacingLeft() ? zebraIdleLeft : zebraIdleRight;
		if (zebra.getState().equals(State.WALKING)) {
			zebraFrame = zebra.isFacingLeft() ? walkLeftAnimation.getKeyFrame(zebra.getStateTime(), true) : walkRightAnimation.getKeyFrame(zebra.getStateTime(), true);
		} else if (zebra.getState().equals(State.JUMPING)) {
			if (zebra.getVelocity().y > 0) {
				zebraFrame = zebra.isFacingLeft() ? zebraJumpLeft : zebraJumpRight;
			} else {
				zebraFrame = zebra.isFacingLeft() ? zebraFallLeft : zebraFallRight;
			}
		}
		spriteBatch.draw(zebraFrame,  zebra.getPosition().x, zebra.getPosition().y, Zebra.SIZE, Zebra.SIZE);
	}
	

}
