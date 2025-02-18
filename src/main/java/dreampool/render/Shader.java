package dreampool.render;

import java.nio.FloatBuffer;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import dreampool.IO.FileUtils;

public class Shader {
	public int ID;
	
	public Shader(String vertPath, String fragPath, String tcsPath, String tesPath) {
		String vertexCode = FileUtils.readTextResource(vertPath);
		String fragmentCode = FileUtils.readTextResource(fragPath);
		String tcsCode = FileUtils.readTextResource(tcsPath);
		String tesCode = FileUtils.readTextResource(tesPath);
		int vertex, fragment, tcs, tes;
		vertex = GL46.glCreateShader(GL46.GL_VERTEX_SHADER);
		GL46.glShaderSource(vertex, vertexCode);
		GL46.glCompileShader(vertex);
	    int[] success = new int[1];
	    GL46.glGetShaderiv(vertex, GL46.GL_COMPILE_STATUS, success);
	    if (success[0] == GL46.GL_FALSE) {
	        int[] logLength = new int[1];
	        GL46.glGetShaderiv(vertex, GL46.GL_INFO_LOG_LENGTH, logLength);
	        String log = GL46.glGetShaderInfoLog(vertex, logLength[0]);
	        System.err.println("Shader compilation failed (" + GL46.GL_VERTEX_SHADER + "):\n" + log);
	        GL46.glDeleteShader(vertex);
	    }
		fragment = GL46.glCreateShader(GL46.GL_FRAGMENT_SHADER);
		GL46.glShaderSource(fragment, fragmentCode);
		GL46.glCompileShader(fragment);
	    success = new int[1];
	    GL46.glGetShaderiv(fragment, GL46.GL_COMPILE_STATUS, success);
	    if (success[0] == GL46.GL_FALSE) {
	        int[] logLength = new int[1];
	        GL46.glGetShaderiv(fragment, GL46.GL_INFO_LOG_LENGTH, logLength);
	        String log = GL46.glGetShaderInfoLog(fragment, logLength[0]);
	        System.err.println("Shader compilation failed (" + GL46.GL_FRAGMENT_SHADER + "):\n" + log);
	        GL46.glDeleteShader(fragment);
	    }
		tcs = GL46.glCreateShader(GL46.GL_TESS_CONTROL_SHADER);
		GL46.glShaderSource(tcs, tcsCode);
		GL46.glCompileShader(tcs);
	    success = new int[1];
	    GL46.glGetShaderiv(tcs, GL46.GL_COMPILE_STATUS, success);
	    if (success[0] == GL46.GL_FALSE) {
	        int[] logLength = new int[1];
	        GL46.glGetShaderiv(tcs, GL46.GL_INFO_LOG_LENGTH, logLength);
	        String log = GL46.glGetShaderInfoLog(tcs, logLength[0]);
	        System.err.println("Shader compilation failed (" + GL46.GL_TESS_CONTROL_SHADER + "):\n" + log);
	        GL46.glDeleteShader(tcs);
	    }
		tes = GL46.glCreateShader(GL46.GL_TESS_EVALUATION_SHADER);
		GL46.glShaderSource(tes, tesCode);
		GL46.glCompileShader(tes);
	    success = new int[1];
	    GL46.glGetShaderiv(tes, GL46.GL_COMPILE_STATUS, success);
	    if (success[0] == GL46.GL_FALSE) {
	        int[] logLength = new int[1];
	        GL46.glGetShaderiv(tes, GL46.GL_INFO_LOG_LENGTH, logLength);
	        String log = GL46.glGetShaderInfoLog(tes, logLength[0]);
	        System.err.println("Shader compilation failed (" + GL46.GL_TESS_EVALUATION_SHADER + "):\n" + log);
	        GL46.glDeleteShader(tes);
	    }
		
		ID = GL46.glCreateProgram();
		GL46.glAttachShader(ID, vertex);
		GL46.glAttachShader(ID, fragment);
		GL46.glAttachShader(ID, tcs);
		GL46.glAttachShader(ID, tes);
		GL46.glLinkProgram(ID);
		GL46.glDeleteShader(vertex);
		GL46.glDeleteShader(fragment);
		GL46.glDeleteShader(tcs);
		GL46.glDeleteShader(tes);
	}
	
	public void use() {
		GL46.glUseProgram(ID);
	}
	
	public void setBool(String name, boolean value) {
		GL46.glUniform1i(GL46.glGetUniformLocation(ID, name), value ? 1 : 0);
	}
	public void setInt(String name, int value) {
		GL46.glUniform1i(GL46.glGetUniformLocation(ID, name), value);
	}
	public void setFloat(String name, float value) {
		GL46.glUniform1f(GL46.glGetUniformLocation(ID, name), value);
	}
	public void setVec2(String name, float x, float y) {
		GL46.glUniform2f(GL46.glGetUniformLocation(ID, name), x, y);
	}
	public void setVec2(String name, Vector2f value) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(2);
		value.get(fb);
		GL46.glUniform2fv(GL46.glGetUniformLocation(ID, name), fb);
		fb.clear();
	}
	public void setVec3(String name, float x, float y, float z) {
		GL46.glUniform3f(GL46.glGetUniformLocation(ID, name), x ,y, z);
	}
	public void setVec3(String name, Vector3f value) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(3);
		value.get(fb);
		GL46.glUniform3fv(GL46.glGetUniformLocation(ID, name), fb);
	}
	public void setVec4(String name, float x, float y, float z, float w) {
		GL46.glUniform4f(GL46.glGetUniformLocation(ID, name), x ,y, z, w);
	}
	public void setVec4(String name, Vector4f value) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		value.get(fb);
		GL46.glUniform4fv(GL46.glGetUniformLocation(ID, name), fb);
	}
	public void setMat2(String name, Matrix2f value) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		value.get(fb);
		GL46.glUniformMatrix2fv(GL46.glGetUniformLocation(ID, name), false, fb);
	}
	public void setMat3(String name, Matrix3f value) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(9);
		value.get(fb);
		GL46.glUniformMatrix3fv(GL46.glGetUniformLocation(ID, name), false, fb);
	}
	public void setMat4(String name, Matrix4f value) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		value.get(fb);
		GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(ID, name), false, fb);
	}
}
