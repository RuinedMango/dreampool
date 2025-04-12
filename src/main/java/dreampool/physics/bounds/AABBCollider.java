package dreampool.physics.bounds;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import dreampool.core.Part;
import dreampool.render.camera.Camera;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;

public class AABBCollider extends Part implements Bound {
    Vector3f max;
    Vector3f min;
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

	Matrix4f model = new Matrix4f();
	model.scale(transform.size);
	model.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x),
		(float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)));
	model.translate(transform.position);

	float minX, minY, minZ;
	float maxX, maxY, maxZ;

	List<Float> vertices = mesh.vertices;
	int vertexCount = vertices.size() / 8;
	int positionOffset = 0;
	int vertexStride = 8;
	if (vertexCount == 0) {
	    throw new IllegalArgumentException("No vertices");
	}

	// Initialize with first vertex's position
	int firstPosIndex = positionOffset;
	minX = vertices.get(firstPosIndex);
	minY = vertices.get(firstPosIndex + 1);
	minZ = vertices.get(firstPosIndex + 2);
	maxX = minX;
	maxY = minY;
	maxZ = minZ;

	// Process remaining vertices
	for (int i = 1; i < vertexCount; i++) {
	    int posIndex = i * vertexStride + positionOffset;

	    float x = vertices.get(posIndex);
	    float y = vertices.get(posIndex + 1);
	    float z = vertices.get(posIndex + 2);

	    // Update bounds
	    if (x < minX) {
		minX = x;
	    }
	    if (x > maxX) {
		maxX = x;
	    }
	    if (y < minY) {
		minY = y;
	    }
	    if (y > maxY) {
		maxY = y;
	    }
	    if (z < minZ) {
		minZ = z;
	    }
	    if (z > maxZ) {
		maxZ = z;
	    }
	}

	Vector3f[] corners = { new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ),
		new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), new Vector3f(minX, minY, maxZ),
		new Vector3f(maxX, minY, maxZ), new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ) };

	// Transform all corners to world space
	Vector3f first = corners[0].mulPosition(model);
	float wMinX = first.x, wMaxX = first.x;
	float wMinY = first.y, wMaxY = first.y;
	float wMinZ = first.z, wMaxZ = first.z;

	for (int i = 1; i < 8; i++) {
	    Vector3f transformed = corners[i].mulPosition(model);
	    wMinX = Math.min(wMinX, transformed.x);
	    wMaxX = Math.max(wMaxX, transformed.x);
	    wMinY = Math.min(wMinY, transformed.y);
	    wMaxY = Math.max(wMaxY, transformed.y);
	    wMinZ = Math.min(wMinZ, transformed.z);
	    wMaxZ = Math.max(wMaxZ, transformed.z);
	}

	min = new Vector3f(wMinX, wMinY, wMinZ);
	max = new Vector3f(wMaxX, wMaxY, wMaxZ);
    }

    @Override
    public void Update() {
	boolean onFrus = isOnFrustum();
	mesh.inFrustum = onFrus;
	tex.inFrustum = onFrus;

    }
}