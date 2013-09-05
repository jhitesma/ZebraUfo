package com.jthtml.zufo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class ZebraUfoDesktop {
	public static void main(String[] args) {
		new LwjglApplication(new ZebraUfo(), "Zebras in UFOs", 480, 320, true);
	}
}