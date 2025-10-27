package dreampool.physics.bounds;

import org.joml.Vector3f;

import dreampool.core.Thing;

public interface Collider {
	Vector3f getMin();

	Vector3f getMax();

	Thing getThing();

	// TODO support other intersection types
	float intersectRay(Vector3f origin, Vector3f dir);
}
