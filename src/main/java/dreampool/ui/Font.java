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
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import dreampool.Application;
import dreampool.IO.FileUtils;
import dreampool.render.BasicShader;

public class Font{
	public ByteBuffer bitmap;
	public static Matrix4f altProj = null;
	
	private int texture;
	
	private final float[] scale = {64.0f, 14.0f};
	private STBTTPackedchar.Buffer chardata;
	private STBTTAlignedQuad.Buffer alignedQuads;
	
	private List<Float> vertices = new ArrayList<Float>();
	
	public static BasicShader shader = new BasicShader("/shaders/text.vert", "/shaders/text.frag");
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
        
	    texture = GL46.glGenTextures();
	    GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
	    
	    GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_R8, 512, 512, 0, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, bitmap);
	    
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
	    
	    GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
	    
	    //Output debug atlas
	    //STBImageWrite.stbi_write_png("fontAtlas.png", 512, 512, 1, bitmap, 512);
	    
	    setupVAOandVBO();
	}
	
	public void renderText(String text, Vector3f position, Vector4f color, float size) {
		vertices.clear();
		
		int[] order = {0, 1, 2, 0 , 2, 3};
		
		Vector3f localPos = new Vector3f(position);
		
		float scaleFactor = size / scale[0];
		
		for(char ch : text.toCharArray()) {
			if(ch >= 32 && ch <= 126) {
				if(ch == ' ') {
		            localPos.x += 10 * scaleFactor;
				}else {
					STBTTPackedchar packedChar = chardata.get(ch - 32);
					STBTTAlignedQuad alignedQuad = alignedQuads.get(ch - 32);
					
	                Vector2f glyphSize = new Vector2f(
	                        (packedChar.x1() - packedChar.x0()) * scaleFactor,
	                        (packedChar.y1() - packedChar.y0()) * scaleFactor
	                    );

	                    Vector2f glyphBoundingBoxBottomLeft = new Vector2f(
	                        localPos.x + (packedChar.xoff() * scaleFactor),
	                        localPos.y - (packedChar.yoff() + packedChar.y1() - packedChar.y0()) * scaleFactor
	                    );

					
					Vector2f[] glyphVertices = {
							new Vector2f(glyphBoundingBoxBottomLeft.x + glyphSize.x, glyphBoundingBoxBottomLeft.y + glyphSize.y),
							new Vector2f(glyphBoundingBoxBottomLeft.x, glyphBoundingBoxBottomLeft.y + glyphSize.y),
							new Vector2f(glyphBoundingBoxBottomLeft.x, glyphBoundingBoxBottomLeft.y),
							new Vector2f(glyphBoundingBoxBottomLeft.x + glyphSize.x, glyphBoundingBoxBottomLeft.y),
					};
					
					Vector2f[] glyphTextureCoords = {
							new Vector2f(alignedQuad.s1(), alignedQuad.t0()),
							new Vector2f(alignedQuad.s0(), alignedQuad.t0()),
							new Vector2f(alignedQuad.s0(), alignedQuad.t1()),
							new Vector2f(alignedQuad.s1(), alignedQuad.t1())
					};
					
					for(int i = 0; i < 6; i++) {
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
			}else if(ch == '\n') {
				localPos.y -= scale[0] * scaleFactor;
				localPos.x = position.x;
			}
		}
		
		int vertexCount = vertices.size() / 9;
		
		GL46.glDisable(GL46.GL_DEPTH_TEST);
		shader.use();
		
		GL46.glActiveTexture(GL46.GL_TEXTURE0);
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
		
		shader.setInt("uFontAtlasTexture", 0);
		if(altProj == null) {
			shader.setMat4("projection", new Matrix4f().ortho(0, Application.width, 0, Application.height, -1, 1));
		}else {
			shader.setMat4("projection", altProj);
		}
		
		GL46.glBindVertexArray(VAO);
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO);
		float[] verticeArray = new float[vertices.size()];
		for(int i = 0; i < vertices.size(); i++) verticeArray[i] = vertices.get(i);
		FloatBuffer fb = BufferUtils.createFloatBuffer(verticeArray.length);
		fb.put(verticeArray).flip();
		GL46.glBufferSubData(GL46.GL_ARRAY_BUFFER, 0, fb);
		
		GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, vertexCount);
	}
	
	static void setupVAOandVBO() {
		VAO = GL46.glGenVertexArrays();
		GL46.glBindVertexArray(VAO);
		
		VBO = GL46.glGenBuffers();
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO);
		GL46.glBufferData(GL46.GL_ARRAY_BUFFER, 1024 * 1024, GL46.GL_DYNAMIC_DRAW);
		
		GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, Float.BYTES * 9, 0);
		GL46.glEnableVertexAttribArray(0);
		GL46.glVertexAttribPointer(1, 4, GL46.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 3);
		GL46.glEnableVertexAttribArray(1);
		GL46.glVertexAttribPointer(2, 2, GL46.GL_FLOAT, false, Float.BYTES * 9, Float.BYTES * 7);
		GL46.glEnableVertexAttribArray(2);
		
		GL46.glBindVertexArray(0);
	}
	static float[] verticesTest = {
		    // x, y, depth (z), r, g, b, a, texX, texY
		    100.0f, 100.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // Vertex 1
		    200.0f, 100.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, // Vertex 2
		    200.0f, 200.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, // Vertex 3
		    100.0f, 200.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f  // Vertex 4
		};
}