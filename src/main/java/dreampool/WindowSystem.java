package dreampool;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.system.MemoryStack;

public class WindowSystem {
	private long ID;
	private int height;
	private int width;
	private String title;

	private IntBuffer wBuffer;
	private IntBuffer hBuffer;

	private boolean initialized = false;

	public WindowSystem(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public void init() {
		if (initialized)
			return;

		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("GLFW Failed to Init.");
		}

		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		ID = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
		if (ID == NULL) {
			GLFW.glfwTerminate();
			throw new RuntimeException("Failed to create GLFW window");
		}
		GLFW.glfwMakeContextCurrent(ID);
		GLFW.glfwSwapInterval(0);

		wBuffer = MemoryStack.stackPush().mallocInt(1);
		hBuffer = MemoryStack.stackPush().mallocInt(1);

		initialized = true;
	}

	public void update() {
		GLFW.glfwPollEvents();
		GLFW.glfwGetWindowSize(ID, wBuffer, hBuffer);
		width = wBuffer.get(0);
		height = hBuffer.get(0);
		wBuffer.rewind();
		hBuffer.rewind();
	}

	public void destroy() {
		if (!initialized)
			return;
		GLFW.glfwDestroyWindow(ID);
		GLFW.glfwTerminate();

		wBuffer.clear();
		hBuffer.clear();

		initialized = false;
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(ID);
	}

	public void setFramebufferSizeCallback(GLFWFramebufferSizeCallback callback) {
		GLFW.glfwSetFramebufferSizeCallback(ID, callback);
	}

}
