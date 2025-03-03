package dreampool.core;

import org.lwjgl.glfw.GLFW;

public class Time {
	public static Time Singleton = null;
	public static float deltaTime;
	public static int fps;
	private int fpsCounter = 0;
	private float lastTime;
	private float lastFPSUpdateTime;
	
	public Time() {
		if(Singleton == null) {
			Singleton = this;
		} else {
			System.out.println("Time already exists");
		}
	}
	
	public void update() {
		float currentTime = (float) GLFW.glfwGetTime();
		deltaTime = currentTime - lastTime;
		lastTime = currentTime;
		fpsCounter++;
		if(currentTime - lastFPSUpdateTime > 1.0) {
			fps = fpsCounter;
			fpsCounter = 0;
			lastFPSUpdateTime = currentTime;
		}
	}
}
