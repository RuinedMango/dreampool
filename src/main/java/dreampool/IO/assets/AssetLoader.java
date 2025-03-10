package dreampool.IO.assets;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AssetLoader implements Runnable {
    public static AssetLoader Singleton = null;

    private BlockingQueue<AssetLoadingTask> taskQueue = new LinkedBlockingQueue<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = true;

    public AssetLoader() {
	if (Singleton == null) {
	    Singleton = this;
	    executorService.submit(this);
	} else {
	    System.out.println("AssetLoader already exists");
	}
    }

    @Override
    public void run() {
	// TODO Auto-generated method stub
	while (isRunning) {
	    try {
		AssetLoadingTask task = taskQueue.take();
		task.load();
	    } catch (InterruptedException e) {
		Thread.currentThread().interrupt();
		e.printStackTrace();
	    }

	}
    }

    public void submitTask(AssetLoadingTask task) {
	taskQueue.add(task);
    }

    public void shutdown() {
	isRunning = false;
	executorService.shutdown();
    }
}
