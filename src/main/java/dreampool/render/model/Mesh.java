package dreampool.render.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import dreampool.Application;
import dreampool.IO.FileUtils;
import dreampool.core.Part;

// TODO implement skeletal animation and fix instancing and possibly add texture atlases.
public class Mesh extends Part {
	public boolean isLoaded = false;
	public boolean inFrustum;
	private String path;
	private boolean flat = true;
	public boolean hit = false;

	public float[] vertexArray;
	public int[] indiceArray;

	public static class CacheEntry {
		float[] vertices;
		int[] indices;
		int refCount;

		public CacheEntry(float[] vertices, int[] indices) {
			this.vertices = vertices;
			this.indices = indices;
			this.refCount = 1;
		}
	}

	public static Map<String, CacheEntry> meshCache = new HashMap<>();

	public Mesh(String path, boolean flat) {
		this.path = path;
		this.flat = flat;

		synchronized (meshCache) {
			CacheEntry entry = meshCache.get(path);
			if (entry != null) {
				this.vertexArray = entry.vertices;
				this.indiceArray = entry.indices;
				entry.refCount++;
				return;
			}
		}

		// Do NOT cache here! Wait for the task to call onComplete().
		try {
			meshCache.put(path, FileUtils.readObjMeshResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		synchronized (meshCache) {
			CacheEntry entry = meshCache.get(path);
			if (entry != null) {
				this.vertexArray = entry.vertices;
				this.indiceArray = entry.indices;
			}
		}
	}

	@Override
	public void Update() {
		if (inFrustum) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			Application.mainShader.use();
			GL30.glBindVertexArray(Application.VAO);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, Application.VBO);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_STATIC_DRAW);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceArray, GL15.GL_STATIC_DRAW);
			Matrix4f model = new Matrix4f();
			model.translate(transform.position);
			model.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
					(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)));
			model.scale(transform.size);
			Application.mainShader.setMat4("model", model);
			Application.mainShader.setBool("flatlight", flat);
			Application.mainShader.setBool("hit", hit);
			GL11.glDrawArrays(GL40.GL_PATCHES, 0, vertexArray.length);
			// GL46.glDrawElements(GL46.GL_TRIANGLES, indices.size(), GL46.GL_UNSIGNED_INT,
			// 0);
		} else {
			return;
		}
	}

	public void destroy() {
		synchronized (meshCache) {
			CacheEntry entry = meshCache.get(path);
			if (entry != null) {
				entry.refCount--;
				if (entry.refCount <= 0) {
					meshCache.remove(path);
				}
			}
		}
	}
}