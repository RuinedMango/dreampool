package dreampool.render.texture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;

import dreampool.IO.FileUtils;
import dreampool.core.Part;

public class Texture extends Part {
	private String path;
	private ByteBuffer data;
	public int ID;
	public int width;
	public int height;
	public int comps;
	private int unit;
	public boolean inFrustum;

	private static class TextureCacheEntry {
		int id;
		int refCount;

		TextureCacheEntry(int id) {
			this.id = id;
			this.refCount = 1;
		}
	}

	private static Map<String, TextureCacheEntry> textureCache = new HashMap<>();

	public Texture(String path) {
		synchronized (textureCache) {
			TextureCacheEntry entry = textureCache.get(path);
			if (entry != null) {
				this.ID = entry.id;
				entry.refCount++;
				this.path = path;
				return;
			}
		}

		this.path = path;
		ID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		try {
			FileUtils.readImageResource(this);
		} catch (IOException e) {
			e.printStackTrace();
			GL11.glDeleteTextures(ID);
			this.ID = 0;
			return;
		}

		int format;

		if (comps == 3) {
			if ((width & 3) != 0) {
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2 - (width & 1));
			}
			format = GL11.GL_RGB;
		} else {
			format = GL11.GL_RGBA;
		}

		if (data != null) {
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, data);
		}
		STBImage.stbi_image_free(data);

		synchronized (textureCache) {
			textureCache.put(path, new TextureCacheEntry(ID));
		}
	}

	public Texture(String path, int unit) {
		this(path);

		this.unit = unit;
	}

	@Override
	public void Start() {

	}

	@Override
	public void Update() {
		if (!inFrustum) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
		}
	}

	public String getPath() {
		return path;
	}

	public void setData(ByteBuffer data) {
		this.data = data;
	}

	public void destroy() {
		synchronized (textureCache) {
			TextureCacheEntry entry = textureCache.get(path);
			if (entry != null) {
				entry.refCount--;
				if (entry.refCount <= 0) {
					GL11.glDeleteTextures(entry.id);
					textureCache.remove(path);
				}
			}
		}
	}
}
