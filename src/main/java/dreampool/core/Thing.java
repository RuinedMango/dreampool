package dreampool.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dreampool.core.transform.Transform;

public class Thing {
    public boolean startedOnce = false;
    public Transform transform = new Transform();
    public String name = "nilli";
    public UUID uid = UUID.randomUUID();
    public List<Part> parts = new ArrayList<>();

    public Thing(String name) {
	this.name = name;
    }

    public void Update() {
	for (Part part : parts) {
	    part.Update();
	}
    }

    public void Start() {
	for (Part part : parts) {
	    if (!part.startedOnce) {
		part.startedOnce = true;
		part.Start();

	    }
	}
    }

    public void addPart(Part part) {
	parts.add(0, part);
	part.thing = this;
	part.transform = this.transform;
    }

    @SuppressWarnings("unchecked")
    public <T extends Part> T getPart(String type) {
	for (Part part : parts) {
	    if (part.getClass().getSimpleName().equals(type)) {
		return (T) part;
	    }
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Part> T getPartExtends(String extension) {
	for (Part part : parts) {
	    for (Class<?> interfac : part.getClass().getInterfaces()) {
		if (interfac.getClass().getSimpleName().equals(extension)) {
		    return (T) part;
		}
	    }
	}
	return null;
    }
}
