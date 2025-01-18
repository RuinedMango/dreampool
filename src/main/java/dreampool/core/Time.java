package dreampool.core;

import org.lwjgl.glfw.GLFW;

public class Time {
	public Time Singleton;
	public static float deltaTime;
	private float lastFrame;
	
	public Time() {
		if(Singleton == null) {
			Singleton = this;
		} else {
			System.out.println("Time already exists");
		}
	}
	
	public void update() {
		float currentFrame = (float) GLFW.glfwGetTime();
		deltaTime = currentFrame - lastFrame;
		lastFrame = currentFrame;
	}
}
