package dreampool.render.pass;

import java.util.List;

import dreampool.render.RenderCommand;
import dreampool.render.RenderStage;
import dreampool.render.camera.Camera;

public interface RenderPass {
	public RenderStage getTag();

	public void start();

	public void render(List<RenderCommand> cmds, Camera camera);

	public void end();

	public void destroy();
}
