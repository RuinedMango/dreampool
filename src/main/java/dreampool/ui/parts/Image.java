package dreampool.ui.parts;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import dreampool.Application;
import dreampool.core.Part;
import dreampool.ui.UIImage;

public class Image extends Part {
	public UIImage uiTexture;
	public Vector3f position;
	public Vector2f size;
	public Vector4f color;

	public Image(UIImage texture, float x, float y, float width, float height, int r, int g, int b, float a) {
		this.desiredRenderOrder = 1000;
		this.uiTexture = texture;
		this.position = new Vector3f(x / Application.resDivisor, y / Application.resDivisor, 0);
		this.size = new Vector2f(width * Application.resDivisor, height * Application.resDivisor);
		this.color = new Vector4f((float) r / 255, (float) g / 255, (float) b / 255, a);
	}

	@Override
	public void Update() {
		if (enabled) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			uiTexture.renderQuad(position, color, size);
		}
	}
}
