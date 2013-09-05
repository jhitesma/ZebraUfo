package com.jthtml.zufo;

import com.jthtml.zufo.screens.GameScreen;

import com.badlogic.gdx.Game;

public class ZebraUfo extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}	
}