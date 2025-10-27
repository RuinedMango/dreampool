package dreampool.physics.bounds;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.core.Part;
import dreampool.core.Thing;
import dreampool.render.camera.Camera;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;

public class AABBCollider extends Part implements Bound, Collider {
	private Vector3f max;
	private Vector3f min;
	private Mesh mesh;
	private Texture tex;

	@Override
	public boolean isOnFrustum() {
		Vector4f[] planes = Camera.Singleton.frustum.planes;

		for (Vector4f plane : planes) {
			Vector3f v = new Vector3f(plane.x > 0 ? max.x : min.x, plane.y > 0 ? max.y : min.y,
					plane.z > 0 ? max.z : min.z);

			if (plane.x * v.x + plane.y * v.y + plane.z * v.z + plane.w < 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void Start() {
		mesh = (Mesh) thing.getPart("Mesh");
		tex = (Texture) thing.getPart("Texture");

		float[] vertices = mesh.vertexArray; // ✅ Use raw float array now
		if (vertices == null || vertices.length == 0) {
			throw new IllegalArgumentException("No vertices found in mesh: " + mesh);
		}

		// Compute bounds in local space
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

		min = new Vector3f(minX, minY, minZ);
		max = new Vector3f(maxX, maxY, maxZ);

		updateWorldBounds();
	}

	private void updateWorldBounds() {
		Matrix4f model = new Matrix4f().translate(transform.position)
				.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
						(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)))
				.scale(transform.size);

		// Compute 8 corners in world space
		Vector3f[] corners = { new Vector3f(min.x, min.y, min.z), new Vector3f(max.x, min.y, min.z),
				new Vector3f(min.x, max.y, min.z), new Vector3f(max.x, max.y, min.z), new Vector3f(min.x, min.y, max.z),
				new Vector3f(max.x, min.y, max.z), new Vector3f(min.x, max.y, max.z),
				new Vector3f(max.x, max.y, max.z) };

		Vector3f first = corners[0].mulPosition(model, new Vector3f());
		float wMinX = first.x, wMaxX = first.x;
		float wMinY = first.y, wMaxY = first.y;
		float wMinZ = first.z, wMaxZ = first.z;

		for (int i = 1; i < corners.length; i++) {
			Vector3f v = corners[i].mulPosition(model, new Vector3f());
			wMinX = Math.min(wMinX, v.x);
			wMaxX = Math.max(wMaxX, v.x);
			wMinY = Math.min(wMinY, v.y);
			wMaxY = Math.max(wMaxY, v.y);
			wMinZ = Math.min(wMinZ, v.z);
			wMaxZ = Math.max(wMaxZ, v.z);
		}

		min.set(wMinX, wMinY, wMinZ);
		max.set(wMaxX, wMaxY, wMaxZ);
	}

	@Override
	public void Update() {
		boolean onFrustum = isOnFrustum();
		mesh.inFrustum = onFrustum;
		tex.inFrustum = onFrustum;
	}

	@Override
	public Vector3f getMin() {
		return min;
	}

	@Override
	public Vector3f getMax() {
		return max;
	}

	@Override
	public float intersectRay(Vector3f origin, Vector3f dir) {
		float result = Intersectionf.intersectRayAab(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, min.x, min.y,
				min.z, max.x, max.y, max.z, new Vector2f()) ? 1f : -1f;

		if (result < 0)
			return -1;

		Vector2f t = new Vector2f();
		if (Intersectionf.intersectRayAab(origin, dir, min, max, t))
			return t.x >= 0 ? t.x : t.y;
		return -1;
	}

	@Override
	public Thing getThing() {
		return thing;
	}
}
