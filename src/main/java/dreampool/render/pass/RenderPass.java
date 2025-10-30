package dreampool.render.pass;

import java.util.List;

import dreampool.render.RenderCommand;
import dreampool.render.camera.Camera;

public interface RenderPass {

	public void start();

	public void render(List<RenderCommand> cmds, Camera camera);

	public void end();
}
