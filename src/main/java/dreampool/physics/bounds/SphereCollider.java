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

public class SphereCollider extends Part implements Bound, Collider {
	public Mesh mesh;
	private Vector3f centerWorld;
	private float radiusWorld;

	@Override
	public boolean isOnFrustum() {
		Vector4f[] planes = Camera.Singleton.frustum.planes;
		for (Vector4f plane : planes) {
			float distance = plane.x * centerWorld.x + plane.y * centerWorld.y + plane.z * centerWorld.z + plane.w;
			if (distance < -radiusWorld) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void Start() {
		mesh = (Mesh) thing.getPart("Mesh");

		float[] vertices = mesh.vertexArray;
		if (vertices == null || vertices.length < 3) {
			centerWorld = new Vector3f(0, 0, 0);
			radiusWorld = 0;
			return;
		}

		computeSphere(vertices);
	}

	public void computeSphere(float[] vertices) {
		final int stride = 8; // x, y, z, nx, ny, nz, u, v

		// --- Step 1: Find min/max X positions ---
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

		// --- Step 2: Initial model-space sphere ---
		Vector3f modelSpaceCenter = new Vector3f((minXPos[0] + maxXPos[0]) / 2f, (minXPos[1] + maxXPos[1]) / 2f,
				(minXPos[2] + maxXPos[2]) / 2f);
		float modelSpaceRadius = distance(maxXPos,
				new float[] { modelSpaceCenter.x, modelSpaceCenter.y, modelSpaceCenter.z });

		// --- Step 3: Expand to include all vertices ---
		for (int i = 0; i < vertices.length; i += stride) {
			float vx = vertices[i];
			float vy = vertices[i + 1];
			float vz = vertices[i + 2];

			Vector3f vertex = new Vector3f(vx, vy, vz);
			float dist = modelSpaceCenter.distance(vertex);
			if (dist > modelSpaceRadius) {
				Vector3f dir = new Vector3f(vertex).sub(modelSpaceCenter);
				float adjustment = (dist - modelSpaceRadius) / (2f * dist);
				modelSpaceCenter.add(dir.mul(adjustment));
				modelSpaceRadius = (modelSpaceRadius + dist) / 2f;
			}
		}

		// --- Step 4: Transform to world space ---
		Matrix4f modelMatrix = computeModelMatrix();
		Vector4f worldCenter = new Vector4f(modelSpaceCenter.x, modelSpaceCenter.y, modelSpaceCenter.z, 1.0f)
				.mul(modelMatrix);

		this.centerWorld = new Vector3f(worldCenter.x, worldCenter.y, worldCenter.z);

		float maxScale = Math.max(Math.max(transform.size.x, transform.size.y), transform.size.z);
		this.radiusWorld = modelSpaceRadius * maxScale;
	}

	private Matrix4f computeModelMatrix() {
		return new Matrix4f().translate(transform.position)
				.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
						(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)))
				.scale(transform.size);
	}

	private float distance(float[] a, float[] b) {
		float dx = a[0] - b[0];
		float dy = a[1] - b[1];
		float dz = a[2] - b[2];
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@Override
	public void Update() {
		mesh.inFrustum = isOnFrustum();
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

	@Override
	public float intersectRay(Vector3f origin, Vector3f dir) {
		Vector2f t = new Vector2f();
		if (Intersectionf.intersectRaySphere(origin, dir, centerWorld, radiusWorld, t))
			return t.x >= 0 ? t.x : t.y;
		return -1;
	}
}
