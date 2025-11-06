package dreampool.ui;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import dreampool.Window;
import dreampool.core.Part;

public class UIButton extends Part {
	public float x;
	public float y;
	public float w;
	public float h;
	public boolean hovered;
	public boolean pressed;
	public Runnable callback;

	public UIButton(float x, float y, float w, float h, Runnable callback) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.callback = callback;
	}

	@Override
	public void Update() {
		DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer mouseY_tmp = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(Window.Singleton.ID, mouseX, mouseY_tmp);
		double mouseY = Window.Singleton.height - mouseY_tmp.get(0);

		System.out.print("" + mouseX.get(0) + ":" + mouseY);

		boolean insideBound = mouseX.get(0) >= x && mouseX.get(0) <= x + w && mouseY >= y && mouseY <= y + h;

		System.out.println(":" + insideBound);
	}
}
