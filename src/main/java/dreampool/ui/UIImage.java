package dreampool.ui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import dreampool.Application;
import dreampool.render.BasicShader;
import dreampool.render.texture.Texture;

public class UIImage {
	public static Matrix4f altProj = null;
	private Texture texture;
	private static Map<String, Texture> textureCache = new HashMap<>();

	private List<Float> vertices = new ArrayList<>();
	private boolean vertset = false;

	public static BasicShader shader = new BasicShader("/shaders/ui.vert", "/shaders/ui.frag");
	static int VAO;
	static int VBO;

	private Vector3f oldPosition = new Vector3f();
	private Vector4f oldColor = new Vector4f();
	private Vector2f oldSize = new Vector2f(0, 0);

	public UIImage(String path) {
		synchronized (textureCache) {
			if (textureCache.containsKey(path)) {
				texture = textureCache.get(path);
			} else {
				texture = new Texture(path);
				textureCache.put(path, texture);
			}
		}

		setupVAOandVBO();
	}

	public void renderQuad(Vector3f position, Vector4f color, Vector2f size) {

		if (!position.equals(oldPosition.x, oldPosition.y, oldPosition.z)
				|| !color.equals(oldColor.x, oldColor.y, oldColor.z, oldColor.w)
				|| !size.equals(oldSize.x, oldSize.y)) {
			vertset = false;
			oldPosition.set(position);
			oldColor.set(color);
			oldSize.set(size);
		}
		if (!vertset) {
			vertices.clear();

			int[] order = { 0, 1, 2, 0, 2, 3 };

			Vector3f topLeft = new Vector3f(position.x, position.y + size.y, position.z);
			Vector3f topRight = new Vector3f(position.x + size.x, position.y + size.y, position.z);
			Vector3f bottomLeft = new Vector3f(position.x, position.y, position.z);
			Vector3f bottomRight = new Vector3f(position.x + size.x, position.y, position.z);

			Vector3f[] quadVertices = { topRight, topLeft, bottomLeft, bottomRight };
			Vector2f[] quadUVs = { new Vector2f(1, 1), new Vector2f(0, 1), new Vector2f(0, 0), new Vector2f(1, 0) };

			for (int i = 0; i < 6; i++) {
				int idx = order[i];
				vertices.add(quadVertices[idx].x);
				vertices.add(quadVertices[idx].y);
				vertices.add(quadVertices[idx].z);

				vertices.add(color.x);
				vertices.add(color.y);
				vertices.add(color.z);
				vertices.add(color.w);

				vertices.add(quadUVs[idx].x);
				vertices.add(quadUVs[idx].y);
			}

			vertset = true;
		}

		int vertexCount = vertices.size() / 9;

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.use();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.ID);

		shader.setInt("uTexture", 0);
		if (altProj == null) {
			shader.setMat4("projection", new Matrix4f().ortho(0, Application.width, 0, Application.height, -1, 1));
		} else {
			shader.setMat4("projection", altProj);
		}

		GL30.glBindVertexArray(VAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		float[] verticeArray = new float[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			verticeArray[i] = vertices.get(i);
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(verticeArray.length);
		fb.put(verticeArray).flip();
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fb);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	static void setupVAOandVBO() {
		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 1024 * 1024, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Float.BYTES * 9, 0);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 3);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 7);
		GL20.glEnableVertexAttribArray(2);

		GL30.glBindVertexArray(0);
	}

	public void destroy() {
		if (texture != null) {
			texture.destroy();
		}
	}
}
