package dreampool.render.model;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import dreampool.Application;
import dreampool.core.Part;
import dreampool.render.RenderCommand;
import dreampool.render.RenderPipeline;

// TODO implement skeletal animation and fix instancing and possibly add texture atlases.
public class Mesh extends Part {
	private boolean hitDebug = false;
	public boolean isLoaded = false;
	public boolean inFrustum;
	private boolean flat = true;
	public boolean hit = false;

	public MeshPool.PoolEntry entry;

	public Mesh(String path, boolean flat) {
		this.flat = flat;

		entry = MeshPool.registerMesh(path);
	}

	@Override
	public void Update() {
		if (inFrustum) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			Application.mainShader.use();
			GL30.glBindVertexArray(entry.VAO);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, entry.VBO);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, entry.EBO);
			Matrix4f model = new Matrix4f();
			model.translate(transform.position);
			model.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
					(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)));
			model.scale(transform.size);
			Application.mainShader.setMat4("model", model);
			Application.mainShader.setBool("flatlight", flat);
			if (hitDebug) {
				Application.mainShader.setBool("hit", hit);
			}
			GL11.glDrawArrays(GL40.GL_PATCHES, 0, entry.vertices.length / 8);
			RenderPipeline.Singleton.submit(new RenderCommand(this, model));
			// GL46.glDrawElements(GL46.GL_TRIANGLES, entry.indices.length,
			// GL46.GL_UNSIGNED_INT, 0);
		} else {
			return;
		}
	}

	public void destroy() {
		entry.destroy();
	}
}