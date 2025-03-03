package dreampool.example.scenes;

import org.joml.Vector3f;
import org.joml.Vector4f;
import dreampool.audio.NoiseListener;
import dreampool.audio.NoiseSource;
import dreampool.core.Thing;
import dreampool.core.scene.Scene;
import dreampool.render.Camera;
import dreampool.render.fog.Fog;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;

public class ExampleScene {
	public Scene scene;
	
	public ExampleScene() {
		scene = new Scene("example");
		scene.fog = new Fog(new Vector4f(0.5f, 0.5f, 0.5f, 1.0f), 10.0f, 50.0f);
		Thing player = new Thing("player");
		player.addPart(new Camera());
		player.addPart(new NoiseListener());
		player.addPart(new PlayerController());
		player.transform.position = new Vector3f(4, 0, 1);
		player.transform.rotation = new Vector3f(-90, 0, 0);
		Thing ball1 = new Thing("ball1");
		ball1.addPart(new Mesh("/models/Sphere.obj", true));
		ball1.addPart(new Texture("/images/white.png"));
		ball1.addPart(new Texture("/images/white.png", 1));
		Thing ball2 = new Thing("ball2");
		ball2.addPart(new Mesh("/models/Sphere.obj", false));
		ball2.addPart(new Texture("/images/white.png"));
		ball2.addPart(new Texture("/images/white.png", 1));
		ball2.transform.position = new Vector3f(0, 0, 2);
		Thing sound = new Thing("emitter");
		NoiseSource soundsource = new NoiseSource("/audio/yell.ogg", false, true);
		sound.addPart(soundsource);
		sound.addPart(new Mesh("/models/FaceCube.obj", true));
		sound.addPart(new Texture("/images/face.png"));
		sound.addPart(new Texture("/images/doge.png", 1));
		sound.transform.position = new Vector3f(-2f, 2f, 2f);
		ball1.addPart(new Rotator());
		ball2.addPart(new Rotator());
		scene.addThing(ball1);
		scene.addThing(ball2);
		scene.addThing(sound);
		scene.addThing(player);
		soundsource.play();
	}
}
