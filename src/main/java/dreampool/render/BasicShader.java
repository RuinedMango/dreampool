package dreampool.render;

import java.nio.FloatBuffer;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import dreampool.IO.FileUtils;

public class BasicShader{
	public int ID;

	public BasicShader(String vertPath, String fragPath){
		String vertexCode = FileUtils.readTextResource(vertPath);
		String fragmentCode = FileUtils.readTextResource(fragPath);
		int vertex, fragment;
		vertex = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vertex, vertexCode);
		GL20.glCompileShader(vertex);
		int[] success = new int[1];
		GL20.glGetShaderiv(vertex, GL20.GL_COMPILE_STATUS, success);
		if (success[0] == GL11.GL_FALSE){
			int[] logLength = new int[1];
			GL20.glGetShaderiv(vertex, GL20.GL_INFO_LOG_LENGTH, logLength);
			String log = GL20.glGetShaderInfoLog(vertex, logLength[0]);
			System.err.println("Shader compilation failed (" + GL20.GL_VERTEX_SHADER + "):\n" + log);
			GL20.glDeleteShader(vertex);
		}
		fragment = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fragment, fragmentCode);
		GL20.glCompileShader(fragment);
		success = new int[1];
		GL20.glGetShaderiv(fragment, GL20.GL_COMPILE_STATUS, success);
		if (success[0] == GL11.GL_FALSE){
			int[] logLength = new int[1];
			GL20.glGetShaderiv(fragment, GL20.GL_INFO_LOG_LENGTH, logLength);
			String log = GL20.glGetShaderInfoLog(fragment, logLength[0]);
			System.err.println("Shader compilation failed (" + GL20.GL_FRAGMENT_SHADER + "):\n" + log);
			GL20.glDeleteShader(fragment);
		}

		ID = GL20.glCreateProgram();
		GL20.glAttachShader(ID, vertex);
		GL20.glAttachShader(ID, fragment);
		GL20.glLinkProgram(ID);
		GL20.glDeleteShader(vertex);
		GL20.glDeleteShader(fragment);
	}

	public void use(){
		GL20.glUseProgram(ID);
	}

	public void setBool(String name, boolean value){
		GL20.glUniform1i(GL20.glGetUniformLocation(ID, name), value ? 1 : 0);
	}

	public void setInt(String name, int value){
		GL20.glUniform1i(GL20.glGetUniformLocation(ID, name), value);
	}

	public void setFloat(String name, float value){
		GL20.glUniform1f(GL20.glGetUniformLocation(ID, name), value);
	}

	public void setVec2(String name, float x, float y){
		GL20.glUniform2f(GL20.glGetUniformLocation(ID, name), x, y);
	}

	public void setVec2(String name, Vector2f value){
		FloatBuffer fb = BufferUtils.createFloatBuffer(2);
		value.get(fb);
		GL20.glUniform2fv(GL20.glGetUniformLocation(ID, name), fb);
		fb.clear();
	}

	public void setVec3(String name, float x, float y, float z){
		GL20.glUniform3f(GL20.glGetUniformLocation(ID, name), x, y, z);
	}

	public void setVec3(String name, Vector3f value){
		FloatBuffer fb = BufferUtils.createFloatBuffer(3);
		value.get(fb);
		GL20.glUniform3fv(GL20.glGetUniformLocation(ID, name), fb);
	}

	public void setVec4(String name, float x, float y, float z, float w){
		GL20.glUniform4f(GL20.glGetUniformLocation(ID, name), x, y, z, w);
	}

	public void setVec4(String name, Vector4f value){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		value.get(fb);
		GL20.glUniform4fv(GL20.glGetUniformLocation(ID, name), fb);
	}

	public void setMat2(String name, Matrix2f value){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		value.get(fb);
		GL20.glUniformMatrix2fv(GL20.glGetUniformLocation(ID, name), false, fb);
	}

	public void setMat3(String name, Matrix3f value){
		FloatBuffer fb = BufferUtils.createFloatBuffer(9);
		value.get(fb);
		GL20.glUniformMatrix3fv(GL20.glGetUniformLocation(ID, name), false, fb);
	}

	public void setMat4(String name, Matrix4f value){
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		value.get(fb);
		GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(ID, name), false, fb);
	}
}
