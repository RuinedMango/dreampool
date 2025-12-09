package dreampool.ui.parts;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import dreampool.Application;
import dreampool.core.Part;
import dreampool.ui.Font;

public class Text extends Part {
	public Font font;
	public String text;
	public Vector2f position;
	public Vector4f color;
	public float size;
	public Vector2f textMin;
	public Vector2f textMax;

	public Text(String text, float size, float x, float y, int r, int g, int b, float a, Font font) {
		this.desiredRenderOrder = 100;
		this.text = text;
		position = new Vector2f(x / Application.resDivisor, y / Application.resDivisor);
		color = new Vector4f((float) r / 255, (float) g / 255, (float) b / 255, a);
		this.font = font;
		this.size = size * Application.resDivisor;
	}

	@Override
	public void Start() {
		calculateMinMax();
	}

	@Override
	public void Update() {
		if (enabled) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			font.renderText(text, new Vector3f(position, 0), color, size);
		}
		calculateMinMax();
	}

	private void calculateMinMax() {
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;

		if (font.vertices != null) {
			final int stride = 9; // x,y,z,r,g,b,a,u,v
			final int xOff = 0;
			final int yOff = 1;

			float[] verticeArray = new float[font.vertices.size()];
			for (int i = 0; i < font.vertices.size(); i++) {
				verticeArray[i] = font.vertices.get(i);
			}
			float[] verts = verticeArray;
			for (int i = 0; i + Math.max(xOff, yOff) < verts.length; i += stride) {
				float x = verts[i + xOff];
				float y = verts[i + yOff];

				if (x < minX)
					minX = x;
				if (y < minY)
					minY = y;
				if (x > maxX)
					maxX = x;
				if (y > maxY)
					maxY = y;
			}
		}

		// Handle empty or invalid data
		if (!Float.isFinite(minX) || !Float.isFinite(minY)) {
			minX = minY = 0f;
			maxX = maxY = 0f;
		}

		textMin = new Vector2f(minX, minY);
		textMax = new Vector2f(maxX, maxY);
	}
}
