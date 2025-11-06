package dreampool.render.fog;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import dreampool.core.Part;
import dreampool.render.RenderCommand;
import dreampool.render.RenderPipeline;
import dreampool.render.RenderStage;

// TODO abstract away from scene and Application
public class Fog extends Part {
	public static Fog Singleton;
	public Vector4f color = new Vector4f(0, 0, 0, 0);
	public float depthMax = 10;
	public float depthMin = 1;

	public Fog() {
		if (Singleton == null) {
			Singleton = this;
		} else {
			System.out.println("Fog already exists");
		}
	}

	public Fog(Vector4f color, float depthMax, float depthMin) {
		if (Singleton == null) {
			Singleton = this;
			this.color = color;
			this.depthMax = depthMax;
			this.depthMin = depthMin;
		} else {
			System.out.println("Fog already exists");
		}
	}

	@Override
	public void Start() {
		Matrix4f packedMat = new Matrix4f();
		Vector4f packDepth = new Vector4f(depthMax, depthMin, 0, 0);
		packedMat.setColumn(0, color);
		packedMat.setColumn(1, packDepth);

		RenderPipeline.Singleton.submit(new RenderCommand(RenderStage.GEOMETRY, null, null, packedMat, "fog"));
	}

	@Override
	public void Update() {
	}
}
