package dreampool.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import dreampool.Application;
import dreampool.WindowSystem;
import dreampool.core.Part;
import dreampool.ui.ColoredRectangle;
import dreampool.ui.Font;

public class Dropdown extends Part {
	public List<Part> children = new ArrayList<>();
	private UIButton bar;
	private ColoredRectangle background;
	private Text nameBar;
	public Vector3f position;
	public Vector2f size;
	public Vector4f color;
	public boolean fitChildren = true;
	private boolean down = true;
	private int contentHeight = 0;
	private int contentWidth = 0;

	public Dropdown(Font font, String name, float x, float y, float width, float height, int r, int g, int b, float a) {
		this.position = new Vector3f(x / Application.resDivisor, y / Application.resDivisor, 0);
		this.size = new Vector2f(width * Application.resDivisor, height * Application.resDivisor);
		this.color = new Vector4f((float) r / 255, (float) g / 255, (float) b / 255, a);
		bar = new UIButton(x, y, width, height, new Runnable() {
			@Override
			public void run() {
				if (GLFW.glfwGetKey(WindowSystem.Singleton.ID, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
					bar.position.y += 10;
					nameBar.position.y += 10;
					background.position.y += 10;
					children.forEach(child -> {
						if (child instanceof Text) {
							((Text) child).position.y += 10;
						}
						if (child instanceof Image) {
							((Image) child).position.y += 10;
						}
					});
					return;
				}
				if (GLFW.glfwGetKey(WindowSystem.Singleton.ID, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
					bar.position.y -= 10;
					nameBar.position.y -= 10;
					background.position.y -= 10;
					children.forEach(child -> {
						if (child instanceof Text) {
							((Text) child).position.y -= 10;
						}
						if (child instanceof Image) {
							((Image) child).position.y -= 10;
						}
					});
					return;
				}
				if (GLFW.glfwGetKey(WindowSystem.Singleton.ID, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
					bar.position.x += 10;
					nameBar.position.x += 10;
					background.position.x += 10;
					children.forEach(child -> {
						if (child instanceof Text) {
							((Text) child).position.x += 10;
						}
						if (child instanceof Image) {
							((Image) child).position.x += 10;
						}
					});
					return;
				}
				down = !down;
				background.enabled = down;
				children.forEach(child -> {
					child.enabled = down;
				});
			}
		});
		bar.baseColor = new Vector4f((float) r / 255, (float) g / 255, (float) b / 255, a);
		background = new ColoredRectangle(x, y - (height * Application.resDivisor), width, height, 80, 80, 80, 1);
		this.nameBar = new Text(name, height, x * Application.resDivisor, y * Application.resDivisor, 1, 1, 1, a, font);
	}

	public void addChild(Part child) {
		child.thing = this.thing;
		child.Start();
		if (child instanceof Text) {
			((Text) child).position.add(this.position.x * Application.resDivisor,
					(this.position.y * Application.resDivisor) - (((Text) child).textMax.y - ((Text) child).textMin.y)
							- contentHeight);
			contentHeight += (((Text) child).textMax.y - ((Text) child).textMin.y);
		}
		if (child instanceof Image) {
			((Image) child).position.add(this.position.x * Application.resDivisor,
					(this.position.y * Application.resDivisor) - contentHeight - ((Image) child).size.y, 0);
			contentHeight += (((Image) child).size.y);
		}
		children.add(child);
		if (fitChildren) {
			background.size.y = contentHeight + size.y;
		}
	}

	@Override
	public void Start() {
		bar.thing = this.thing;
		bar.Start();
		nameBar.Start();
		background.position.y -= contentHeight;
	}

	@Override
	public void Update() {
		bar.Update();
		nameBar.Update();
		background.Update();
		children.forEach(child -> {
			child.Update();
		});
	}
}
