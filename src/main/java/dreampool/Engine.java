package dreampool;

import dreampool.IO.InputManager;
import dreampool.audio.AudioManager;
import dreampool.audio.backend.OpenALBackend;
import dreampool.core.Time;
import dreampool.core.scene.Scene;
import dreampool.core.scene.SceneManager;
import dreampool.example.scenes.ExampleScene;
import dreampool.render.DefaultPipeline;
import dreampool.render.RenderPipeline;
import dreampool.render.backend.OpenGLBackend;
import dreampool.render.backend.RenderBackend;

public class Engine {
	public static Engine Singleton;

	private WindowSystem window;
	private RenderBackend backend;
	private RenderPipeline pipeline;
	private SceneManager sceneManager;
	private Time time;
	private InputManager deviceManager;
	private AudioManager audio;

	public Engine() {
		if (Singleton != null)
			throw new IllegalStateException("Only one engine instance allowed");
	}

	public void init() {
		window = new WindowSystem(1280, 720, "DreamPool Engine");

		backend = new OpenGLBackend();
		pipeline = DefaultPipeline.createForwardPipeline();

		audio = new AudioManager(new OpenALBackend());

		deviceManager = new InputManager(window.ID);

		Scene initialScene = ExampleScene.generateScene();
		sceneManager = new SceneManager(initialScene);

		time = new Time();

		backend.init(window.width, window.height);
		backend.buildPipeline(pipeline);

		input.init();
		audio.init();
		sceneManager.currentScene.Start();
	}

	public void run() {
		if (window == null || backend == null) {
			throw new IllegalStateException("Engine.init() must be called first.");
		}

		while (!window.shouldClose()) {
			time.update();
			input.update();
			audio.update();

			sceneManager.currentScene.Update();

			backend.beginFrame();
			backend.execute(pipeline);
			backend.endFrame();

			window.update();
		}

		shutdown();
	}

	public void shutdown() {
		sceneManager.currentScene.destroy();
		audio.shutdown();
		pipeline.destroy();
		backend.destroy();
		window.destroy();
	}

	public WindowSystem getWindow() {
		return window;
	}

	public RenderBackend getBackend() {
		return backend;
	}

	public RenderPipeline getPipeline() {
		return pipeline;
	}

	public SceneManager getSceneManager() {
		return sceneManager;
	}

	public Time getTime() {
		return time;
	}

	public InputManager getDeviceManager() {
		return deviceManager;
	}

	public AudioManager getAudio() {
		return audio;
	}

}
