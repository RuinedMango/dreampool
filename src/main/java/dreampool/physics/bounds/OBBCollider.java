package dreampool.physics.bounds;

import org.joml.Vector3f;

import dreampool.core.Part;
import dreampool.core.Thing;

// TODO Eventually implement
public class OBBCollider extends Part implements Bound, Collider {

	@Override
	public boolean isOnFrustum() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vector3f getMin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3f getMax() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Thing getThing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float intersectRay(Vector3f origin, Vector3f dir) {
		// TODO Auto-generated method stub
		return 0;
	}

}
