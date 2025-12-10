package dreampool.IO;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.MemoryStack;

public class InputManager {
	private long window;

	private boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST + 1];
	private boolean[] keyPressed = new boolean[GLFW.GLFW_KEY_LAST + 1];
	private boolean[] keyReleased = new boolean[GLFW.GLFW_KEY_LAST + 1];

	private boolean[] mouseDown = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
	private boolean[] mousePressed = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
	private boolean[] mouseReleased = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

	private double mouseX;
	private double mouseY;
	private double lastMouseX;
	private double lastMouseY;
	private double mouseDX;
	private double mouseDY;
	private double scrollY;

	private StringBuilder textBuffer = new StringBuilder();

	private GLFWKeyCallback keyCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback scrollCallback;
	private GLFWCharCallback charCallback;

	public InputManager(long window) {
		this.window = window;
	}

	public void init() {
		keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long windowHandle, int key, int scancode, int action, int mods) {
				if (key < 0 || key > GLFW.GLFW_KEY_LAST)
					return;
				if (action == GLFW.GLFW_PRESS) {
					keyDown[key] = true;
					keyPressed[key] = true;
				} else if (action == GLFW.GLFW_RELEASE) {
					keyDown[key] = false;
					keyReleased[key] = true;
				}
			}
		};
		GLFW.glfwSetKeyCallback(window, keyCallback);

		mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long windowHandle, int button, int action, int mods) {
				if (button < 0 || button > GLFW.GLFW_MOUSE_BUTTON_LAST)
					return;
				if (action == GLFW.GLFW_PRESS) {
					mouseDown[button] = true;
					mousePressed[button] = true;
				} else if (action == GLFW.GLFW_RELEASE) {
					mouseDown[button] = false;
					mouseReleased[button] = true;
				}
			}
		};
		GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);

		scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long windowHandle, double xOffset, double yOffset) {
				scrollY += yOffset;
			}
		};
		GLFW.glfwSetScrollCallback(window, scrollCallback);

		charCallback = new GLFWCharCallback() {
			@Override
			public void invoke(long windowHandle, int codepoint) {
				textBuffer.append((char) codepoint);
			}
		};
		GLFW.glfwSetCharCallback(window, charCallback);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer x = stack.mallocDouble(1);
			DoubleBuffer y = stack.mallocDouble(1);
			GLFW.glfwGetCursorPos(window, x, y);
			mouseX = lastMouseX = x.get(0);
			mouseY = lastMouseY = y.get(0);
		}
	}

	public void update() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer x = stack.mallocDouble(1);
			DoubleBuffer y = stack.mallocDouble(1);
			GLFW.glfwGetCursorPos(window, x, y);
			mouseX = lastMouseX = x.get(0);
			mouseY = lastMouseY = y.get(0);
		}
		mouseDX = mouseX - lastMouseX;
		mouseDY = mouseY - lastMouseY;
		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}

	public void endFrame() {
		Arrays.fill(keyPressed, false);
		Arrays.fill(keyReleased, false);
		Arrays.fill(mousePressed, false);
		Arrays.fill(mouseReleased, false);
		scrollY = 0;
		mouseDX = 0;
		mouseDY = 0;
		textBuffer.setLength(0);
	}

	public boolean isKeyDown(int key) {
		return key >= 0 && key <= GLFW.GLFW_KEY_LAST && keyDown[key];
	}

	public boolean isKeyPressed(int key) {
		return key >= 0 && key <= GLFW.GLFW_KEY_LAST && keyPressed[key];
	}

	public boolean isKeyReleased(int key) {
		return key >= 0 && key <= GLFW.GLFW_KEY_LAST && keyReleased[key];
	}

	public boolean isMouseDown(int btn) {
		return btn >= 0 && btn <= GLFW.GLFW_MOUSE_BUTTON_LAST && mouseDown[btn];
	}

	public boolean isMousePressed(int btn) {
		return btn >= 0 && btn <= GLFW.GLFW_MOUSE_BUTTON_LAST && mousePressed[btn];
	}

	public boolean isMouseReleased(int btn) {
		return btn >= 0 && btn <= GLFW.GLFW_MOUSE_BUTTON_LAST && mouseReleased[btn];
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public double getMouseDX() {
		return mouseDX;
	}

	public double getMouseDY() {
		return mouseDY;
	}

	public double getScrollY() {
		return scrollY;
	}

	public StringBuilder getTextBuffer() {
		return textBuffer;
	}

}