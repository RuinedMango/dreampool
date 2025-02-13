package dreampool.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import dreampool.Application;
import dreampool.core.Part;

public class Camera extends Part{
	public Matrix4f matrix = new Matrix4f();
	public Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	public Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	
	@Override
	public void Update() {
		Vector3f frontPos = new Vector3f();
		Vector3f cameraPos = transform.position;
		cameraPos.add(front, frontPos);
		matrix = new Matrix4f().lookAt(cameraPos, frontPos, up);
		Application.mainShader.setMat4("view", matrix);
	}
}
