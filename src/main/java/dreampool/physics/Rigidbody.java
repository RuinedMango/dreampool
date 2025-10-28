package dreampool.physics;

import dreampool.core.Part;
import dreampool.physics.bounds.Bound;

// TODO eventually add physics
public class Rigidbody extends Part {
	public int velocity;
	public float mass;
	public float bounciness;
	public Bound collider;

	@Override
	public void Start() {
		collider = thing.getPartExtendsOrImplements(Bound.class);
	}

	@Override
	public void Update() {

	}
}
