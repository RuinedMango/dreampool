package dreampool.physics;

import dreampool.core.Part;

public class AABBCollider extends Part implements Bound{

	@Override
	public boolean isOnFrustum() {
		
		return false;
	}

}
