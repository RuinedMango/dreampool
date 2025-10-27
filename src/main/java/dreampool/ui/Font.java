package dreampool.ui;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import dreampool.Application;
import dreampool.IO.FileUtils;
import dreampool.render.BasicShader;

public class Font {
	public ByteBuffer bitmap;
	public static Matrix4f altProj = null;

	private int texture;

	private final float[] scale = { 64.0f, 14.0f };
	private STBTTPackedchar.Buffer chardata;
	private STBTTAlignedQuad.Buffer alignedQuads;

	private List<Float> vertices = new ArrayList<>();

	public static BasicShader shader = new BasicShader("/shaders/ui.vert", "/shaders/font.frag");
	static int VAO;
	static int VBO;

	public Font(String path) {
		ByteBuffer ttf = FileUtils.resourceToByteBuffer(path, 512 * 1024);
		bitmap = BufferUtils.createByteBuffer(512 * 512);
		chardata = STBTTPackedchar.malloc(95);
		alignedQuads = STBTTAlignedQuad.malloc(95);

		STBTTPackContext pc = STBTTPackContext.malloc();
		STBTruetype.stbtt_PackBegin(pc, bitmap, 512, 512, 0, 1, MemoryUtil.NULL);
		STBTruetype.stbtt_PackSetOversampling(pc, 1, 1);
		STBTruetype.stbtt_PackFontRange(pc, ttf, 0, scale[0], 32, chardata);
		STBTruetype.stbtt_PackEnd(pc);

		for (int i = 0; i < 95; i++) {
			FloatBuffer unusedX = BufferUtils.createFloatBuffer(1);
			FloatBuffer unusedY = BufferUtils.createFloatBuffer(1);
			STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
			STBTruetype.stbtt_GetPackedQuad(chardata, 512, 512, i, unusedX, unusedY, quad, false);
			alignedQuads.put(i, quad);
		}

		texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_R8, 512, 512, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, bitmap);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		// Output debug atlas
		// STBImageWrite.stbi_write_png("fontAtlas.png", 512, 512, 1, bitmap, 512);

		setupVAOandVBO();
	}

	public void renderText(String text, Vector3f position, Vector4f color, float size) {
		vertices.clear();

		int[] order = { 0, 1, 2, 0, 2, 3 };

		Vector3f localPos = new Vector3f(position);

		float scaleFactor = size / scale[0];

		for (char ch : text.toCharArray()) {
			if (ch >= 32 && ch <= 126) {
				if (ch == ' ') {
					localPos.x += 10 * scaleFactor;
				} else {
					STBTTPackedchar packedChar = chardata.get(ch - 32);
					STBTTAlignedQuad alignedQuad = alignedQuads.get(ch - 32);

					Vector2f glyphSize = new Vector2f((packedChar.x1() - packedChar.x0()) * scaleFactor,
							(packedChar.y1() - packedChar.y0()) * scaleFactor);

					Vector2f glyphBoundingBoxBottomLeft = new Vector2f(localPos.x + (packedChar.xoff() * scaleFactor),
							localPos.y - (packedChar.yoff() + packedChar.y1() - packedChar.y0()) * scaleFactor);

					Vector2f[] glyphVertices = {
							new Vector2f(glyphBoundingBoxBottomLeft.x + glyphSize.x,
									glyphBoundingBoxBottomLeft.y + glyphSize.y),
							new Vector2f(glyphBoundingBoxBottomLeft.x, glyphBoundingBoxBottomLeft.y + glyphSize.y),
							new Vector2f(glyphBoundingBoxBottomLeft.x, glyphBoundingBoxBottomLeft.y),
							new Vector2f(glyphBoundingBoxBottomLeft.x + glyphSize.x, glyphBoundingBoxBottomLeft.y), };

					Vector2f[] glyphTextureCoords = { new Vector2f(alignedQuad.s1(), alignedQuad.t0()),
							new Vector2f(alignedQuad.s0(), alignedQuad.t0()),
							new Vector2f(alignedQuad.s0(), alignedQuad.t1()),
							new Vector2f(alignedQuad.s1(), alignedQuad.t1()) };

					for (int i = 0; i < 6; i++) {
						vertices.add(glyphVertices[order[i]].x);
						vertices.add(glyphVertices[order[i]].y);
						vertices.add(position.z);
						vertices.add(color.x);
						vertices.add(color.y);
						vertices.add(color.z);
						vertices.add(color.w);
						vertices.add(glyphTextureCoords[order[i]].x);
						vertices.add(glyphTextureCoords[order[i]].y);
					}

					localPos.x += packedChar.xadvance() * scaleFactor;
				}
			} else if (ch == '\n') {
				localPos.y -= scale[0] * scaleFactor;
				localPos.x = position.x;
			}
		}

		int vertexCount = vertices.size() / 9;

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.use();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

		shader.setInt("uTexture", 0);
		if (altProj == null) {
			shader.setMat4("projection", new Matrix4f().ortho(0, Application.width, 0, Application.height, -1, 1));
		} else {
			shader.setMat4("projection", altProj);
		}

		GL30.glBindVertexArray(VAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		float[] verticeArray = new float[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			verticeArray[i] = vertices.get(i);
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(verticeArray.length);
		fb.put(verticeArray).flip();
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fb);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
	}

	static void setupVAOandVBO() {
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

		GL30.glBindVertexArray(0);
	}

	static float[] verticesTest = {
			// x, y, depth (z), r, g, b, a, texX, texY
			100.0f, 100.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // Vertex 1
			200.0f, 100.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, // Vertex 2
			200.0f, 200.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, // Vertex 3
			100.0f, 200.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f // Vertex 4
	};
}