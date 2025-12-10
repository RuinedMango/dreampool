package dreampool.render.pass;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import dreampool.Application;
import dreampool.WindowSystem;
import dreampool.render.RenderCommand;
import dreampool.render.RenderStage;
import dreampool.render.camera.Camera;
import dreampool.render.shader.PostShader;

public class PostPass implements RenderPass {
	PostShader post = new PostShader("/shaders/dither.frag");
	static float quadVertices[] = { -1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f,
			1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f };
	private int quadVAO;
	private int quadVBO;

	public PostPass() {
		post.use();
		post.setInt("levels", 32);
		post.setInt("screenTexture", 0);
		quadVAO = GL30.glGenVertexArrays();
		quadVBO = GL15.glGenBuffers();
		GL30.glBindVertexArray(quadVAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
		GL20.glEnableVertexAttribArray(1);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(List<RenderCommand> cmds, Camera camera) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL15.glActiveTexture(GL13.GL_TEXTURE0);
		GL15.glBindTexture(GL13.GL_TEXTURE_2D, 0);
		post.use();
		GL11.glViewport(0, 0, WindowSystem.Singleton.width, WindowSystem.Singleton.height);
		GL30.glBindVertexArray(quadVAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, quadVertices, GL15.GL_STATIC_DRAW);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Application.FBOtex);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
	}

	@Override
	public void end() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

	}

	@Override
	public RenderStage getTag() {
		// TODO Auto-generated method stub
		return RenderStage.POST;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
