package dreampool.example.scenes;

import dreampool.core.Part;
import dreampool.core.Time;

public class Rotator extends Part{
	public Rotator(){

	}

	@Override
	public void Update(){
		transform.rotation.x += 10 * Time.deltaTime;
		transform.rotation.y += 10 * Time.deltaTime;
		transform.rotation.z += 10 * Time.deltaTime;
	}
}
