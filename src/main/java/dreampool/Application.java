package dreampool;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import dreampool.IO.DeviceManager;
import dreampool.audio.AudioDevice;
import dreampool.core.Time;
import dreampool.core.scene.SceneManager;
import dreampool.example.scenes.ExampleScene;
import dreampool.render.PostShader;
import dreampool.render.Shader;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

public class Application {	
	public static int width;
	public static int height;
	public static float resDivisor = 2;
	private static long window;
	static boolean wireframe = false;
	public static int VBO;
	public static int VAO;
	static int EBO;
	static int FBO;
	static int RBO;
	
	static int FBOtex;
	
	static int fontVAO;
	static int fontVBO;
	
    static float quadVertices[] = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
            // positions   // texCoords
            -1.0f,  1.0f,  0.0f, 1.0f,
            -1.0f, -1.0f,  0.0f, 0.0f,
             1.0f, -1.0f,  1.0f, 0.0f,

            -1.0f,  1.0f,  0.0f, 1.0f,
             1.0f, -1.0f,  1.0f, 0.0f,
             1.0f,  1.0f,  1.0f, 1.0f
        };
	
	public static Shader mainShader;
	
	public static void main(String[] args) {
		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		window = GLFW.glfwCreateWindow(800, 600, "Winer", NULL, NULL);
		if(window == NULL) {
			System.out.println("Failed to create GLFW window");
			GLFW.glfwTerminate();
			return;
		}
		GLFW.glfwMakeContextCurrent(window);
		
		GL.createCapabilities();
		
		GL46.glViewport(0, 0, 800, 600);
		GL46.glEnable(GL46.GL_CULL_FACE);
		GL46.glCullFace(GL46.GL_BACK);
		GLFW.glfwSetFramebufferSizeCallback(window, myBufferCallback());
		
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		
		AudioDevice sound = new AudioDevice();
		
		VAO = GL46.glGenVertexArrays();
		GL46.glBindVertexArray(VAO);
		
		VBO = GL46.glGenBuffers();
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO);
		
		EBO = GL46.glGenBuffers();
		GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, EBO);
		
		FBO = GL46.glGenFramebuffers();
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, FBO);
		
        IntBuffer w = MemoryStack.stackPush().mallocInt(1);
        IntBuffer h = MemoryStack.stackPush().mallocInt(1);
        GLFW.glfwGetFramebufferSize(window, w, h);
        int initialWidth = w.get(0);
        int initialHeight = h.get(0);
		
		FBOtex = GL46.glGenTextures();
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, FBOtex);
		GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGB16F, (int) (initialWidth / resDivisor), (int) (initialHeight / resDivisor), 0, GL46.GL_RGB, GL46.GL_FLOAT, NULL);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER,  GL46.GL_NEAREST);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER,  GL46.GL_NEAREST);
		GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, GL46.GL_TEXTURE_2D, FBOtex, 0);
		
		RBO = GL46.glGenRenderbuffers();
		GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, RBO);
		GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH24_STENCIL8, (int) (initialWidth / resDivisor), (int) (initialHeight / resDivisor));
		GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_STENCIL_ATTACHMENT, GL46.GL_RENDERBUFFER, RBO);
		if(GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Framebuffer is not complete");
		}
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
		
		
		PostShader post = new PostShader("/shaders/dither.frag");
		post.use();
		post.setInt("levels",32);
		post.setInt("screenTexture", 0);
		
		mainShader = new Shader("/shaders/main.vert", "/shaders/main.frag", "/shaders/main.tcs", "/shaders/main.tes");
		mainShader.use();
		mainShader.setInt("texture1", 0);
		mainShader.setInt("texture2", 1);
		
		GL46.glEnableVertexAttribArray(0);
		GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 8 * Float.BYTES, 0);
		GL46.glEnableVertexAttribArray(1);
		GL46.glVertexAttribPointer(1, 2, GL46.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
		GL46.glEnableVertexAttribArray(2);
		GL46.glVertexAttribPointer(2, 3, GL46.GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);
        
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
		
		int quadVAO = GL46.glGenVertexArrays();
		int quadVBO = GL46.glGenBuffers();
		GL46.glBindVertexArray(quadVAO);
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, quadVBO);
		GL46.glVertexAttribPointer(0, 2, GL46.GL_FLOAT, false, 4 * Float.BYTES, 0);
		GL46.glEnableVertexAttribArray(0);
		GL46.glVertexAttribPointer(1, 2, GL46.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
		GL46.glEnableVertexAttribArray(1);
		
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
        
		fontVAO = GL46.glGenVertexArrays();
		GL46.glBindVertexArray(fontVAO);
		
		fontVBO = GL46.glGenBuffers();
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, fontVBO);
		GL46.glBufferData(GL46.GL_ARRAY_BUFFER, 1024 * 1024, GL46.GL_DYNAMIC_DRAW);
		
		GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, Float.BYTES * 9, 0);
		GL46.glEnableVertexAttribArray(0);
		GL46.glVertexAttribPointer(1, 4, GL46.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 3);
		GL46.glEnableVertexAttribArray(1);
		GL46.glVertexAttribPointer(2, 2, GL46.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 7);
		GL46.glEnableVertexAttribArray(2);
		
		SceneManager manager = new SceneManager(new ExampleScene().scene);
		
		@SuppressWarnings("unused")
		DeviceManager device = new DeviceManager(window);
		
		Time time = new Time();
		
		manager.currentScene.Start();
		
		GL46.glPatchParameteri(GL46.GL_PATCH_VERTICES, 3);	
		GL46.glDisable(GL46.GL_DITHER);
		GL46.glDisable(GL46.GL_POINT_SMOOTH);
		GL46.glDisable(GL46.GL_LINE_SMOOTH);
		GL46.glDisable(GL46.GL_POLYGON_SMOOTH);
		GL46.glHint(GL46.GL_POINT_SMOOTH, GL46.GL_DONT_CARE);
		GL46.glHint(GL46.GL_LINE_SMOOTH, GL46.GL_DONT_CARE);
		GL46.glHint(GL46.GL_POLYGON_SMOOTH_HINT, GL46.GL_DONT_CARE);
		
		Vector2f lightDir = new Vector2f(90, 0);
		
		if(wireframe) {
			GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
		}
		
		while(!GLFW.glfwWindowShouldClose(window)) {
			time.update();
			
			GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, FBO);
			GL46.glEnable(GL46.GL_DEPTH_TEST);
			
			GL46.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
			
			GLFW.glfwGetFramebufferSize(window, w, h);
			width = w.get(0);
			height = h.get(0);
			Matrix4f projection = new Matrix4f().perspective(45.0f, ((float)w.get(0) / resDivisor) / ((float)h.get(0) / resDivisor), 0.1f, 100.0f);
			mainShader.use();
			mainShader.setMat4("projection", projection);
			mainShader.setVec2("targetResolution", (int) (w.get(0) / resDivisor) / 2, (int) (h.get(0) / resDivisor) / 2);
			
			lightDir.add(new Vector2f(1, 0));
			
			mainShader.setVec2("lightDir", lightDir);
			mainShader.setVec3("ambientColor", new Vector3f(0.2f, 0.2f, 0.2f));
			mainShader.setVec3("diffuseColor", new Vector3f(1.0f, 1.0f, 1.0f));
			
			GL46.glViewport(0, 0, (int) (w.get(0) / resDivisor), (int) (h.get(0) / resDivisor));
			manager.currentScene.Update();
			
			GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
			GL46.glDisable(GL46.GL_DEPTH_TEST);
			
			GL46.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
			
			post.use();
			GL46.glViewport(0, 0, w.get(0), h.get(0));
			GL46.glBindVertexArray(quadVAO);
			GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, quadVBO);
			GL46.glBufferData(GL46.GL_ARRAY_BUFFER, quadVertices, GL46.GL_STATIC_DRAW);
			GL46.glBindTexture(GL46.GL_TEXTURE_2D, FBOtex);
			GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, 6);
			
			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();
			w.clear();
			h.clear();
		}
		
		sound.destroy();
		 
		GL46.glDeleteVertexArrays(VAO);
		GL46.glDeleteBuffers(VBO);
		GL46.glDeleteBuffers(EBO);
		GL46.glDeleteFramebuffers(FBO);
		GL46.glDeleteRenderbuffers(RBO);
		
		GLFW.glfwTerminate();
		return;
	}
	
	static GLFWFramebufferSizeCallback myBufferCallback() {
		return new GLFWFramebufferSizeCallback()
		{
		    @Override
		    public void invoke(long window, int width, int height)
		    {
	            GL46.glViewport(0, 0, (int) (width / resDivisor), (int) (height / resDivisor));

	            // Resize FBO Texture
	            GL46.glBindTexture(GL46.GL_TEXTURE_2D, FBOtex);
	            GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGB16F, (int) (width / resDivisor), (int) (height / resDivisor), 0, GL46.GL_RGB, GL46.GL_FLOAT, NULL);
	            
	            // Resize Renderbuffer Storage
	            GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, RBO);
	            GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH24_STENCIL8, (int) (width / resDivisor), (int) (height / resDivisor));

	            // Verify FBO is complete
	            GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, FBO);
	            if (GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
	                System.err.println("Framebuffer is not complete after resize!");
	            }
	            GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
		    }
		};
		
	}
}
