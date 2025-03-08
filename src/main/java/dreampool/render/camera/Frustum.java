package dreampool.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Frustum {
    public final Vector4f[] planes;

    public Frustum() {
        planes = new Vector4f[6];
        for (int i = 0; i < 6; i++) {
            planes[i] = new Vector4f();
        }
    }

    public void update(Matrix4f viewProjMatrix) {
        // Left plane: row3 + row0
        planes[0].set(
                viewProjMatrix.m30() + viewProjMatrix.m00(),
                viewProjMatrix.m31() + viewProjMatrix.m01(),
                viewProjMatrix.m32() + viewProjMatrix.m02(),
                viewProjMatrix.m33() + viewProjMatrix.m03()
        );
        normalizePlane(planes[0]);

        // Right plane: row3 - row0
        planes[1].set(
                viewProjMatrix.m30() - viewProjMatrix.m00(),
                viewProjMatrix.m31() - viewProjMatrix.m01(),
                viewProjMatrix.m32() - viewProjMatrix.m02(),
                viewProjMatrix.m33() - viewProjMatrix.m03()
        );
        normalizePlane(planes[1]);

        // Bottom plane: row3 + row1
        planes[2].set(
                viewProjMatrix.m30() + viewProjMatrix.m10(),
                viewProjMatrix.m31() + viewProjMatrix.m11(),
                viewProjMatrix.m32() + viewProjMatrix.m12(),
                viewProjMatrix.m33() + viewProjMatrix.m13()
        );
        normalizePlane(planes[2]);

        // Top plane: row3 - row1
        planes[3].set(
                viewProjMatrix.m30() - viewProjMatrix.m10(),
                viewProjMatrix.m31() - viewProjMatrix.m11(),
                viewProjMatrix.m32() - viewProjMatrix.m12(),
                viewProjMatrix.m33() - viewProjMatrix.m13()
        );
        normalizePlane(planes[3]);

        // Near plane: row3 + row2
        planes[4].set(
                viewProjMatrix.m30() + viewProjMatrix.m20(),
                viewProjMatrix.m31() + viewProjMatrix.m21(),
                viewProjMatrix.m32() + viewProjMatrix.m22(),
                viewProjMatrix.m33() + viewProjMatrix.m23()
        );
        normalizePlane(planes[4]);

        // Far plane: row3 - row2
        planes[5].set(
                viewProjMatrix.m30() - viewProjMatrix.m20(),
                viewProjMatrix.m31() - viewProjMatrix.m21(),
                viewProjMatrix.m32() - viewProjMatrix.m22(),
                viewProjMatrix.m33() - viewProjMatrix.m23()
        );
        normalizePlane(planes[5]);
    }

    private void normalizePlane(Vector4f plane) {
        float length = (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);
        if (length == 0.0f) return;
        plane.div(length);
    }
}
