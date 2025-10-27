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

public class SphereCollider extends Part implements Bound, Collider {
	public Mesh mesh;
	public Texture texture;
	private Vector3f centerLocal = new Vector3f();
	private float radiusLocal = 0.0f;

	private Vector3f centerWorld = new Vector3f();
	private float radiusWorld = 0.0f;

	public boolean active;

	public SphereCollider(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isOnFrustum() {
		return Camera.Singleton.frustum.testSphere(centerWorld, radiusWorld);
	}

	@Override
	public void Start() {
		mesh = (Mesh) thing.getPart("Mesh");
		texture = (Texture) thing.getPart("Texture");

		float[] vertices = mesh.vertexArray;
		if (vertices == null || vertices.length < 3) {
			centerWorld = new Vector3f(0, 0, 0);
			radiusWorld = 0;
			return;
		}

		computeSphere(vertices);
		updateWorldSpace();
	}

	// TODO fix for bunny model (strangely large)
	public void computeSphere(float[] vertices) {
		final int stride = 8; // x, y, z, nx, ny, nz, u, v

		int minXIndex = 0;
		int maxXIndex = 0;
		float minX = vertices[0];
		float maxX = vertices[0];

		for (int i = 0; i < vertices.length; i += stride) {
			float x = vertices[i];
			if (x < minX) {
				minX = x;
				minXIndex = i;
			}
			if (x > maxX) {
				maxX = x;
				maxXIndex = i;
			}
		}

		float[] minXPos = { vertices[minXIndex], vertices[minXIndex + 1], vertices[minXIndex + 2] };
		float[] maxXPos = { vertices[maxXIndex], vertices[maxXIndex + 1], vertices[maxXIndex + 2] };

		centerLocal = new Vector3f((minXPos[0] + maxXPos[0]) / 2f, (minXPos[1] + maxXPos[1]) / 2f,
				(minXPos[2] + maxXPos[2]) / 2f);
		radiusLocal = distance(maxXPos, new float[] { centerLocal.x, centerLocal.y, centerLocal.z });

		for (int i = 0; i < vertices.length; i += stride) {
			float vx = vertices[i];
			float vy = vertices[i + 1];
			float vz = vertices[i + 2];

			Vector3f vertex = new Vector3f(vx, vy, vz);
			float dist = centerLocal.distance(vertex);
			if (dist > radiusLocal) {
				Vector3f dir = new Vector3f(vertex).sub(centerLocal);
				float adjustment = (dist - radiusLocal) / (2f * dist);
				centerLocal.add(dir.mul(adjustment));
				radiusLocal = (radiusLocal + dist) / 2f;
			}
		}
	}

	private void updateWorldSpace() {
		Matrix4f model = new Matrix4f().translate(transform.position)
				.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
						(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)))
				.scale(transform.size);
		Matrix4f modelMatrix = model;
		Vector4f worldCenter = new Vector4f(centerLocal.x, centerLocal.y, centerLocal.z, 1.0f).mul(modelMatrix);

		this.centerWorld = new Vector3f(worldCenter.x, worldCenter.y, worldCenter.z);

		float maxScale = Math.max(Math.max(transform.size.x, transform.size.y), transform.size.z);
		this.radiusWorld = radiusLocal * maxScale;
	}

	private float distance(float[] a, float[] b) {
		float dx = a[0] - b[0];
		float dy = a[1] - b[1];
		float dz = a[2] - b[2];
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@Override
	public void Update() {
		updateWorldSpace();
		mesh.inFrustum = isOnFrustum();
		texture.inFrustum = isOnFrustum();
	}

	@Override
	public Vector3f getMin() {
		return new Vector3f(centerWorld).sub(radiusWorld, radiusWorld, radiusWorld);
	}

	@Override
	public Vector3f getMax() {
		return new Vector3f(centerWorld).add(radiusWorld, radiusWorld, radiusWorld);
	}

	@Override
	public Thing getThing() {
		return thing;
	}

	// TODO implement other intersection types
	@Override
	public float intersectRay(Vector3f origin, Vector3f dir) {
		if (!active) {
			return 0;
		}
		Vector2f t = new Vector2f();
		if (Intersectionf.intersectRaySphere(origin, dir, centerWorld, radiusWorld, t))
			return t.x >= 0 ? t.x : t.y;
		return -1;
	}
}
