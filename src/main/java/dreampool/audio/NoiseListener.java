package dreampool.audio;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import dreampool.core.Part;
import dreampool.render.camera.Camera;

public class NoiseListener extends Part {
	// TODO abstract away from camera
	private Camera cam;

	// Funny class
	public NoiseListener() {
	}

	@Override
	public void Start() {
		cam = (Camera) thing.getPart("Camera");
	}

	@Override
	public void Update() {
		setPosition(transform.position);

		Matrix4f camMatrix = cam.matrix;
		Vector3f at = new Vector3f();
		camMatrix.positiveZ(at).negate();
		Vector3f up = new Vector3f();
		camMatrix.positiveY(up);
		setOrientation(at, up);
	}

	public void setPosition(Vector3f position) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
	}

	public void setOrientation(Vector3f at, Vector3f up) {
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		AL10.alListenerfv(AL10.AL_ORIENTATION, data);
	}
}
