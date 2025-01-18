package dreampool.render.texture;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import dreampool.IO.FileUtils;
import dreampool.core.Part;
import dreampool.render.model.Mesh;

public class Texture extends Part {
	private String path;
	private ByteBuffer data;
	public int ID;
	public int width;
	public int height;
	public int comps;
	private int unit;
	public Class[] doBefore = {Mesh.class};
	
	public Texture(String path) {
		this.path = path;
		ID = GL46.glGenTextures();
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, ID);
		
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
		
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
		
	    try {
			FileUtils.readImageResource(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    int format;
	    
        if (comps == 3) {
            if ((width & 3) != 0) {
                GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            format = GL46.GL_RGB;
        } else {
            format = GL46.GL_RGBA;
        }
	    
		if(data != null) {
			GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL46.GL_UNSIGNED_BYTE, data);
		}
		STBImage.stbi_image_free(data);
	}
	
	public Texture(String path, int unit) {
		this.unit = unit;
		this.path = path;
		ID = GL46.glGenTextures();
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, ID);
		
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
		
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
	    GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
		
	    try {
			FileUtils.readImageResource(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    int format;
	    
        if (comps == 3) {
            if ((width & 3) != 0) {
                GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            format = GL46.GL_RGB;
        } else {
            format = GL46.GL_RGBA;
        }
	    
		if(data != null) {
			GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL46.GL_UNSIGNED_BYTE, data);
		}
		STBImage.stbi_image_free(data);
	}
	
	@Override
	public void Start() {
		
	}
	
	@Override
	public void Update() {
		GL46.glActiveTexture(GL46.GL_TEXTURE0 + unit);
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, ID);
	}
	
	public String getPath() {
		return path;
	}
	
	public void setData(ByteBuffer data) {
		this.data = data;
	}
}
