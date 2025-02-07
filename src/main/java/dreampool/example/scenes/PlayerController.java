package dreampool.example.scenes;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import dreampool.IO.DeviceManager;
import dreampool.core.Part;
import dreampool.core.Time;
import dreampool.render.Camera;

public class PlayerController extends Part{
	static boolean firstMouse = true;
	static float yaw = -90.0f;
	static float pitch = 0.0f;
	static float lastX = 800.0f / 2.0f;
	static float lastY = 600.0f / 2.0f;
	public Camera cam;
	
	public PlayerController() {
		
	}
	
	@Override
	public void Start() {
		DeviceManager.Singleton.setCursorPosCallback(myMouseCallback());
		cam = (Camera) thing.getPart("Camera");
		if(cam == null) {
			System.out.println("Dick n balls");
		}
	}
	
	@Override
	public void Update(){
		processMovement(DeviceManager.Singleton.window);
	}
	
	void processMovement(long window) {
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
			GLFW.glfwSetWindowShouldClose(window, true);
		}
		Vector3f temp = new Vector3f();
		float cameraSpeed = 2.5f * Time.deltaTime;
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
			cam.front.mul(cameraSpeed, temp);
			transform.position.add(temp);
		}
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
			cam.front.mul(cameraSpeed, temp);
			transform.position.sub(temp);
		}
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
			cam.front.cross(cam.up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			transform.position.sub(temp);
		}
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
			cam.front.cross(cam.up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			transform.position.add(temp);
		}
	}
	
	public GLFWCursorPosCallback myMouseCallback() {
		return new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if(firstMouse) {
					lastX = (float) xpos;
					lastY = (float) ypos;
					firstMouse = false;
				}
				
				float xoffset = (float) (xpos - lastX);
				float yoffset = (float) (lastY - ypos);
				lastX = (float) xpos;
				lastY = (float) ypos;
				
				float sensitivity = 0.1f;
				xoffset *= sensitivity;
				yoffset *= sensitivity;
				
				yaw += xoffset;
				pitch += yoffset;
				
				if(pitch > 89.0f) {
					pitch = 89.0f;
				} else if(pitch < -89.0f) {
					pitch = -89.0f;
				}
				
				Vector3f direction = new Vector3f();
				direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
				direction.y = (float) Math.sin(Math.toRadians(pitch));
				direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
				cam.front = direction.normalize();
			}
		};
	}
}
