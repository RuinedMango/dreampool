package dreampool.audio;

import org.lwjgl.openal.AL11;

import dreampool.core.Part;

public class NoiseSource extends Part{
	public int ID;
	public SoundBuffer buffer;
	private boolean relative;
	
	public NoiseSource(String path, boolean relative, boolean looping) {
		this.relative = relative;
		buffer = new SoundBuffer(path);
		if(buffer == null) {
			System.out.println("Buffer can't be initialized: " + path);
		}
		ID = AL11.alGenSources();
		
		if(relative) {
			AL11.alSourcei(ID, AL11.AL_SOURCE_RELATIVE, AL11.AL_TRUE);
		}
		if(looping) {
			AL11.alSourcei(ID, AL11.AL_LOOPING, AL11.AL_TRUE);
		}
		AL11.alSourcei(ID, AL11.AL_BUFFER, buffer.ID);
		if(AL11.alGetError() != AL11.AL_NO_ERROR) {
			throw new IllegalStateException("Failed to create sound source");
		}
	}
	
	@Override
	public void Start() {
		if(relative) {
			AL11.alSource3f(ID, AL11.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);
		}
	}
	
	@Override
	public void Update() {
		if(relative) {
			AL11.alSource3f(ID, AL11.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);
		}
	}
	
	public void play() {
		AL11.alSourcePlay(ID);
	}
	
	public void destroy() {
		AL11.alDeleteSources(ID);
		buffer.destroy();
	}
}
