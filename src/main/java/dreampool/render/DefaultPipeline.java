package dreampool.render;

import dreampool.render.pass.GeometryPass;
import dreampool.render.pass.PostPass;
import dreampool.render.pass.UIPass;

public class DefaultPipeline {
	public static RenderPipeline createForwardPipeline() {
		RenderPipeline pipeline = new RenderPipeline();
		pipeline.addPass(new GeometryPass());
		pipeline.addPass(new UIPass());
		pipeline.addPass(new PostPass());
		return pipeline;
	}
}
