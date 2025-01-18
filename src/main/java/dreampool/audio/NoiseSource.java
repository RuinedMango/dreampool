package dreampool.audio;

import org.lwjgl.openal.AL11;

import dreampool.core.Part;

public class NoiseSource extends Part{
	public int ID;
	public SoundBuffer buffer;
	
	public NoiseSource(String path, int relative, int looping) {
		buffer = new SoundBuffer(path);
		if(buffer == null) {
			System.out.println("Buffer can't be initialized: " + path);
		}
		ID = AL11.alGenSources();
		AL11.alSourcei(ID, AL11.AL_SOURCE_RELATIVE, relative);
		AL11.alSourcei(ID, AL11.AL_BUFFER, buffer.ID);
		AL11.alSourcei(ID, AL11.AL_LOOPING, looping);
		if(AL11.alGetError() != AL11.AL_NO_ERROR) {
			throw new IllegalStateException("Failed to create sound source");
		}
	}
	
	@Override
	public void Start() {
		AL11.alSource3f(ID, AL11.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);
	}
	
	@Override
	public void Update() {
		AL11.alSource3f(ID, AL11.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);
	}
	
	public void play() {
		AL11.alSourcePlay(ID);
	}
	
	public void destroy() {
		AL11.alDeleteSources(ID);
		buffer.destroy();
	}
}
