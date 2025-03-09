package dreampool.IO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

public class DeviceManager{
	public static DeviceManager Singleton = null;
	public long window;

	public DeviceManager(long window){
		if (Singleton == null){
			Singleton = this;
			this.window = window;
		}else{
			System.out.println("DeviceManger exists");
		}
	}

	public void setFramebufferSizeCallback(GLFWFramebufferSizeCallback callback){
		GLFW.glfwSetFramebufferSizeCallback(window, callback);
	}

	public void setCursorPosCallback(GLFWCursorPosCallback callback){
		GLFW.glfwSetCursorPosCallback(window, callback);
	}
}