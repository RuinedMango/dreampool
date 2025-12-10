package dreampool.audio;

import dreampool.audio.backend.AudioBackend;

public class AudioManager {
	private AudioBackend backend;

	public AudioManager(AudioBackend backend) {
		this.backend = backend;
	}
}
