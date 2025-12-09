package dreampool.example.scenes;

import org.joml.Random;

import dreampool.Window;
import dreampool.core.Part;
import dreampool.ui.parts.Image;

public class MenuBackground extends Part {
	public Image image;

	public MenuBackground(Image image) {
		this.image = image;
	}

	@Override
	public void Update() {
		Random r = new Random();
		int widthRandom = r.nextInt(20);
		int heightRandom = r.nextInt(20);
		image.size.set(Window.Singleton.width + widthRandom, Window.Singleton.height + heightRandom);
		image.position.set(0, 0, 0);
		image.position.sub(widthRandom / 2, heightRandom / 2, 0);

	}
}
