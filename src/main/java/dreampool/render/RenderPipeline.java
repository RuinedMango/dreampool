package dreampool.render;

import java.util.ArrayList;
import java.util.List;

import dreampool.render.camera.Camera;
import dreampool.render.pass.RenderPass;

public class RenderPipeline {
	public static RenderPipeline Singleton;
	private Camera camera;
	private static List<RenderPass> passes = new ArrayList<>();
	private static List<RenderCommand> cmds = new ArrayList<>();

	public RenderPipeline() {
		if (Singleton != null) {
			System.out.println("Only one RenderPipline!");
		} else {
			Singleton = this;
		}
	}

	public void beginFrame() {
		// Nothing yet
	}

	public void addPass(RenderPass pass) {
		passes.add(pass);
	}

	public void execute() {
		for (RenderPass pass : passes) {
			pass.start();
			pass.render(cmds, camera);
			pass.end();
		}
		cmds.clear();
	}

	public void submit(RenderCommand cmd) {
		cmds.add(cmd);
		cmds.sort((a, b) -> Integer.compare(a.sortKey, b.sortKey));
	}

	public void endFrame() {
		// Nothing yet
	}
}
