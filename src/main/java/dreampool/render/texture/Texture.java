package dreampool.render.texture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import dreampool.IO.FileUtils;
import dreampool.core.Part;

public class Texture extends Part{
	private String path;
	private ByteBuffer data;
	public int ID;
	public int width;
	public int height;
	public int comps;
	private int unit;

	private static class TextureCacheEntry{
		int id;
		int refCount;

		TextureCacheEntry(int id){
			this.id = id;
			this.refCount = 1;
		}
	}

	private static Map<String, TextureCacheEntry> textureCache = new HashMap<>();

	public Texture(String path){
		synchronized (textureCache){
			TextureCacheEntry entry = textureCache.get(path);
			if (entry != null){
				this.ID = entry.id;
				entry.refCount++;
				this.path = path;
				return;
			}
		}

		this.path = path;
		ID = GL46.glGenTextures();
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, ID);

		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);

		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);

		try{
			FileUtils.readImageResource(this);
		}catch (IOException e){
			e.printStackTrace();
			GL46.glDeleteTextures(ID);
			this.ID = 0;
			return;
		}

		int format;

		if (comps == 3){
			if ((width & 3) != 0){
				GL46.glPixelStorei(GL46.GL_UNPACK_ALIGNMENT, 2 - (width & 1));
			}
			format = GL46.GL_RGB;
		}else{
			format = GL46.GL_RGBA;
		}

		if (data != null){
			GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL46.GL_UNSIGNED_BYTE, data);
		}
		STBImage.stbi_image_free(data);

		synchronized (textureCache){
			textureCache.put(path, new TextureCacheEntry(ID));
		}
	}

	public Texture(String path, int unit){
		this(path);

		this.unit = unit;
	}

	@Override
	public void Start(){

	}

	@Override
	public void Update(){
		GL46.glActiveTexture(GL46.GL_TEXTURE0 + unit);
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, ID);
	}

	public String getPath(){
		return path;
	}

	public void setData(ByteBuffer data){
		this.data = data;
	}

	public void destroy(){
		synchronized (textureCache){
			TextureCacheEntry entry = textureCache.get(path);
			if (entry != null){
				entry.refCount--;
				if (entry.refCount <= 0){
					GL46.glDeleteTextures(entry.id);
					textureCache.remove(path);
				}
			}
		}
	}
}
