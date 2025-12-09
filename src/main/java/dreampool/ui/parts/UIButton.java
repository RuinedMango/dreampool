package dreampool.ui.parts;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import dreampool.Window;
import dreampool.IO.DeviceManager;
import dreampool.core.Part;
import dreampool.render.RenderCommand;
import dreampool.render.RenderPipeline;
import dreampool.render.RenderStage;
import dreampool.render.model.Mesh;
import dreampool.render.model.MeshPool.PoolEntry;

public class UIButton extends Part {
	public Vector2f position = new Vector2f();
	public Vector2f size = new Vector2f();
	public Vector4f color = new Vector4f();
	private Vector2f oldPosition = new Vector2f();
	private Vector4f oldColor = new Vector4f();
	private Vector2f oldSize = new Vector2f(0, 0);
	private Vector4f hoverColor = new Vector4f(255, 255, 255, 1);
	private Vector4f baseColor = new Vector4f(0, 0, 0, 1);
	public boolean hovered;
	public boolean clicked;
	public Runnable callback;

	private boolean renderable = false;

	private List<Float> vertices = new ArrayList<>();
	private boolean vertset = false;
	private Mesh mesh = new Mesh();

	static int VAO;
	static int VBO;

	private Image image;

	public UIButton(float x, float y, float w, float h, Runnable callback) {
		position.x = x;
		position.y = y;
		size.x = w;
		size.y = h;
		this.callback = callback;
		setupVAOandVBO();
		renderable = true;
	}

	public UIButton(Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void Start() {
		image = thing.getPart(Image.class);
		if (image != null) {
			image.position.xy(position);
			image.size.get(size);
		}
	}

	@Override
	public void Update() {
		if (enabled) {
			DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer mouseY_tmp = BufferUtils.createDoubleBuffer(1);
			GLFW.glfwGetCursorPos(Window.Singleton.ID, mouseX, mouseY_tmp);
			double mouseY = Window.Singleton.height - mouseY_tmp.get(0);
			if (image != null) {
				image.position.xy(position);
				image.size.get(size);
			}

			boolean insideBound = mouseX.get(0) >= position.x && mouseX.get(0) <= position.x + size.x
					&& mouseY >= position.y && mouseY <= position.y + size.y;

			if (insideBound) {
				color.set(hoverColor);
			} else {
				color.set(baseColor);
			}
			if (!position.equals(oldPosition.x, oldPosition.y)
					|| !color.equals(oldColor.x, oldColor.y, oldColor.z, oldColor.w)
					|| !size.equals(oldSize.x, oldSize.y)) {
				vertset = false;
				oldPosition.set(position);
				oldColor.set(color);
				oldSize.set(size);
			}
			if (renderable) {
				if (!vertset) {
					vertices.clear();

					int[] order = { 0, 1, 2, 0, 2, 3 };

					Vector3f topLeft = new Vector3f(position.x, position.y + size.y, 0);
					Vector3f topRight = new Vector3f(position.x + size.x, position.y + size.y, 0);
					Vector3f bottomLeft = new Vector3f(position.x, position.y, 0);
					Vector3f bottomRight = new Vector3f(position.x + size.x, position.y, 0);

					Vector3f[] quadVertices = { topRight, topLeft, bottomLeft, bottomRight };
					Vector2f[] quadUVs = { new Vector2f(1, 1), new Vector2f(0, 1), new Vector2f(0, 0),
							new Vector2f(1, 0) };

					for (int i = 0; i < 6; i++) {
						int idx = order[i];
						vertices.add(quadVertices[idx].x);
						vertices.add(quadVertices[idx].y);
						vertices.add(quadVertices[idx].z);

						vertices.add(color.x);
						vertices.add(color.y);
						vertices.add(color.z);
						vertices.add(color.w);

						vertices.add(quadUVs[idx].x);
						vertices.add(quadUVs[idx].y);
					}

					vertset = true;
				}
				float[] verticeArray = new float[vertices.size()];
				for (int i = 0; i < vertices.size(); i++) {
					verticeArray[i] = vertices.get(i);
				}

				mesh.entry.vertices = verticeArray;

				RenderPipeline.Singleton.submit(new RenderCommand(RenderStage.UI, mesh, null, null));

				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			}
			if (insideBound && !clicked && GLFW.glfwGetMouseButton(DeviceManager.Singleton.window,
					GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
				callback.run();
				clicked = true;
			}
			if (GLFW.glfwGetMouseButton(DeviceManager.Singleton.window,
					GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_RELEASE) {
				clicked = false;
			}
		}
	}

	void setupVAOandVBO() {
		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 1024 * 1024, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Float.BYTES * 9, 0);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 3);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 7);
		GL20.glEnableVertexAttribArray(2);

		mesh.entry = new PoolEntry(VAO, VBO, -1);

		GL30.glBindVertexArray(0);
	}
}
