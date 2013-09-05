package com.jthtml.zufo.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Zebra {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}

	public static final float SIZE = 1.5f; // half a unit
	
	Vector2	position = new Vector2();
	Vector2 acceleration = new Vector2();
	Vector2	velocity = new Vector2();
	Rectangle bounds = new Rectangle();
	State state = State.IDLE;
	boolean	facingLeft = true;
	float stateTime = 0;
	boolean  longJump = false;
	boolean grounded = true;
	
	public Zebra(Vector2 position) {
		this.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.bounds.height = SIZE;
		this.bounds.width = SIZE;
	}
	
	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getAcceleration() {
		return acceleration;	
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public State getState() {
		return state;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setState(State newState) {
		this.state = newState;
	}

	public boolean isLongJump() {
		return longJump;
	}
	
	public boolean isGrounded() {
		return grounded;
	}
	
	public void setLongJump(boolean longJump) {
		this.longJump = longJump;
	}

	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}
	
	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}
	
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public void update(float delta) {
		stateTime += delta;
	}
}