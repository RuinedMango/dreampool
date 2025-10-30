package dreampool.render.camera;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import dreampool.Application;
import dreampool.core.Part;

// TODO add other views and advanced camera stuff like that
public class Camera extends Part {
	public static Camera Singleton;
	public FrustumIntersection frustum = new FrustumIntersection();
	public Matrix4f matrix = new Matrix4f();
	public Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	public Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	public Vector3f right = new Vector3f();

	public Camera() {
		if (Singleton == null) {
			Singleton = this;
		} else {
			System.out.println("Camera already exists");
		}
	}

	@Override
	public void Start() {
		this.front = transform.rotation.normalize();
	}

	@Override
	public void Update() {
		Vector3f frontPos = new Vector3f();
		Vector3f cameraPos = transform.position;
		cameraPos.add(front, frontPos);
		matrix = new Matrix4f().lookAt(cameraPos, frontPos, up);

		Matrix4f projViewMatrix = new Matrix4f();
		projViewMatrix.set(Application.projection);
		projViewMatrix.mul(matrix);

		frustum.set(projViewMatrix);
	}
}
