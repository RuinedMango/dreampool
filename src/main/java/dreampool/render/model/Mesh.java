package dreampool.render.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import dreampool.Application;
import dreampool.IO.FileUtils;
import dreampool.core.Part;

public class Mesh extends Part {
    public boolean isLoaded = false;
    public boolean inFrustum;
    private String path;
    private boolean flat = true;
    public List<Float> vertices = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    float[] vertexArray;
    float[] indiceArray;

    public static class CacheEntry {
	List<Float> vertices;
	List<Integer> indices;
	int refCount;

	CacheEntry(List<Float> vertices, List<Integer> indices) {
	    this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
	    this.indices = Collections.unmodifiableList(new ArrayList<>(indices));
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
		this.vertices = entry.vertices;
		this.indices = entry.indices;
		entry.refCount++;
		return;
	    }
	}

	// Use thread-safe lists
	List<Float> loadedVertices = new CopyOnWriteArrayList<>();
	List<Integer> loadedIndices = new CopyOnWriteArrayList<>();

	// Submit the task
	try {
	    FileUtils.readObjMeshResource(path, loadedVertices, loadedIndices);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// Do NOT cache here! Wait for the task to call onComplete().
	meshCache.put(path, new Mesh.CacheEntry(loadedVertices, loadedIndices));
	synchronized (meshCache) {
	    CacheEntry entry = meshCache.get(path);
	    if (entry != null) {
		this.vertices = entry.vertices;
		this.indices = entry.indices;
	    }
	}
    }

    @Override
    public void Start() {
	vertexArray = new float[vertices.size()];
	for (int i = 0; i < vertices.size(); i++) {
	    vertexArray[i] = vertices.get(i);
	}
	indiceArray = new float[indices.size()];
	for (int i = 0; i < indices.size(); i++) {
	    indiceArray[i] = indices.get(i);
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
	    GL11.glDrawArrays(GL40.GL_PATCHES, 0, vertices.size());
	    // GL46.glDrawElements(GL46.GL_TRIANGLES, indices.size(), GL46.GL_UNSIGNED_INT,
	    // 0);
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