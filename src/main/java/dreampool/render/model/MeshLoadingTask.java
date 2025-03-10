package dreampool.render.model;

import java.io.IOException;
import java.util.List;

import dreampool.IO.FileUtils;
import dreampool.IO.assets.AssetLoadingTask;

public class MeshLoadingTask implements AssetLoadingTask {
    private String path;
    private List<Float> vertices;
    private List<Integer> indices;

    public MeshLoadingTask(String path, List<Float> vertices, List<Integer> indices) {
	this.path = path;
	this.vertices = vertices;
	this.indices = indices;
    }

    @Override
    public void load() {
	try {
	    FileUtils.readObjMeshResource(path, vertices, indices);
	    onComplete();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void onComplete() {
	// Update the cache after loading
	synchronized (Mesh.meshCache) {
	    Mesh.meshCache.put(path, new Mesh.CacheEntry(vertices, indices));
	}
    }

}
