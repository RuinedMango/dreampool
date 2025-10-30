package dreampool.render;

import java.util.List;

import org.joml.Matrix4f;

import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;

public class RenderCommand {
	public Mesh mesh;
	public Matrix4f modelMat;
	public List<Texture> textures;
	public RenderStage target;

	public int sortKey;

	public RenderCommand(RenderStage target, Mesh mesh, List<Texture> textures, Matrix4f modelMat) {
		this.target = target;
		this.mesh = mesh;
		this.modelMat = modelMat;
		this.textures = textures;
		sortKey = mesh.entry.vertices.length;
	}
}
