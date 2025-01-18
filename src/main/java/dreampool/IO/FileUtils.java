package dreampool.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.system.MemoryUtil.NULL;

import dreampool.render.texture.Texture;

public class FileUtils {
	public static String readTextResource(String path) {
		InputStream vertStream = Class.class.getResourceAsStream(path);
		try (Scanner s = new Scanner(vertStream).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}
	
	public static void readObjMeshResource(String path, List<Float> vertices, List<Integer> indices) throws IOException {
		List<Integer> vertexIndices = new ArrayList<Integer>();
		List<Integer> uvIndices = new ArrayList<Integer>();
		List<Vector3f> temp_vertices = new ArrayList<Vector3f>();
		List<Vector2f> temp_uvs = new ArrayList<Vector2f>();
		InputStream modelFile = Class.class.getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(modelFile));
		String line;
		while((line = reader.readLine()) != null) {
			if(line.startsWith("v ")) {
				String[] array = line.replaceAll("v ", "").split(" ");
				Float[] floats = Arrays.stream(array).map(Float::valueOf).toArray(Float[]::new);
		    	Vector3f vec = new Vector3f(floats[0], floats[1], floats[2]);
		    	temp_vertices.add(vec);
			} else if(line.startsWith("vt ")) {
		    	String[] array = line.replaceAll("vt ", "").split(" ");
		    	Float[] floats = Arrays.stream(array).map(Float::valueOf).toArray(Float[]::new);
		    	Vector2f vec = new Vector2f(floats[0], floats[1]);
		    	temp_uvs.add(vec);
			} else if(line.startsWith("f")) {
		    	String[] array = line.replaceAll("f ", "").split(" ");
		    	for(String set : array) {
		    		String[] indiceArray = set.split("/");
		    		Integer[] values = Arrays.stream(indiceArray).map(Integer::valueOf).toArray(Integer[]::new);
		    		vertexIndices.add(values[0]);
		    		uvIndices.add(values[1]);
		    	}
		    }
		}
		for(int i = 0; i < vertexIndices.size(); i++) {
			indices.add(i);
			int vertexIndex = vertexIndices.get(i);
			Float[] vertex = {temp_vertices.get(vertexIndex - 1).x, temp_vertices.get(vertexIndex - 1).y, temp_vertices.get(vertexIndex - 1).z};
			for(float value : vertex) {
				vertices.add(value);
			}
			int uvIndex = uvIndices.get(i);
			Float[] uv = {temp_uvs.get(uvIndex - 1).x, temp_uvs.get(uvIndex - 1).y};
			for(float value : uv) {
				vertices.add(value);
			}
		}
	}
	
	public static ShortBuffer readVorbis(String path, STBVorbisInfo info) {
        InputStream vorbisFile = Class.class.getResourceAsStream(path); 
        
        ReadableByteChannel rbc = Channels.newChannel(vorbisFile);
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(20);

        while (true) {
            int bytes = 0;
			try {
				bytes = rbc.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
            if (bytes == -1) {
                break;
            }
            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
            }
        }
        
        buffer.flip();
        
        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoder = STBVorbis.stb_vorbis_open_memory(buffer, error, null);
        if (decoder == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }
        
        STBVorbis.stb_vorbis_get_info(decoder, info);
        
        int channels = info.channels();
        
        ShortBuffer pcm = BufferUtils.createShortBuffer(STBVorbis.stb_vorbis_stream_length_in_samples(decoder) * channels);
        
        STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        STBVorbis.stb_vorbis_close(decoder);
        
        return pcm;
	}
	
	public static ByteBuffer readImageResource(String path, IntBuffer w, IntBuffer h, IntBuffer comp) throws IOException {
        w = MemoryStack.stackPush().mallocInt(1);
        h = MemoryStack.stackPush().mallocInt(1);
        comp = MemoryStack.stackPush().mallocInt(1);
        
        InputStream imageFile = Class.class.getResourceAsStream(path); 
        
        ReadableByteChannel rbc = Channels.newChannel(imageFile);
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(20);

        while (true) {
            int bytes = rbc.read(buffer);
            if (bytes == -1) {
                break;
            }
            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
            }
        }
        
        buffer.flip();
        
        STBImage.stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = STBImage.stbi_load_from_memory(MemoryUtil.memSlice(buffer), w, h, comp, 0);
		if(image == null) {
			System.out.println("fuck");
		}
		return image;
	}
	
	public static void readImageResource(Texture text) throws IOException {
        IntBuffer w = MemoryStack.stackPush().mallocInt(1);
        IntBuffer h = MemoryStack.stackPush().mallocInt(1);
        IntBuffer comp = MemoryStack.stackPush().mallocInt(1);
        
        InputStream imageFile = Class.class.getResourceAsStream(text.getPath()); 
        
        ReadableByteChannel rbc = Channels.newChannel(imageFile);
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(20);

        while (true) {
            int bytes = rbc.read(buffer);
            if (bytes == -1) {
                break;
            }
            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
            }
        }
        
        buffer.flip();
        
        STBImage.stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = STBImage.stbi_load_from_memory(MemoryUtil.memSlice(buffer), w, h, comp, 0);
		if(image == null) {
			System.out.println("fuck");
		}
		text.width = w.get(0);
		text.height = h.get(0);
		text.comps = comp.get(0);
		text.setData(image);
	}
	
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
