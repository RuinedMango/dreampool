package dreampool.example.scenes;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.Application;
import dreampool.audio.NoiseListener;
import dreampool.core.Thing;
import dreampool.core.Time;
import dreampool.core.scene.Scene;
import dreampool.physics.bounds.Collider;
import dreampool.physics.bounds.SphereCollider;
import dreampool.render.camera.Camera;
import dreampool.render.fog.Fog;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;
import dreampool.ui.Font;
import dreampool.ui.UIImage;
import dreampool.ui.parts.Image;
import dreampool.ui.parts.Text;

public class ExampleScene {
	public Scene scene;
	private List<Collider> colliders = new ArrayList<>();

	public ExampleScene() {
		scene = new Scene("example");
		scene.fog = new Fog(new Vector4f(0.5f, 0.5f, 0.5f, 1.0f), 10.0f, 50.0f);
		Font arial = new Font("/fonts/Oswald-Regular.ttf");
		Thing player = new Thing("player");
		player.addPart(new Camera());
		player.addPart(new NoiseListener());
		player.addPart(new PlayerController());
		player.transform.position = new Vector3f(4, 0, 1);
		player.transform.rotation = new Vector3f(-90, 0, 0);

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				Thing ball = new Thing("ball" + i + j);
				ball.addPart(new Mesh("/models/bunny.obj", i % 2 >= 1 ? false : true));
				ball.addPart(new SphereCollider());
				ball.addPart(new Texture("/images/white.png"));
				ball.addPart(new Texture("/images/white.png", 1));
				// ball.addPart(new Rotator());
				ball.transform.position = new Vector3f(-i, 1, j);
				scene.addThing(ball);
			}
		}

		Thing fps = new Thing("fps");
		Text text = new Text("Fps: " + Time.fps, 32, 10, 700, 255, 0, 0, 1, arial);
		fps.addPart(text);
		fps.addPart(new FPSDisplay(text));
		Thing image = new Thing("image");
		Image imp = new Image(new UIImage("/images/doge.png"), 1000f, 100f, 200f, 100f, 255, 255, 255, 0.2f);
		image.addPart(imp);
		Thing crosshair = new Thing("crosshair");
		Image crossimage = new Image(new UIImage("/images/doge.png"), (Application.height / 2f),
				(Application.width / 2f), 200f, 200f, 255, 255, 255, 0.2f);
		crosshair.addPart(crossimage);
		crosshair.addPart(new Crosshair(crossimage));
		scene.addThing(player);
		scene.addThing(fps);
		scene.addThing(image);
		scene.addThing(crosshair);
		for (Thing thing : scene.things) {
			Collider collider = thing.getPartExtendsOrImplements("Collider");
			if (collider != null) {
				PlayerController playerController = (PlayerController) player.getPart("PlayerController");
				if (playerController != null) {
					playerController.colliders.add(collider);
				} else {
					System.out.println("No playerController");
				}
			}
		}
	}
}
