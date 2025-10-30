package dreampool.render.pass;

import dreampool.core.scene.Scene;
import dreampool.render.camera.Camera;

public interface RenderPass {

	public void start();

	public void render(Scene scene, Camera camera);

	public void end();
}
