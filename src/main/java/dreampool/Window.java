package dreampool;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

public class Window {
	public static Window Singleton;
	public long ID;
	public int height = 10;
	public int width = 10;
	private IntBuffer w;
	private IntBuffer h;

	public Window() {
		if (Singleton != null) {
			System.out.println("Only one Window!");
		} else {
			Singleton = this;
		}
		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		ID = GLFW.glfwCreateWindow(800, 600, "Winer", NULL, NULL);
		if (ID == NULL) {
			System.out.println("Failed to create GLFW window");
			GLFW.glfwTerminate();
			return;
		}
		GLFW.glfwMakeContextCurrent(ID);
		GLFW.glfwSwapInterval(0);
		w = MemoryStack.stackPush().mallocInt(1);
		h = MemoryStack.stackPush().mallocInt(1);
	}

	public void update() {
		GLFW.glfwGetWindowSize(Window.Singleton.ID, w, h);
		width = w.get(0);
		height = h.get(0);
	}

	public void destroy() {
		w.clear();
		h.clear();
		GLFW.glfwTerminate();
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(ID);
	}

}
