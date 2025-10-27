package dreampool.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dreampool.core.scene.Scene;
import dreampool.core.transform.Transform;

public class Thing {
	// TODO fix runtime instantiation
	public Scene scene;
	public boolean startedOnce = false;
	public Transform transform = new Transform();
	public String name = "nilli";
	public UUID uid = UUID.randomUUID();
	public List<Part> parts = new ArrayList<>();
	public List<Part> toAdd = new ArrayList<>();
	// TODO actually implement removing
	public List<Part> toRemove = new ArrayList<>();

	public Thing(String name) {
		this.name = name;
	}

	public void Update() {
		for (Part part : parts) {
			part.Update();
		}

		if (!toAdd.isEmpty()) {
			parts.addAll(toAdd);
			toAdd.clear();
		}
	}

	public void Start() {
		if (!toAdd.isEmpty()) {
			parts.addAll(toAdd);
			toAdd.clear();
		}
		for (Part part : parts) {
			if (!part.startedOnce) {
				part.startedOnce = true;
				part.Start();

			}
		}
	}

	public void addPart(Part part) {
		part.thing = this;
		part.transform = this.transform;
		toAdd.add(part);
	}

	// TODO figure out how to remove suppress warning.
	@SuppressWarnings("unchecked")
	public <T extends Part> T getPart(String type) {
		for (Part part : parts) {
			if (part.getClass().getSimpleName().equals(type)) {
				return (T) part;
			}
		}
		return null;
	}

	// TODO here too
	@SuppressWarnings("unchecked")
	public <T extends Part> T getPartExtendsOrImplements(String extension) {
		for (Part part : parts) {
			for (Class<?> interfac : part.getClass().getInterfaces()) {
				if (interfac.getSimpleName().equals(extension)) {
					return (T) part;
				}
			}
		}
		return null;
	}
}
