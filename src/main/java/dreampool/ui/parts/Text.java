package dreampool.ui.parts;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import dreampool.Application;
import dreampool.core.Part;
import dreampool.ui.Font;

public class Text extends Part{
	public Font font;
	public String text;
	public Vector2f position;
	public Vector4f color;
	public float size;
	
	public Text(String text, float size, float x, float y, int r, int g, int b, float a, Font font) {
		this.text = text;
		position = new Vector2f(x / Application.resDivisor, y / Application.resDivisor);
		color = new Vector4f((float)r / 255, (float)g / 255, (float)b / 255, a);
		this.font = font;
		this.size = size * Application.resDivisor;
	}
	
	@Override
	public void Update() {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
		font.renderText(text, new Vector3f(position, 0), color, size);
	}
}
