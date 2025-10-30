package dreampool.render.pass;

import java.util.List;

import dreampool.render.RenderCommand;
import dreampool.render.RenderStage;
import dreampool.render.camera.Camera;

public class UIPass implements RenderPass {

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(List<RenderCommand> cmds, Camera camera) {
		// TODO Auto-generated method stub

	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

	@Override
	public RenderStage getTag() {
		// TODO Auto-generated method stub
		return RenderStage.UI;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
