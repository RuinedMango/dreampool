package dreampool.example.scenes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import dreampool.IO.DeviceManager;
import dreampool.core.Part;
import dreampool.core.Time;
import dreampool.render.camera.Camera;

public class PlayerController extends Part{
	static boolean firstMouse = true;
	static float yaw = -90.0f;
	static float pitch = 0.0f;
	static float lastX = 800.0f / 2.0f;
	static float lastY = 600.0f / 2.0f;
	public Camera cam;

	public PlayerController(){

	}

	@Override
	public void Start(){
		DeviceManager.Singleton.setCursorPosCallback(myMouseCallback());
		cam = (Camera)thing.getPart("Camera");
	}

	@Override
	public void Update(){
		processMovement(DeviceManager.Singleton.window);
	}

	void processMovement(long window){
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS){
			GLFW.glfwSetWindowShouldClose(window, true);
		}
		Vector3f temp = new Vector3f();
		float cameraSpeed = 2.5f * Time.deltaTime;
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS){
			cam.front.mul(cameraSpeed, temp);
			transform.position.add(temp);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS){
			cam.front.mul(cameraSpeed, temp);
			transform.position.sub(temp);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS){
			cam.front.cross(cam.up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			transform.position.sub(temp);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS){
			cam.front.cross(cam.up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			transform.position.add(temp);
		}
	}

	public GLFWCursorPosCallback myMouseCallback(){
		return new GLFWCursorPosCallback(){
			@Override
			public void invoke(long window, double xpos, double ypos){
				if (firstMouse){
					lastX = (float)xpos;
					lastY = (float)ypos;
					firstMouse = false;
				}

				float xoffset = (float)(xpos - lastX);
				float yoffset = (float)(lastY - ypos); // Reversed y-coordinates
				lastX = (float)xpos;
				lastY = (float)ypos;

				float sensitivity = 0.1f;
				xoffset *= sensitivity;
				yoffset *= sensitivity;

				// Get current front and up vectors
				Vector3f front = new Vector3f(cam.front).normalize();
				Vector3f up = new Vector3f(0, 1, 0);

				// Apply yaw rotation around the Y-axis
				Matrix4f yawRotation = new Matrix4f().rotation((float)Math.toRadians(-xoffset), up);
				yawRotation.transformDirection(front);

				// Compute the right axis using the updated front
				Vector3f right = new Vector3f();
				front.cross(up, right).normalize();

				// Apply pitch rotation around the right axis
				Matrix4f pitchRotation = new Matrix4f().rotation((float)Math.toRadians(yoffset), right);
				pitchRotation.transformDirection(front);

				// Clamp pitch to prevent flipping
				float currentPitch = (float)Math.toDegrees(Math.asin(front.y));
				float maxPitch = 89.0f;
				if (currentPitch > maxPitch){
					front.y = (float)Math.sin(Math.toRadians(maxPitch));
					adjustHorizontalComponents(front, maxPitch);
				}else if (currentPitch < -maxPitch){
					front.y = (float)Math.sin(Math.toRadians(-maxPitch));
					adjustHorizontalComponents(front, -maxPitch);
				}

				front.normalize();
				cam.front.set(front);
			}

			private void adjustHorizontalComponents(Vector3f front, float pitch){
				float horizontalLength = (float)Math.cos(Math.toRadians(pitch));
				float currentHorizontal = (float)Math.sqrt(front.x * front.x + front.z * front.z);
				if (currentHorizontal > 0){
					front.x = (front.x / currentHorizontal) * horizontalLength;
					front.z = (front.z / currentHorizontal) * horizontalLength;
				}else{
					front.x = horizontalLength;
					front.z = 0;
				}
			}
		};
	}
}
