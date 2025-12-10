package dreampool.example.scenes;

import dreampool.WindowSystem;
import dreampool.core.Part;
import dreampool.ui.parts.Text;

public class StartText extends Part {
	public Text text;

	public StartText(Text text) {
		this.text = text;
	}

	@Override
	public void Update() {
		float textWidth = text.textMax.x - text.textMin.x;
		float textHeight = text.textMax.y - text.textMin.y;

		float centerX = WindowSystem.Singleton.width / 2.0f;
		float centerY = WindowSystem.Singleton.height / 2.0f;

		text.position.x = centerX - (textWidth / 2.0f);
		text.position.y = centerY - (textHeight / 2.0f) + 100;
	}
}
