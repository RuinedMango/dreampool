package dreampool.example.scenes;

import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.WindowSystem;
import dreampool.audio.NoiseListener;
import dreampool.core.Thing;
import dreampool.core.scene.Scene;
import dreampool.physics.bounds.AABBCollider;
import dreampool.physics.bounds.SphereCollider;
import dreampool.render.camera.Camera;
import dreampool.render.fog.Fog;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;
import dreampool.ui.Font;
import dreampool.ui.UIImage;
import dreampool.ui.parts.Dropdown;
import dreampool.ui.parts.Image;
import dreampool.ui.parts.Text;

public class ExampleScene {
	public static Scene generateScene() {
		Scene scene = new Scene("example");
		Thing fogThing = new Thing("fog");
		fogThing.addPart(new Fog(new Vector4f(0.5f, 0.5f, 0.5f, 1.0f), 10.0f, 50.0f));
		Font arial = new Font("/fonts/Oswald-Regular.ttf");
		Font shade = new Font("/fonts/ShadeBlue.ttf");
		Thing player = new Thing("player");
		player.addPart(new Camera());
		player.addPart(new NoiseListener());
		player.addPart(new PlayerController());
		player.transform.position = new Vector3f(4, 0, 1);
		player.transform.rotation = new Vector3f(-90, 0, 0);

		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 50; j++) {
				Thing ball = new Thing((i % 2 >= 1 ? "bunny" : "ball") + i + j);
				ball.addPart(i % 2 >= 1 ? new Mesh("/models/bunny.obj", true) : new Mesh("/models/Sphere.obj", false));
				ball.addPart(i % 2 >= 1 ? new AABBCollider(true) : new SphereCollider(true));
				ball.addPart(new Texture("/images/white.png"));
				ball.addPart(new Texture("/images/white.png", 1));
				ball.addPart(new Rotator());
				ball.transform.position = new Vector3f(-i, -2, j);
				if (i % 2 >= 1) {
					ball.transform.size = new Vector3f(3, 3, 3);
				} else {
					ball.transform.size = new Vector3f(0.3f, 0.3f, 0.3f);
				}
				scene.addThing(ball);
			}
		}

		Thing fps = new Thing("fps");
		// Text text = new Text("Fps: " + Time.fps, 32, 10, 700, 190, 0, 75, 1, arial);
		// fps.addPart(text);
		// fps.addPart(new FPSDisplay(text));
		Thing crosshair = new Thing("crosshair");
		Image crossimage = new Image(new UIImage("/images/crosshair.png"), (WindowSystem.Singleton.height / 2f),
				(WindowSystem.Singleton.width / 2f), 50f, 50f, 255, 255, 255, 1.0f);
		crosshair.addPart(crossimage);
		crosshair.addPart(new Crosshair(crossimage));
		Thing dropdown = new Thing("drop");
		Dropdown dropart = new Dropdown(arial, "ballsack", 400, 400, 180, 30, 200, 200, 200, 1);
		dropdown.addPart(dropart);
		Text droptext = new Text("Weener", 32, 0, 0, 0, 0, 0, 1, shade);
		dropart.addChild(droptext);
		dropart.addChild(new Image(new UIImage("/images/crosshair.png"), 0, 0, 50f, 50f, 255, 255, 255, 1.0f));
		dropart.addChild(new Image(new UIImage("/images/crosshair.png"), 0, 0, 50f, 50f, 255, 255, 255, 1.0f));
		scene.addThing(fogThing);
		scene.addThing(player);
		scene.addThing(fps);
		scene.addThing(crosshair);
		scene.addThing(dropdown);
		return scene;
	}
}
