package dreampool.render;

import java.util.ArrayList;
import java.util.List;

import dreampool.core.scene.Scene;
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

	public void execute(Scene scene) {
		for (RenderPass pass : passes) {
			pass.start();
			pass.render(scene, camera);
			pass.end();
		}
	}

	public void submit(RenderCommand cmd) {
		cmds.add(cmd);
	}

	public void endFrame() {
		// Nothing yet
	}
}
