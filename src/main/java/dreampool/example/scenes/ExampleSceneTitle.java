package dreampool.example.scenes;

import dreampool.audio.NoiseListener;
import dreampool.core.Thing;
import dreampool.core.scene.Scene;
import dreampool.core.scene.SceneManager;
import dreampool.render.camera.Camera;
import dreampool.ui.UIButton;
import dreampool.ui.UIImage;
import dreampool.ui.parts.Image;

public class ExampleSceneTitle {
	public static Scene generateScene() {
		Scene scene = new Scene("title");
		Thing player = new Thing("player");
		player.addPart(new Camera());
		player.addPart(new NoiseListener());
		Thing button = new Thing("button");
		button.addPart(new Image(new UIImage("/images/crosshair.png"), 40, 700, 50f, 50f, 255, 255, 255, 1.0f));
		button.addPart(new UIButton(new Runnable() {
			@Override
			public void run() {
				SceneManager.Singleton.setScene(ExampleScene.generateScene());
			}
		}));
		scene.addThing(button);
		scene.addThing(player);
		return scene;
	}
}
