package dreampool.core.scene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dreampool.core.Thing;
import dreampool.physics.bounds.Collider;
import dreampool.render.model.Mesh;

public class Scene {
	public String name;
	public List<Thing> things = new ArrayList<>();
	public List<Thing> toAdd = new ArrayList<>();
	public List<Thing> toRemove = new ArrayList<>();

	public Scene(String name) {
		this.name = name;
	}

	public void addThing(Thing thing) {
		thing.scene = this;
		toAdd.add(thing);
	}

	public void removeThing(Thing thing) {
		toRemove.add(thing);
	}

	public void Update() {
		if (!toAdd.isEmpty()) {
			things.addAll(toAdd);
			toAdd.clear();
		}
		if (!toRemove.isEmpty()) {
			things.removeAll(toRemove);
			toRemove.clear();
		}
		things.sort(Comparator.comparingInt(t -> t.renderOrder));
		for (Thing thing : things) {
			if (!thing.startedOnce) {
				thing.startedOnce = true;
				thing.Start();
			}
			thing.Update();
		}
	}

	public void Start() {
		if (!toAdd.isEmpty()) {
			things.addAll(toAdd);
			toAdd.clear();
		}
		if (!toRemove.isEmpty()) {
			things.removeAll(toRemove);
			toRemove.clear();
		}
		for (Thing thing : things) {
			if (!thing.startedOnce) {
				thing.startedOnce = true;
				thing.Start();
			}
		}
	}

	public List<Collider> getColliders() {
		List<Collider> colliders = new ArrayList<>();
		for (Thing thing : this.things) {
			Collider collider = thing.getPartExtendsOrImplements(Collider.class);
			if (collider != null) {
				colliders.add(collider);
			}
		}
		return colliders;
	}

	public List<Mesh> getMeshes() {
		List<Mesh> meshes = new ArrayList<>();
		for (Thing thing : this.things) {
			Mesh mesh = thing.getPartExtendsOrImplements(Mesh.class);
			if (mesh != null) {
				meshes.add(mesh);
			}
		}
		return meshes;
	}

	public void destroy() {
		toAdd.clear();
		toRemove.clear();
		for (Thing thing : this.things) {
			thing.Destroy();
		}
	}
}
