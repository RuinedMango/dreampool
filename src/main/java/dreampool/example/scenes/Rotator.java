package dreampool.example.scenes;

import dreampool.core.Part;

public class Rotator extends Part{
	public Rotator() {
		
	}
	
	@Override
	public void Update() {
		transform.rotation.x += 1;
		transform.rotation.y += 1;
		transform.rotation.z += 1;
	}
}
