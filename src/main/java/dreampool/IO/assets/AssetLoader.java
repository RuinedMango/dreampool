package dreampool.IO.assets;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AssetLoader implements Runnable{
	private BlockingQueue<AssetLoadingTask> taskQueue = new LinkedBlockingQueue<>();
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private volatile boolean isRunning = true;

	public AssetLoader(){
		executorService.submit(this);
	}

	@Override
	public void run(){
		// TODO Auto-generated method stub
		while (isRunning){

		}
	}
}
