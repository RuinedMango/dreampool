package dreampool.render.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL46;

import dreampool.Application;
import dreampool.IO.FileUtils;
import dreampool.core.Part;

public class Mesh extends Part{
	private boolean flat = true;
	private List<Float> vertices = new ArrayList<Float>();
	private List<Integer> indices = new ArrayList<Integer>();
	static float testvertices[] = {
		    -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
		     0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
		    -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

		    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		    -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

		    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		    -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
		    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
		};
	static int testindices[] = {
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
			21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35
	};
	
	public Mesh(String path, boolean flat) {
		try {
			FileUtils.readObjMeshResource(path, vertices, indices);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.flat = flat;
	}
	
	@Override
	public void Update() {
		float[] vertexArray = new float[vertices.size()];
		for(int i = 0; i < vertices.size(); i++) vertexArray[i] = vertices.get(i);
		float[] indiceArray = new float[indices.size()];
		for(int i = 0; i < indices.size(); i++) indiceArray[i] = indices.get(i);
		GL46.glBufferData(GL46.GL_ARRAY_BUFFER, vertexArray, GL46.GL_STATIC_DRAW);
		GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indiceArray, GL46.GL_STATIC_DRAW);
		Matrix4f model = new Matrix4f();
    	model.translate(transform.position);
    	model.rotate(new Quaternionf().rotationXYZ((float) Math.toRadians(transform.rotation.x), (float) Math.toRadians(transform.rotation.y), (float) Math.toRadians(transform.rotation.z)));
    	model.scale(transform.size);
    	Application.mainShader.setMat4("model", model);
    	Application.mainShader.setBool("flatlight", flat);
    	GL46.glDrawArrays(GL46.GL_PATCHES, 0, vertices.size());
    	//GL46.glDrawElements(GL46.GL_TRIANGLES, indices.size(), GL46.GL_UNSIGNED_INT, 0);
	}
}
