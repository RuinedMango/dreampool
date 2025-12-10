package dreampool.example.scenes;

import dreampool.WindowSystem;
import dreampool.core.Part;
import dreampool.ui.parts.Image;

public class Crosshair extends Part {
	public Image image;

	public Crosshair(Image image) {
		this.image = image;
	}

	@Override
	public void Update() {
		// image.position.x += 1;
		float height = (WindowSystem.Singleton.height / 2) - (image.size.y / 2);
		float width = (WindowSystem.Singleton.width / 2) - (image.size.x / 2);
		image.position.y = height;
		image.position.x = width;
	}
}
