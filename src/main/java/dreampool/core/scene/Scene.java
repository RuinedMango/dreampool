package dreampool.core.scene;

import java.util.ArrayList;
import java.util.List;

import dreampool.core.Thing;

public class Scene {
	public String name;
	public List<Thing> things = new ArrayList<Thing>();
	
	public Scene(String name) {
		this.name = name;
	}
	
	public void addThing(Thing thing) {
		things.add(thing);
	}
	public void Update() {
		for(Thing thing : things) {
			thing.Update();
		}
	}
	public void Start() {
		for(Thing thing : things) {
			if(!thing.startedOnce) {
				thing.startedOnce = true;
				thing.Start();
			}
		}
	}
}
