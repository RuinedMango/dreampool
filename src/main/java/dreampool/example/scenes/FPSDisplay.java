package dreampool.example.scenes;

import org.joml.Vector2f;

import dreampool.WindowSystem;
import dreampool.core.Part;
import dreampool.core.Time;
import dreampool.ui.parts.Text;

public class FPSDisplay extends Part {
	public Text text;

	public FPSDisplay(Text text) {
		this.text = text;
	}

	@Override
	public void Update() {
		text.text = "Fps: " + Time.fps;
		text.position = new Vector2f(10, (float) (WindowSystem.Singleton.height - (text.size * 0.75)));
	}
}
