package dreampool.example.scenes;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import dreampool.WindowSystem;
import dreampool.IO.InputManager;
import dreampool.core.Part;
import dreampool.core.Thing;
import dreampool.core.Time;
import dreampool.physics.bounds.Collider;
import dreampool.physics.bounds.SphereCollider;
import dreampool.render.camera.Camera;
import dreampool.render.model.Mesh;
import dreampool.render.texture.Texture;

public class PlayerController extends Part {
	static boolean firstMouse = true;
	static float yaw = -90.0f;
	static float pitch = 0.0f;
	static float lastX = 800.0f / 2.0f;
	static float lastY = 600.0f / 2.0f;
	public Camera cam;
	public List<Collider> colliders = new ArrayList<>();
	public float baseSpeed = 2.5f;
	public float sprintMultiplier = 4;
	private float speedHolder;
	private boolean mouseCaptured = true;
	private boolean rightPressable = true;
	private boolean leftPressable = true;

	public PlayerController() {

	}

	@Override
	public void Start() {
		InputManager.Singleton.setCursorPosCallback(myMouseCallback());
		cam = thing.getPart(Camera.class);
		GLFW.glfwSetInputMode(WindowSystem.Singleton.ID, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}

	@Override
	public void Update() {
		speedHolder = baseSpeed;
		processMovement(InputManager.Singleton.window);
		if (GLFW.glfwGetKey(WindowSystem.Singleton.ID, GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
			if (!mouseCaptured) {
				GLFW.glfwSetInputMode(WindowSystem.Singleton.ID, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
				mouseCaptured = true;
			} else {
				GLFW.glfwSetInputMode(WindowSystem.Singleton.ID, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				mouseCaptured = false;
			}
		}
		// TODO eventually do something cool
		if (leftPressable && GLFW.glfwGetMouseButton(InputManager.Singleton.window,
				GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
			colliders = thing.scene.getColliders();
			fireRayFromCamera();
			leftPressable = false;
		}
		if (rightPressable && GLFW.glfwGetMouseButton(InputManager.Singleton.window,
				GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
			colliders = thing.scene.getColliders();
			fireDestructionRayFromCamera();
			rightPressable = false;
		}
		if (GLFW.glfwGetMouseButton(InputManager.Singleton.window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_RELEASE) {
			leftPressable = true;
		}
		if (GLFW.glfwGetMouseButton(InputManager.Singleton.window,
				GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_RELEASE) {
			rightPressable = true;
		}
	}

	void processMovement(long window) {
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
			GLFW.glfwSetWindowShouldClose(window, true);
		}
		Vector3f temp = new Vector3f();
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
				|| GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) {
			speedHolder = baseSpeed * sprintMultiplier;
		}
		float cameraSpeed = speedHolder * Time.deltaTime;
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
			cam.front.mul(cameraSpeed, temp);
			transform.position.add(temp);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
			cam.front.mul(cameraSpeed, temp);
			transform.position.sub(temp);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
			cam.front.cross(cam.up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			transform.position.sub(temp);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
			cam.front.cross(cam.up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			transform.position.add(temp);
		}
	}

	// TODO implement BHV over this stupid shit
	private void fireRayFromCamera() {
		Vector3f rayOrigin = new Vector3f(transform.position);
		Vector3f rayDir = new Vector3f(cam.front).normalize();

		float closestT = Float.POSITIVE_INFINITY;
		Collider hitCollider = null;

		for (Collider c : colliders) {
			float t = c.intersectRay(rayOrigin, rayDir);
			if (t > 0 && t < closestT) {
				closestT = t;
				hitCollider = c;
			}
		}

		if (hitCollider != null) {
			Vector3f hitPoint = new Vector3f(rayDir).mul(closestT).add(rayOrigin);
			hitCollider.getThing().getPart(Mesh.class).hit = true;
			Thing ball = new Thing("ball");
			ball.addPart(new Mesh("/models/Sphere.obj", false));
			ball.addPart(new SphereCollider(true));
			ball.addPart(new Texture("/images/white.png"));
			ball.addPart(new Texture("/images/white.png", 1));
			ball.addPart(new Rotator());
			ball.transform.position = hitPoint;
			thing.scene.addThing(ball);
		}
	}

	// TODO implement BHV over this stupid shit
	private void fireDestructionRayFromCamera() {
		Vector3f rayOrigin = new Vector3f(transform.position);
		Vector3f rayDir = new Vector3f(cam.front).normalize();

		float closestT = Float.POSITIVE_INFINITY;
		Collider hitCollider = null;

		for (Collider c : colliders) {
			float t = c.intersectRay(rayOrigin, rayDir);
			if (t > 0 && t < closestT) {
				closestT = t;
				hitCollider = c;
			}
		}

		if (hitCollider != null) {
			hitCollider.getThing().getPart(Mesh.class).hit = true;
			thing.scene.removeThing(hitCollider.getThing());
		}
	}

	public GLFWCursorPosCallback myMouseCallback() {
		return new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (firstMouse) {
					lastX = (float) xpos;
					lastY = (float) ypos;
					firstMouse = false;
				}

				float xoffset = (float) (xpos - lastX);
				float yoffset = (float) (lastY - ypos); // Reversed y-coordinates
				lastX = (float) xpos;
				lastY = (float) ypos;

				float sensitivity = 0.1f;
				xoffset *= sensitivity;
				yoffset *= sensitivity;

				// Get current front and up vectors
				Vector3f front = new Vector3f(cam.front).normalize();
				Vector3f up = new Vector3f(0, 1, 0);

				// Apply yaw rotation around the Y-axis
				Matrix4f yawRotation = new Matrix4f().rotation((float) Math.toRadians(-xoffset), up);
				yawRotation.transformDirection(front);

				// Compute the right axis using the updated front
				Vector3f right = new Vector3f();
				front.cross(up, right).normalize();

				// Apply pitch rotation around the right axis
				Matrix4f pitchRotation = new Matrix4f().rotation((float) Math.toRadians(yoffset), right);
				pitchRotation.transformDirection(front);

				// Clamp pitch to prevent flipping
				float currentPitch = (float) Math.toDegrees(Math.asin(front.y));
				float maxPitch = 89.0f;
				if (currentPitch > maxPitch) {
					front.y = (float) Math.sin(Math.toRadians(maxPitch));
					adjustHorizontalComponents(front, maxPitch);
				} else if (currentPitch < -maxPitch) {
					front.y = (float) Math.sin(Math.toRadians(-maxPitch));
					adjustHorizontalComponents(front, -maxPitch);
				}

				front.normalize();
				cam.front.set(front);
			}

			private void adjustHorizontalComponents(Vector3f front, float pitch) {
				float horizontalLength = (float) Math.cos(Math.toRadians(pitch));
				float currentHorizontal = (float) Math.sqrt(front.x * front.x + front.z * front.z);
				if (currentHorizontal > 0) {
					front.x = (front.x / currentHorizontal) * horizontalLength;
					front.z = (front.z / currentHorizontal) * horizontalLength;
				} else {
					front.x = horizontalLength;
					front.z = 0;
				}
			}
		};
	}
}
