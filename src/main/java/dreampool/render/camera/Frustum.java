package dreampool.render.camera;

import org.joml.Vector3f;

public class Frustum {
	public class Plane{
		Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);
		float distance = 0.0f;
		
		public Plane(Vector3f p1, Vector3f norm) {
			normal = norm.normalize();
			distance = normal.dot(p1);
		}
		
		float getSignedDistanceToPlane(Vector3f point) {
			return normal.dot(point) - distance;
		}
	}
	
	Plane topFace;
	Plane bottomFace;
	Plane rightFace;
	Plane leftFace;
	Plane farFace;
	Plane nearFace;
	
	public Frustum(float aspect, float fovY, float zNear, float zFar) {
		float halfVSide = (float) (zFar * Math.tan(Math.toRadians(fovY * 0.5)));
		float halfHSide = halfVSide * aspect;
		Vector3f frontMultFar = null;
		Camera.Singleton.front.mul(zFar, frontMultFar);
		
		nearFace = new Plane(Camera.Singleton.transform.position.add(Camera.Singleton.front.mul(zNear)), Camera.Singleton.front);
		farFace = new Plane(Camera.Singleton.transform.position.add(frontMultFar), Camera.Singleton.front.negate());
		
		rightFace = new Plane(Camera.Singleton.transform.position, frontMultFar.min(Camera.Singleton.right.mul(halfHSide), Camera.Singleton.up));
		leftFace = new Plane(Camera.Singleton.transform.position, Camera.Singleton.up.cross(frontMultFar.add(Camera.Singleton.right.mul(halfHSide))));
		topFace = new Plane(Camera.Singleton.transform.position, Camera.Singleton.right.cross(frontMultFar.min(Camera.Singleton.up.mul(halfVSide))));
		bottomFace = new Plane(Camera.Singleton.transform.position, frontMultFar.add(Camera.Singleton.up.mul(halfVSide)).cross(Camera.Singleton.right));
	}
}
