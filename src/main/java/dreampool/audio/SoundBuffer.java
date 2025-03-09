package dreampool.audio;

import java.nio.ShortBuffer;

import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbisInfo;

import dreampool.IO.FileUtils;

public class SoundBuffer{
	public int ID;

	public SoundBuffer(String path){
		ID = AL11.alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()){
			ShortBuffer pcm = FileUtils.readVorbis(path, info);
			AL11.alBufferData(ID, AL11.AL_FORMAT_MONO16, pcm, info.sample_rate());
		}
	}

	public void destroy(){
		AL11.alDeleteBuffers(ID);
	}
}