package dreampool.IO;

import static org.lwjgl.system.MemoryUtil.NULL;

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

import dreampool.render.texture.Texture;

public class FileUtils {
	public static String readTextResource(String path) {
		InputStream vertStream = FileUtils.class.getResourceAsStream(path);
		try (Scanner s = new Scanner(vertStream).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}

	// TODO make this support more stuff and make it less error prone
	public static class Doublet {
		public float[] vertices;
		public int[] indices;

		public Doublet(float[] vertices, int[] indices) {
			this.vertices = vertices;
			this.indices = indices;
		}
	}

	public static Doublet readObjMeshResource(String path) throws IOException {
		List<Float> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Integer> vertexIndices = new ArrayList<>();
		List<Integer> uvIndices = new ArrayList<>();
		List<Integer> normalIndices = new ArrayList<>();
		List<Vector3f> temp_vertices = new ArrayList<>();
		List<Vector2f> temp_uvs = new ArrayList<>();
		List<Vector3f> temp_normals = new ArrayList<>();
		InputStream modelFile = FileUtils.class.getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(modelFile));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				String[] array = line.replaceAll("v ", "").split(" ");
				Float[] floats = Arrays.stream(array).map(Float::valueOf).toArray(Float[]::new);
				Vector3f vec = new Vector3f(floats[0], floats[1], floats[2]);
				temp_vertices.add(vec);
			} else if (line.startsWith("vt ")) {
				String[] array = line.replaceAll("vt ", "").split(" ");
				Float[] floats = Arrays.stream(array).map(Float::valueOf).toArray(Float[]::new);
				Vector2f vec = new Vector2f(floats[0], floats[1]);
				temp_uvs.add(vec);
			} else if (line.startsWith("vn ")) {
				String[] array = line.replace("vn ", "").split(" ");
				Float[] floats = Arrays.stream(array).map(Float::valueOf).toArray(Float[]::new);
				Vector3f vec = new Vector3f(floats[0], floats[1], floats[2]);
				temp_normals.add(vec);
			} else if (line.startsWith("f ")) {
				String[] array = line.substring(2).trim().split("\\s+");

				for (String set : array) {
					String[] parts = set.split("/");

					int v = Integer.parseInt(parts[0]);
					Integer vt = null;
					Integer vn = null;

					if (parts.length > 1 && !parts[1].isEmpty())
						vt = Integer.parseInt(parts[1]);
					if (parts.length > 2 && !parts[2].isEmpty())
						vn = Integer.parseInt(parts[2]);

					vertexIndices.add(v);
					uvIndices.add(vt != null ? vt : -1);
					normalIndices.add(vn != null ? vn : -1);
				}
			}
		}
		for (int i = 0; i < vertexIndices.size(); i++) {
			indices.add(i);

			int vertexIndex = vertexIndices.get(i);
			Vector3f v = temp_vertices.get(vertexIndex - 1);
			vertices.add(v.x);
			vertices.add(v.y);
			vertices.add(v.z);

			Integer uvIndex = uvIndices.get(i);
			if (uvIndex != null && uvIndex > 0 && uvIndex <= temp_uvs.size()) {
				Vector2f uv = temp_uvs.get(uvIndex - 1);
				vertices.add(uv.x);
				vertices.add(uv.y);
			} else {
				// fill in defaults (0,0)
				vertices.add(0.0f);
				vertices.add(0.0f);
			}

			Integer normalIndex = normalIndices.get(i);
			if (normalIndex != null && normalIndex > 0 && normalIndex <= temp_normals.size()) {
				Vector3f n = temp_normals.get(normalIndex - 1);
				vertices.add(n.x);
				vertices.add(n.y);
				vertices.add(n.z);
			} else {
				// fill in defaults (0,0,0)
				vertices.add(0.0f);
				vertices.add(0.0f);
				vertices.add(0.0f);
			}
		}
		// Convert once here
		float[] verticesList = new float[vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
			verticesList[i] = vertices.get(i);

		int[] indicesList = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++)
			indicesList[i] = indices.get(i);

		return new Doublet(verticesList, indicesList);
	}

	public static ByteBuffer resourceToByteBuffer(String path, int bufferSize) {
		try (InputStream source = FileUtils.class.getResourceAsStream(path)) {
			if (source == null) {
				throw new IOException("Resource not found: " + path);
			}

			ByteBuffer buffer = BufferUtils.createByteBuffer(bufferSize);

			byte[] buf = new byte[1024];
			while (true) {
				int bytes = source.read(buf);
				if (bytes == -1) {
					break;
				}
				buffer.put(buf, 0, bytes);
			}

			buffer.flip();
			return buffer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ShortBuffer readVorbis(String path, STBVorbisInfo info) {
		InputStream vorbisFile = FileUtils.class.getResourceAsStream(path);

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

		ShortBuffer pcm = BufferUtils
				.createShortBuffer(STBVorbis.stb_vorbis_stream_length_in_samples(decoder) * channels);

		STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
		STBVorbis.stb_vorbis_close(decoder);

		return pcm;
	}

	public static ByteBuffer readImageResource(String path, IntBuffer w, IntBuffer h, IntBuffer comp)
			throws IOException {
		w = MemoryStack.stackPush().mallocInt(1);
		h = MemoryStack.stackPush().mallocInt(1);
		comp = MemoryStack.stackPush().mallocInt(1);

		InputStream imageFile = FileUtils.class.getResourceAsStream(path);

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
		if (image == null) {
			System.out.println("Welp gorsh");
		}
		return image;
	}

	public static void readImageResource(Texture text) throws IOException {
		IntBuffer w = MemoryStack.stackPush().mallocInt(1);
		IntBuffer h = MemoryStack.stackPush().mallocInt(1);
		IntBuffer comp = MemoryStack.stackPush().mallocInt(1);

		InputStream imageFile = FileUtils.class.getResourceAsStream(text.getPath());

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
		if (image == null) {
			System.out.println("Welp gorsh");
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
