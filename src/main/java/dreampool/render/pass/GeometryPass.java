package dreampool.render.pass;

import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import dreampool.Application;
import dreampool.Window;
import dreampool.core.Time;
import dreampool.render.RenderCommand;
import dreampool.render.RenderStage;
import dreampool.render.camera.Camera;
import dreampool.render.model.Mesh;
import dreampool.render.shader.AdvShader;
import dreampool.render.shader.Shader;
import dreampool.render.texture.Texture;

public class GeometryPass implements RenderPass {
	Shader mainShader = new AdvShader("/shaders/main.vert", "/shaders/main.frag", "/shaders/main.tcs",
			"/shaders/main.tes");
	Vector2f lightDir;

	int lastRendered = 0;

	public GeometryPass() {
		mainShader.use();
		mainShader.setInt("texture1", 0);
		mainShader.setInt("texture2", 1);
		lightDir = new Vector2f(90, 0);
	}

	@Override
	public void start() {
	}

	@Override
	public void render(List<RenderCommand> cmds, Camera camera) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		mainShader.use();
		mainShader.setMat4("projection", Application.projection);
		mainShader.setVec2("targetResolution", (int) (Window.Singleton.width / Application.resDivisor) / 2,
				(int) (Window.Singleton.height / Application.resDivisor) / 2);

		lightDir.add(new Vector2f(75 * Time.deltaTime, 0));

		mainShader.setVec2("lightDir", lightDir);
		mainShader.setVec3("ambientColor", new Vector3f(0.2f, 0.2f, 0.2f));
		mainShader.setVec3("diffuseColor", new Vector3f(1.0f, 1.0f, 1.0f));
		mainShader.setMat4("view", Camera.Singleton.matrix);
		for (RenderCommand cmd : cmds) {
			if (this.lastRendered != cmd.sortKey) {
				GL30.glBindVertexArray(cmd.mesh.entry.VAO);
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cmd.mesh.entry.VBO);
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, cmd.mesh.entry.EBO);
				this.lastRendered = cmd.sortKey;
			}
			mainShader.setMat4("model", cmd.modelMat);
			mainShader.setBool("flatlight", cmd.mesh.flat);
			if (Mesh.hitDebug) {
				mainShader.setBool("hit", cmd.mesh.hit);
			}
			for (Texture texture : cmd.textures) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0 + texture.unit);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.ID);
			}

			GL11.glDrawArrays(GL40.GL_PATCHES, 0, cmd.mesh.entry.vertices.length / 8);
		}
		this.lastRendered = 0;
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
		return RenderStage.GEOMETRY;
	}

	@Override
	public void destroy() {

	}

}
