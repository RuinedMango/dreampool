package dreampool;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.MemoryStack;

import dreampool.IO.DeviceManager;
import dreampool.audio.AudioDevice;
import dreampool.core.Time;
import dreampool.core.scene.SceneManager;
import dreampool.example.scenes.ExampleScene;
import dreampool.render.RenderPipeline;
import dreampool.render.pass.GeometryPass;
import dreampool.render.shader.PostShader;

public class Application {
	// TODO fix this whole god awful class
	public static Matrix4f projection;
	public static int width;
	public static int height;
	public static float resDivisor = 2;
	private static long window;
	static boolean wireframe = false;
	static int FBO;
	static int RBO;

	static int FBOtex;

	static float quadVertices[] = { -1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f,
			1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f };

	public static void main(String[] args) {
		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

		window = GLFW.glfwCreateWindow(800, 600, "Winer", NULL, NULL);
		if (window == NULL) {
			System.out.println("Failed to create GLFW window");
			GLFW.glfwTerminate();
			return;
		}
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(0);

		GL.createCapabilities();

		GL11.glViewport(0, 0, 800, 600);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GLFW.glfwSetFramebufferSizeCallback(window, myBufferCallback());

		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

		AudioDevice sound = new AudioDevice();
		// AssetLoader assetLoader = new AssetLoader();
		RenderPipeline renderPipeline = new RenderPipeline();
		renderPipeline.addPass(new GeometryPass());

		FBO = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBO);

		IntBuffer w = MemoryStack.stackPush().mallocInt(1);
		IntBuffer h = MemoryStack.stackPush().mallocInt(1);
		GLFW.glfwGetWindowSize(window, w, h);
		int initialWidth = w.get(0);
		int initialHeight = h.get(0);

		FBOtex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, FBOtex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, (int) (initialWidth / resDivisor),
				(int) (initialHeight / resDivisor), 0, GL11.GL_RGB, GL11.GL_FLOAT, NULL);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, FBOtex, 0);

		RBO = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, RBO);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, (int) (initialWidth / resDivisor),
				(int) (initialHeight / resDivisor));
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
				RBO);
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Framebuffer is not complete");
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		PostShader post = new PostShader("/shaders/dither.frag");
		post.use();
		post.setInt("levels", 16);
		post.setInt("screenTexture", 0);

		int quadVAO = GL30.glGenVertexArrays();
		int quadVBO = GL15.glGenBuffers();
		GL30.glBindVertexArray(quadVAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
		GL20.glEnableVertexAttribArray(1);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Float.BYTES * 9, 0);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 3);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 7);
		GL20.glEnableVertexAttribArray(2);

		SceneManager manager = new SceneManager(new ExampleScene().generateScene());

		@SuppressWarnings("unused")
		DeviceManager device = new DeviceManager(window);

		Time time = new Time();

		manager.currentScene.Start();

		GL40.glPatchParameteri(GL40.GL_PATCH_VERTICES, 3);
		GL11.glDisable(GL11.GL_DITHER);
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GL11.glHint(GL11.GL_POINT_SMOOTH, GL11.GL_DONT_CARE);
		GL11.glHint(GL11.GL_LINE_SMOOTH, GL11.GL_DONT_CARE);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);

		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		while (!GLFW.glfwWindowShouldClose(window)) {
			time.update();
			renderPipeline.beginFrame();
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBO);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			GLFW.glfwGetWindowSize(window, w, h);
			width = w.get(0);
			height = h.get(0);
			projection = new Matrix4f().perspective(70.0f, (w.get(0) / resDivisor) / (h.get(0) / resDivisor), 0.1f,
					50.0f);

			GL11.glViewport(0, 0, (int) (width / resDivisor), (int) (height / resDivisor));
			manager.currentScene.Update();
			renderPipeline.execute();

			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			GL15.glActiveTexture(GL13.GL_TEXTURE0);
			GL15.glBindTexture(GL13.GL_TEXTURE_2D, 0);

			post.use();
			GL11.glViewport(0, 0, w.get(0), h.get(0));
			GL30.glBindVertexArray(quadVAO);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, quadVertices, GL15.GL_STATIC_DRAW);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, FBOtex);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();

			GL15.glActiveTexture(GL13.GL_TEXTURE0);
			GL15.glBindTexture(GL13.GL_TEXTURE_2D, 0);

			GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			w.clear();
			h.clear();
			renderPipeline.endFrame();
		}

		sound.destroy();
		// assetLoader.shutdown();

		GL30.glDeleteFramebuffers(FBO);
		GL30.glDeleteRenderbuffers(RBO);

		GLFW.glfwTerminate();
		return;
	}

	static GLFWFramebufferSizeCallback myBufferCallback() {
		return new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				GL11.glViewport(0, 0, width, height);

				// Resize FBO Texture
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FBOtex);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, (int) (width / resDivisor),
						(int) (height / resDivisor), 0, GL11.GL_RGB, GL11.GL_FLOAT, NULL);

				// Resize Renderbuffer Storage
				GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, RBO);
				GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, (int) (width / resDivisor),
						(int) (height / resDivisor));

				// Verify FBO is complete
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBO);
				if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
					System.err.println("Framebuffer is not complete after resize!");
				}
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			}
		};

	}
}
