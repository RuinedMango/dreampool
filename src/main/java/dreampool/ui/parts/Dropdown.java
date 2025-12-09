package dreampool.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.Application;
import dreampool.core.Part;

public class Dropdown extends Part {
	public List<Part> children = new ArrayList<>();
	public UIButton bar;
	public Vector3f position;
	public Vector2f size;
	public Vector4f color;
	public boolean fitChildren = false;
	private boolean down = true;
	private int contentHeight = 0;
	private int contentWidth = 0;

	public Dropdown(float x, float y, float width, float height, int r, int g, int b, float a) {
		this.position = new Vector3f(x / Application.resDivisor, y / Application.resDivisor, 0);
		this.size = new Vector2f(width * Application.resDivisor, height * Application.resDivisor);
		this.color = new Vector4f((float) r / 255, (float) g / 255, (float) b / 255, a);
		bar = new UIButton(x, y, width, height, new Runnable() {
			@Override
			public void run() {
				down = !down;
				children.forEach(child -> {
					child.enabled = down;
				});
			}
		});
		bar.color = new Vector4f(r, g, b, a);
	}

	public void addChild(Part child) {
		child.thing = this.thing;
		child.Start();
		if (child instanceof Text) {
			((Text) child).position.add(this.position.x * Application.resDivisor,
					(this.position.y * Application.resDivisor) - this.size.y - contentHeight);
			contentHeight += (((Text) child).textMax.y - ((Text) child).textMin.y);
		}
		if (child instanceof Image) {
			((Image) child).position.add(this.position.x * Application.resDivisor,
					(this.position.y * Application.resDivisor) - this.size.y - contentHeight - ((Image) child).size.y,
					0);
			contentHeight += (((Image) child).size.y);
		}
		children.add(child);
	}

	@Override
	public void Start() {
	}

	@Override
	public void Update() {
		bar.Update();
		children.forEach(child -> {
			child.Update();
		});
	}
}
