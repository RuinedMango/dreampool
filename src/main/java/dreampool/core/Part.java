package dreampool.core;

import dreampool.core.transform.Transform;

public class Part {
	public Class<? extends Part>[] doBefore = null;
	public Class<? extends Part>[] doAfter = null;
	public boolean startedOnce = false;
	public String type;
	public Thing thing;
	public Transform transform;

	public void Start() {
	}

	public void Update() {
	}
}
