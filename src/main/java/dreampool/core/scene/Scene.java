package dreampool.core.scene;

import java.util.ArrayList;
import java.util.List;

import dreampool.core.Thing;
import dreampool.render.fog.Fog;

public class Scene{
	public Fog fog;
	public String name;
	public List<Thing> things = new ArrayList<>();

	public Scene(String name){
		this.name = name;
	}

	public void addThing(Thing thing){
		things.add(thing);
	}

	public void Update(){
		fog.Update();
		for (Thing thing : things){
			thing.Update();
		}
	}

	public void Start(){
		if (fog == null){
			fog = new Fog();
		}
		fog.Start();
		for (Thing thing : things){
			if (!thing.startedOnce){
				thing.startedOnce = true;
				thing.Start();
			}
		}
	}
}
