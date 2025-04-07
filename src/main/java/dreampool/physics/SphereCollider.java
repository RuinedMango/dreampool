package dreampool.physics;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.core.Part;
import dreampool.render.camera.Camera;
import dreampool.render.model.Mesh;

public class SphereCollider extends Part implements Bound {
    public Mesh mesh;
    private Vector3f centerWorld; // World space center
    private float radiusWorld; // World space radius

    @Override
    public boolean isOnFrustum() {
	Vector4f[] planes = Camera.Singleton.frustum.planes;
	for (Vector4f plane : planes) {
	    // Calculate signed distance from sphere center to the plane
	    float distance = plane.x * centerWorld.x + plane.y * centerWorld.y + plane.z * centerWorld.z + plane.w;
	    if (distance < -radiusWorld) {
		return false; // Sphere is entirely outside the frustum
	    }
	}
	return true; // Sphere intersects or is inside the frustum
    }

    @Override
    public void Start() {
	mesh = (Mesh) thing.getPart("Mesh");

	// Compute model space bounding sphere (before transformations)
	List<Float> vertices = mesh.vertices;
	int vertexStride = 8;

	if (vertices.isEmpty() || vertexStride < 3) {
	    centerWorld = new Vector3f(0, 0, 0);
	    radiusWorld = 0;
	    return;
	}

	// Step 1: Find initial farthest points along X-axis (model space)
	int minXIndex = 0;
	int maxXIndex = 0;
	float minX = getPositionComponent(vertices, 0, 0, vertexStride);
	float maxX = minX;

	for (int i = 0; i < vertices.size(); i += vertexStride) {
	    float x = getPositionComponent(vertices, i, 0, vertexStride);
	    if (x < minX) {
		minX = x;
		minXIndex = i;
	    }
	    if (x > maxX) {
		maxX = x;
		maxXIndex = i;
	    }
	}

	// Extract positions of min/max X vertices (model space)
	float[] minXPos = getPosition(vertices, minXIndex, vertexStride);
	float[] maxXPos = getPosition(vertices, maxXIndex, vertexStride);

	// Initial sphere in model space
	Vector3f modelSpaceCenter = new Vector3f((minXPos[0] + maxXPos[0]) / 2, (minXPos[1] + maxXPos[1]) / 2,
		(minXPos[2] + maxXPos[2]) / 2);
	float modelSpaceRadius = distance(maxXPos,
		new float[] { modelSpaceCenter.x, modelSpaceCenter.y, modelSpaceCenter.z });

	// Expand sphere to include all vertices (model space)
	for (int i = 0; i < vertices.size(); i += vertexStride) {
	    float[] pos = getPosition(vertices, i, vertexStride);
	    Vector3f vertex = new Vector3f(pos[0], pos[1], pos[2]);
	    float dist = modelSpaceCenter.distance(vertex);
	    if (dist > modelSpaceRadius) {
		Vector3f dir = new Vector3f(vertex).sub(modelSpaceCenter);
		float adjustment = (dist - modelSpaceRadius) / (2 * dist);
		modelSpaceCenter.add(dir.mul(adjustment));
		modelSpaceRadius = (modelSpaceRadius + dist) / 2;
	    }
	}

	// Transform model space center to world space
	Matrix4f modelMatrix = computeModelMatrix();
	Vector4f worldCenter = new Vector4f(modelSpaceCenter.x, modelSpaceCenter.y, modelSpaceCenter.z, 1.0f)
		.mul(modelMatrix);
	this.centerWorld = new Vector3f(worldCenter.x, worldCenter.y, worldCenter.z);

	// Scale radius by the maximum scale factor
	float maxScale = Math.max(Math.max(transform.size.x, transform.size.y), transform.size.z);
	this.radiusWorld = modelSpaceRadius * maxScale;
    }

    private Matrix4f computeModelMatrix() {
	Matrix4f model = new Matrix4f().scale(transform.size)
		.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
			(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)))
		.translate(transform.position);
	return model;
    }

    // Helper methods (unchanged)
    private float[] getPosition(List<Float> vertices, int index, int stride) {
	return new float[] { vertices.get(index), vertices.get(index + 1), vertices.get(index + 2) };
    }

    private float getPositionComponent(List<Float> vertices, int vertexIndex, int component, int stride) {
	return vertices.get(vertexIndex + component);
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
}