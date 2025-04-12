package dreampool.physics;

import dreampool.core.Part;
import dreampool.physics.bounds.Bound;

public class Rigidbody extends Part {
    public int velocity;
    public float mass;
    public float bounciness;
    public Bound collider;

    @Override
    public void Start() {
	collider = thing.getPartExtends("Bound");
    }

    @Override
    public void Update() {

    }
}
