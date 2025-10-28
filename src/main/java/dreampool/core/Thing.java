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
	public int renderOrder = 0;
	public Transform transform = new Transform();
	public String name = "nilli";
	public UUID uid = UUID.randomUUID();
	public List<Part> parts = new ArrayList<>();
	public List<Part> toAdd = new ArrayList<>();
	// TODO actually implement removing
	public List<Part> toRemove = new ArrayList<>();
	private boolean dirty = false;

	public Thing(String name) {
		this.name = name;
	}

	public void Update() {
		if (dirty) {
			parts.addAll(toAdd);
			toAdd.clear();
			reorderParts();
			dirty = false;
		}
		for (Part part : parts) {
			part.Update();
		}
	}

	public void Start() {
		if (dirty) {
			parts.addAll(toAdd);
			toAdd.clear();
			reorderParts();
			dirty = false;
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
		dirty = true;

		if (part.desiredRenderOrder != null) {
			this.renderOrder = part.desiredRenderOrder;
		}
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

	// Reorders parts by dependencies and dependents
	private void reorderParts() {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < parts.size(); i++) {
				Part a = parts.get(i);
				if (a.doBefore != null) {
					for (Class<? extends Part> cls : a.doBefore) {
						int j = indexOf(cls);
						if (j != -1 && i > j) {
							parts.remove(a);
							parts.add(j, a);
							changed = true;
							break;
						}
					}
				}
				if (changed)
					break;
				if (a.doAfter != null) {
					for (Class<? extends Part> cls : a.doAfter) {
						int j = indexOf(cls);
						if (j != -1 && i < j) {
							parts.remove(a);
							if (j >= parts.size())
								j = parts.size() - 1;
							parts.add(j + 1, a);
							changed = true;
							break;
						}
					}
				}
				if (changed)
					break;
			}
		}
	}

	private int indexOf(Class<? extends Part> cls) {
		for (int i = 0; i < parts.size(); i++) {
			if (cls.isAssignableFrom(parts.get(i).getClass())) {
				return i;
			}
		}
		return -1;
	}
}
