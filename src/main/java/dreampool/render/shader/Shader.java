package dreampool.render.shader;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface Shader {
	public int ID = 0;

	public void use();

	public void setBool(String name, boolean value);

	public void setInt(String name, int value);

	public void setFloat(String name, float value);

	public void setVec2(String name, float x, float y);

	public void setVec2(String name, Vector2f value);

	public void setVec3(String name, float x, float y, float z);

	public void setVec3(String name, Vector3f value);

	public void setVec4(String name, float x, float y, float z, float w);

	public void setVec4(String name, Vector4f value);

	public void setMat2(String name, Matrix2f value);

	public void setMat3(String name, Matrix3f value);

	public void setMat4(String name, Matrix4f value);
}
