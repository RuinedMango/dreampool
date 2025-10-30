package dreampool.render;

import org.joml.Matrix4f;

import dreampool.render.model.Mesh;

public class RenderCommand {
	public Mesh mesh;
	public Matrix4f modelMat;

	public RenderCommand(Mesh mesh, Matrix4f modelMat) {
		this.mesh = mesh;
		this.modelMat = modelMat;
	}
}
