package dreampool.example.scenes;

import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.audio.NoiseListener;
import dreampool.core.Thing;
import dreampool.core.Time;
import dreampool.core.scene.Scene;
import dreampool.render.Camera;
import dreampool.render.fog.Fog;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;
import dreampool.ui.Font;
import dreampool.ui.parts.Text;

public class ExampleScene {
	public Scene scene;
	
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
		
		for(int i = 0; i < 200; i++) {
			Thing ball = new Thing("ball" + i);
			ball.addPart(new Mesh("/models/Sphere.obj", false));
			ball.addPart(new Texture("/images/white.png"));
			ball.addPart(new Texture("/images/white.png", 1));
			ball.addPart(new Rotator());
			ball.transform.position = new Vector3f(0, 1, i);
			scene.addThing(ball);
		}
		
		Thing fps = new Thing("fps");
		Text text = new Text("Fps: " + Time.fps, 32, 10, 700, 255, 0, 0, 1, arial);
		fps.addPart(text);
		fps.addPart(new FPSDisplay(text));
		scene.addThing(player);
		scene.addThing(fps);
	}
}
