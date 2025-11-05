package dreampool.render.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import dreampool.IO.FileUtils;
import dreampool.IO.FileUtils.Doublet;

public class MeshPool {
	public static MeshPool Singleton;

	public static class PoolEntry {
		// float[] vertices;
		// int[] indices;
		public int VAO;
		public int VBO;
		public int EBO;
		public float[] vertices;
		public int[] indices;
		int refCount;

		public PoolEntry(int VAO, int VBO, int EBO) {
			this.VAO = VAO;
			this.VBO = VBO;
			this.EBO = EBO;
			this.refCount = 1;
		}

		public PoolEntry() {
			this.VAO = 0;
			this.VBO = 0;
			this.EBO = 0;
			this.refCount = 1;
		}

		public void destroy() {
			GL30.glDeleteVertexArrays(VAO);
			GL15.glDeleteBuffers(VBO);
			GL15.glDeleteBuffers(EBO);
		}
	}

	public Map<String, PoolEntry> meshPool = new HashMap<>();

	public MeshPool() {
		if (Singleton != null) {
			System.out.println("Only 1 MeshPool!");
		} else {
			Singleton = this;
		}
	}

	public PoolEntry registerMesh(String path) {
		synchronized (meshPool) {
			PoolEntry entry = meshPool.get(path);
			if (entry != null) {
				return entry;
			}
		}
		PoolEntry entry = new PoolEntry();
		Doublet raw = null;
		try {
			raw = FileUtils.readObjMeshResource(path);
			entry.vertices = raw.vertices;
			entry.indices = raw.indices;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entry.VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(entry.VAO);

		entry.VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, entry.VBO);

		entry.EBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, entry.EBO);

		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, raw.vertices, GL15.GL_STATIC_DRAW);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, raw.indices, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		meshPool.put(path, entry);
		return entry;
	}

	public void destroy() {
		meshPool.forEach((s, v) -> {
			v.destroy();
		});
		meshPool.clear();
	}
}
