package dreampool.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Frustum {
	public final Vector4f[] planes = new Vector4f[6];

	public Frustum() {
		for (int i = 0; i < 6; i++) {
			planes[i] = new Vector4f();
		}
	}

	// TODO replace with JOML optimized magic
	public void update(Matrix4f viewProjMatrix) {
		// Extract and normalize each frustum plane from the view-projection matrix

		planes[0].set(viewProjMatrix.m03() + viewProjMatrix.m00(), viewProjMatrix.m13() + viewProjMatrix.m10(),
				viewProjMatrix.m23() + viewProjMatrix.m20(), viewProjMatrix.m33() + viewProjMatrix.m30());
		normalizePlane(planes[0]);

		planes[1].set(viewProjMatrix.m03() - viewProjMatrix.m00(), viewProjMatrix.m13() - viewProjMatrix.m10(),
				viewProjMatrix.m23() - viewProjMatrix.m20(), viewProjMatrix.m33() - viewProjMatrix.m30());
		normalizePlane(planes[1]);

		planes[2].set(viewProjMatrix.m03() + viewProjMatrix.m01(), viewProjMatrix.m13() + viewProjMatrix.m11(),
				viewProjMatrix.m23() + viewProjMatrix.m21(), viewProjMatrix.m33() + viewProjMatrix.m31());
		normalizePlane(planes[2]);

		planes[3].set(viewProjMatrix.m03() - viewProjMatrix.m01(), viewProjMatrix.m13() - viewProjMatrix.m11(),
				viewProjMatrix.m23() - viewProjMatrix.m21(), viewProjMatrix.m33() - viewProjMatrix.m31());
		normalizePlane(planes[3]);

		planes[4].set(viewProjMatrix.m03() + viewProjMatrix.m02(), viewProjMatrix.m13() + viewProjMatrix.m12(),
				viewProjMatrix.m23() + viewProjMatrix.m22(), viewProjMatrix.m33() + viewProjMatrix.m32());
		normalizePlane(planes[4]);

		planes[5].set(viewProjMatrix.m03() - viewProjMatrix.m02(), viewProjMatrix.m13() - viewProjMatrix.m12(),
				viewProjMatrix.m23() - viewProjMatrix.m22(), viewProjMatrix.m33() - viewProjMatrix.m32());
		normalizePlane(planes[5]);
	}

	private void normalizePlane(Vector4f plane) {
		float a = plane.x;
		float b = plane.y;
		float c = plane.z;
		float length = (float) Math.sqrt(a * a + b * b + c * c);
		if (length != 0.0f) {
			float invLength = 1.0f / length;
			plane.x *= invLength;
			plane.y *= invLength;
			plane.z *= invLength;
			plane.w *= invLength;
		}
	}
}