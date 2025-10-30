package dreampool.render.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import dreampool.core.Part;
import dreampool.render.RenderCommand;
import dreampool.render.RenderPipeline;
import dreampool.render.RenderStage;
import dreampool.render.texture.Texture;

// TODO implement skeletal animation and fix instancing and possibly add texture atlases.
public class Mesh extends Part {
	public static boolean hitDebug = false;
	public boolean isLoaded = false;
	public boolean inFrustum;
	public boolean flat = true;
	public boolean hit = false;

	public MeshPool.PoolEntry entry;
	public List<Texture> textures = new ArrayList<>();

	public Mesh(String path, boolean flat) {
		this.flat = flat;

		entry = MeshPool.Singleton.registerMesh(path);
	}

	@Override
	public void Update() {
		if (inFrustum) {
			Matrix4f model = new Matrix4f();
			model.translate(transform.position);
			model.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
					(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)));
			model.scale(transform.size);
			RenderPipeline.Singleton.submit(new RenderCommand(RenderStage.GEOMETRY, this, textures, model));
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