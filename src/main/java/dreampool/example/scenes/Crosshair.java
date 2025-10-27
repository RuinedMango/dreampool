package dreampool.example.scenes;

import dreampool.Application;
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
		image.position.x = (Application.height / 2f) - (200f / 2f);
		image.position.y = (Application.width / 2f) - (200f / 2f);
	}
}
