package dreampool;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import dreampool.IO.DeviceManager;
import dreampool.audio.AudioDevice;
import dreampool.core.Time;
import dreampool.core.scene.SceneManager;
import dreampool.example.scenes.WienerScene;
import dreampool.render.Shader;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

public class Application {
	private static long window;
	static int indices[] = {  // note that we start from 0!
		    0, 1, 3,   // first triangle
		    1, 2, 3    // second triangle
		};  
	static int VBO;
	static int VAO;
	static int EBO;
	static Vector3f cameraPos = new Vector3f(0.0f, 0.0f, 3.0f);
	static Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
	static Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
	
	static float fov = 45.0f;
	
	public static Shader ourShader;
	
	public static void main(String[] args) {
		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		window = GLFW.glfwCreateWindow(800, 600, "penis", NULL, NULL);
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
		
		ourShader = new Shader("/shaders/main.vert", "/shaders/main.frag", "/shaders/main.tcs", "/shaders/main.tes");
		
		GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 5 * Float.BYTES, 0);
		GL46.glEnableVertexAttribArray(0);
		GL46.glVertexAttribPointer(1, 2, GL46.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
		GL46.glEnableVertexAttribArray(1);
		
		SceneManager manager = new SceneManager(new WienerScene().scene);

		ourShader.use();
		
		ourShader.setInt("texture1", 0);
		ourShader.setInt("texture2", 1);
		
		@SuppressWarnings("unused")
		DeviceManager device = new DeviceManager(window);
		
		Time time = new Time();
		
		manager.currentScene.Start();
		
		GL46.glPatchParameteri(GL46.GL_PATCH_VERTICES, 3);	
		GL46.glEnable(GL46.GL_DEPTH_TEST);
		GL46.glDisable(GL46.GL_DITHER);
		GL46.glDisable(GL46.GL_POINT_SMOOTH);
		GL46.glDisable(GL46.GL_LINE_SMOOTH);
		GL46.glDisable(GL46.GL_POLYGON_SMOOTH);
		GL46.glHint(GL46.GL_POINT_SMOOTH, GL46.GL_DONT_CARE);
		GL46.glHint(GL46.GL_LINE_SMOOTH, GL46.GL_DONT_CARE);
		GL46.glHint(GL46.GL_POLYGON_SMOOTH_HINT, GL46.GL_DONT_CARE);
		
		while(!GLFW.glfwWindowShouldClose(window)) {
			time.update();
			
			GL46.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
			
	        IntBuffer w = MemoryStack.stackPush().mallocInt(1);
	        IntBuffer h = MemoryStack.stackPush().mallocInt(1);
	        GLFW.glfwGetFramebufferSize(window, w, h);
			Matrix4f projection = new Matrix4f().perspective(45.0f, (float)w.get(0) / (float)h.get(0), 0.1f, 100.0f);
			ourShader.setMat4("projection", projection);
			ourShader.setVec2("targetResolution", 320, 240);
			
			ourShader.use();
			
			GL46.glBindVertexArray(VAO);
			manager.currentScene.Update();
			
			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();
		}
		
		sound.destroy();
		
		GL46.glDeleteVertexArrays(VAO);
		GL46.glDeleteBuffers(VBO);
		GL46.glDeleteBuffers(EBO);
		
		GLFW.glfwTerminate();
		return;
	}
	
	static GLFWFramebufferSizeCallback myBufferCallback() {
		return new GLFWFramebufferSizeCallback()
		{
		    @Override
		    public void invoke(long window, int width, int height)
		    {
		        GL46.glViewport(0, 0, width, height);
		    }
		};
		
	}
}
