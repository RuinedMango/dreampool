package dreampool.render.pass;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import dreampool.WindowSystem;
import dreampool.render.RenderCommand;
import dreampool.render.RenderStage;
import dreampool.render.camera.Camera;
import dreampool.render.shader.BasicShader;
import dreampool.render.texture.Texture;

public class UIPass implements RenderPass {
	public static BasicShader shader = new BasicShader("/shaders/ui.vert", "/shaders/ui.frag");

	@Override
	public void start() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void render(List<RenderCommand> cmds, Camera camera) {
		shader.use();

		shader.setInt("uTexture", 0);
		shader.setMat4("projection",
				new Matrix4f().ortho(0, WindowSystem.Singleton.width, 0, WindowSystem.Singleton.height, -1, 1));
		for (RenderCommand cmd : cmds) {
			GL30.glBindVertexArray(cmd.mesh.entry.VAO);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cmd.mesh.entry.VBO);
			FloatBuffer fb = BufferUtils.createFloatBuffer(cmd.mesh.entry.vertices.length);
			fb.put(cmd.mesh.entry.vertices).flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fb);

			for (Texture texture : cmd.mesh.textures) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.ID);
			}
			if (cmd.mesh.entry.EBO == -2) {
				shader.setBool("uGrayscale", true);
				shader.setBool("uFlat", false);
			} else if (cmd.mesh.entry.EBO == -1) {
				shader.setBool("uGrayscale", false);
				shader.setBool("uFlat", false);
			} else if (cmd.mesh.entry.EBO == -3) {
				shader.setBool("uFlat", true);
			}

			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cmd.mesh.entry.vertices.length / 9);
			FloatBuffer zeroBuffer = BufferUtils.createFloatBuffer(1024 * 1024 / 4);
			zeroBuffer.put(new float[1024 * 1024 / 4]).flip();

			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, zeroBuffer);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
	}

	@Override
	public void end() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public RenderStage getTag() {
		return RenderStage.UI;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
