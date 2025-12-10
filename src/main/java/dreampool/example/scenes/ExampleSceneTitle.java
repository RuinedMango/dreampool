package dreampool.example.scenes;

import dreampool.Application;
import dreampool.WindowSystem;
import dreampool.audio.NoiseListener;
import dreampool.core.Thing;
import dreampool.core.scene.Scene;
import dreampool.core.scene.SceneManager;
import dreampool.render.camera.Camera;
import dreampool.ui.Font;
import dreampool.ui.UIImage;
import dreampool.ui.parts.Image;
import dreampool.ui.parts.Text;
import dreampool.ui.parts.UIButton;

public class ExampleSceneTitle {
	public static Scene generateScene() {
		Font font = new Font("/fonts/Dotemp-8bit.ttf");
		Scene scene = new Scene("title");
		Thing player = new Thing("player");
		player.addPart(new Camera());
		player.addPart(new NoiseListener());
		Thing button = new Thing("button");
		Image buttonimg = new Image(new UIImage("/images/Button.png"), 40, 700, 50f, 50f, 255, 255, 255, 1.0f);
		button.addPart(buttonimg);
		button.addPart(new UIButton(new Runnable() {
			@Override
			public void run() {
				SceneManager.Singleton.setScene(ExampleScene.generateScene());
			}
		}));
		button.addPart(new Crosshair(buttonimg));
		Thing startext = new Thing("Start Text");
		Text starttext = new Text("Start!!!", 80, 40, 700, 0, 0, 0, 1, font);
		startext.addPart(starttext);
		startext.addPart(new StartText(starttext));
		Thing background = new Thing("Background");
		Image iamge = new Image(new UIImage("/images/Courtyard King.jpg"), 0f, 0f,
				(float) WindowSystem.Singleton.width * Application.resDivisor,
				(float) WindowSystem.Singleton.height * Application.resDivisor, 255, 255, 255, 1);
		background.addPart(iamge);
		background.addPart(new MenuBackground(iamge));
		scene.addThing(player);
		scene.addThing(startext);
		scene.addThing(background);
		scene.addThing(button);
		return scene;
	}
}
