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

public class WienerScene {
	public Scene scene;
	
	public WienerScene() {
		scene = new Scene("winer");
		scene.fog = new Fog(new Vector4f(1.0f, 0.969f, 0.0f, 1.0f), 10.0f, 50.0f);
		Thing player = new Thing("player");
		player.addPart(new Camera());
		player.addPart(new NoiseListener());
		player.addPart(new PlayerController());
		player.transform.position = new Vector3f(0, 0, 0);
		Thing ball1 = new Thing("ball1");
		ball1.addPart(new Mesh("/models/sphere.obj", false));
		ball1.addPart(new Texture("/images/white.png"));
		ball1.addPart(new Texture("/images/white.png", 1));
		Thing ball2 = new Thing("ball2");
		ball2.addPart(new Mesh("/models/sphere.obj", false));
		ball2.addPart(new Texture("/images/white.png"));
		ball2.addPart(new Texture("/images/white.png", 1));
		ball2.transform.position = new Vector3f(0, 0, 2);
		Thing shaft = new Thing("shaft");		
		shaft.addPart(new Mesh("/models/FaceCube.obj", false));
		shaft.addPart(new Texture("/images/face.png"));
		shaft.addPart(new Texture("/images/doge.png", 1));
		shaft.transform.position = new Vector3f(0, 20, 1);
		shaft.transform.size = new Vector3f(0.5f, 20f, 0.5f);
		Thing sound = new Thing("testicle");
		NoiseSource soundsource = new NoiseSource("/audio/yell.ogg", false, true);
		sound.addPart(soundsource);
		sound.addPart(new Mesh("/models/FaceCube.obj", false));
		sound.addPart(new Texture("/images/face.png"));
		sound.addPart(new Texture("/images/doge.png", 1));
		sound.transform.position = new Vector3f(-2f, 2f, 2f);
		scene.addThing(ball1);
		scene.addThing(ball2);
		scene.addThing(shaft);
		scene.addThing(sound);
		scene.addThing(player);
		soundsource.play();
	}
}
