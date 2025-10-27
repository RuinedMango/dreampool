package dreampool.core.scene;

public class SceneManager {
	// TODO make it load external scenes with Janino
	public static SceneManager Singleton;
	public Scene currentScene;

	public SceneManager(Scene startScene) {
		if (Singleton == null) {
			Singleton = this;
			currentScene = startScene;
		} else {
			System.out.println("SceneManager exists");
		}
	}

	public void setScene(Scene scene) {
		currentScene = scene;
	}
}
