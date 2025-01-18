package dreampool.core.transform;

import org.joml.Vector3f;

public class Transform {
	public Vector3f position;
	public Vector3f rotation;
	public Vector3f size;
	
	public Transform() {
		position = new Vector3f();
		rotation = new Vector3f();
		size = new Vector3f(1, 1, 1);
	}
}
