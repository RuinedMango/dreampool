package dreampool.audio;

import org.lwjgl.openal.AL10;

import dreampool.core.Part;

public class NoiseSource extends Part {
	public int ID;
	public SoundBuffer buffer;
	private boolean relative;

	public NoiseSource(String path, boolean relative, boolean looping) {
		this.relative = relative;
		buffer = new SoundBuffer(path);
		if (buffer == null) {
			System.out.println("Buffer can't be initialized: " + path);
		}
		ID = AL10.alGenSources();

		if (relative) {
			AL10.alSourcei(ID, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
		}
		if (looping) {
			AL10.alSourcei(ID, AL10.AL_LOOPING, AL10.AL_TRUE);
		}
		AL10.alSourcei(ID, AL10.AL_BUFFER, buffer.ID);
		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			throw new IllegalStateException("Failed to create sound source");
		}
	}

	@Override
	public void Start() {
		// Why is relative checked here?
		if (relative) {
			AL10.alSource3f(ID, AL10.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);
		}
	}

	@Override
	public void Update() {
		// Here aswell?
		if (relative) {
			AL10.alSource3f(ID, AL10.AL_POSITION, transform.position.x, transform.position.y, transform.position.z);
		}
	}

	public void play() {
		AL10.alSourcePlay(ID);
	}

	public void destroy() {
		AL10.alDeleteSources(ID);
		buffer.destroy();
	}
}
