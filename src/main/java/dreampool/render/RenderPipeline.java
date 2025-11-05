package dreampool.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import dreampool.Application;
import dreampool.Window;
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
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, Application.FBO);
		GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void addPass(RenderPass pass) {
		passes.add(pass);
	}

	public void execute() {
		for (RenderPass pass : passes) {
			pass.start();
			List<RenderCommand> suitableCommands = new ArrayList<>();
			for (RenderCommand cmd : cmds) {
				if (cmd.target == pass.getTag()) {
					suitableCommands.add(cmd);
				}
			}
			pass.render(suitableCommands, camera);
			pass.end();
		}
		cmds.clear();
	}

	public void submit(RenderCommand cmd) {
		cmds.add(cmd);
		cmds.sort((a, b) -> Integer.compare(a.sortKey, b.sortKey));
	}

	public void endFrame() {
		GLFW.glfwSwapBuffers(Window.Singleton.ID);
		GLFW.glfwPollEvents();
	}

	public void destroy() {
		for (RenderPass pass : passes) {
			pass.destroy();
		}
	}
}
