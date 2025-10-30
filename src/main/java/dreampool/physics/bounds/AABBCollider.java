package dreampool.physics.bounds;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import dreampool.core.Part;
import dreampool.core.Thing;
import dreampool.render.camera.Camera;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;

public class AABBCollider extends Part implements Bound, Collider {
	private final Vector3f localMin = new Vector3f();
	private final Vector3f localMax = new Vector3f();
	private final Vector3f worldMin = new Vector3f();
	private final Vector3f worldMax = new Vector3f();
	private Mesh mesh;
	private Texture tex;

	public boolean active;

	public AABBCollider(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isOnFrustum() {
		return Camera.Singleton.frustum.testAab(worldMin, worldMax);
	}

	@Override
	public void Start() {
		mesh = thing.getPart(Mesh.class);
		tex = thing.getPart(Texture.class);

		float[] vertices = mesh.entry.vertices;
		if (vertices == null || vertices.length == 0) {
			throw new IllegalArgumentException("No vertices found in mesh: " + mesh);
		}

		final int vertexStride = 8; // (x, y, z, u, v, nx, ny, nz)
		int vertexCount = vertices.length / vertexStride;

		float minX = vertices[0];
		float minY = vertices[1];
		float minZ = vertices[2];
		float maxX = minX, maxY = minY, maxZ = minZ;

		for (int i = 1; i < vertexCount; i++) {
			int base = i * vertexStride;
			float x = vertices[base];
			float y = vertices[base + 1];
			float z = vertices[base + 2];

			if (x < minX)
				minX = x;
			if (x > maxX)
				maxX = x;
			if (y < minY)
				minY = y;
			if (y > maxY)
				maxY = y;
			if (z < minZ)
				minZ = z;
			if (z > maxZ)
				maxZ = z;
		}

		localMin.set(minX, minY, minZ);
		localMax.set(maxX, maxY, maxZ);

		updateWorldBounds();
	}

	private void updateWorldBounds() {
		Matrix4f model = new Matrix4f().translate(transform.position)
				.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
						(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)))
				.scale(transform.size);

		Vector3f[] corners = { new Vector3f(localMin.x, localMin.y, localMin.z),
				new Vector3f(localMax.x, localMin.y, localMin.z), new Vector3f(localMin.x, localMax.y, localMin.z),
				new Vector3f(localMax.x, localMax.y, localMin.z), new Vector3f(localMin.x, localMin.y, localMax.z),
				new Vector3f(localMax.x, localMin.y, localMax.z), new Vector3f(localMin.x, localMax.y, localMax.z),
				new Vector3f(localMax.x, localMax.y, localMax.z) };

		worldMin.set(Float.POSITIVE_INFINITY);
		worldMax.set(Float.NEGATIVE_INFINITY);

		for (Vector3f v : corners) {
			model.transformPosition(v);
			worldMin.min(v);
			worldMax.max(v);
		}
	}

	@Override
	public void Update() {
		updateWorldBounds();
		boolean onFrustum = isOnFrustum();
		mesh.inFrustum = onFrustum;
		tex.inFrustum = onFrustum;
	}

	@Override
	public Vector3f getMin() {
		return worldMin;
	}

	@Override
	public Vector3f getMax() {
		return worldMax;
	}

	// TODO implement other intersection types
	@Override
	public float intersectRay(Vector3f origin, Vector3f dir) {
		if (!active) {
			return 0;
		}
		float result = Intersectionf.intersectRayAab(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, worldMin.x,
				worldMin.y, worldMin.z, worldMax.x, worldMax.y, worldMax.z, new Vector2f()) ? 1f : -1f;

		if (result < 0)
			return -1;

		Vector2f t = new Vector2f();
		if (Intersectionf.intersectRayAab(origin, dir, worldMin, worldMax, t))
			return t.x >= 0 ? t.x : t.y;
		return -1;
	}

	@Override
	public Thing getThing() {
		return thing;
	}
}
