package dreampool.render.fog;

import org.joml.Vector4f;

import dreampool.Application;

// TODO abstract away from scene and Application
public class Fog {
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

	public void Start() {
		Application.mainShader.setVec4("fogColor", color);
		Application.mainShader.setFloat("fogDepthMax", depthMax);
		Application.mainShader.setFloat("fogDepthMin", depthMin);
	}

	public void Update() {

	}
}
