package com.uawebchallenge.cloud.worker;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskManager;
import com.uawebchallenge.cloud.task.TaskRunner;
import com.uawebchallenge.cloud.task.impl.DefaultTaskManager;
import com.uawebchallenge.cloud.task.impl.DefaultTaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Worker {

    private final static Logger logger = LoggerFactory.getLogger(Worker.class);
    private final TaskManager taskManager;
    private final TaskRunner taskRunner;
    private final WorkerSleep workerSleep;
    private boolean stop;

    public Worker(Store store) {
        this.taskManager = new DefaultTaskManager(store);
        this.taskRunner = new DefaultTaskRunner(store);
        this.workerSleep = new WorkerSleep();
    }

    public void stop(boolean stop) {
        this.stop = stop;
    }

    public void work() {
        while (!stop) {
            try {
                Optional<Task> taskOptional = taskManager.nextPendingTask();
                if (taskOptional.isPresent()) {
                    Task task = taskOptional.get();
                    Object result = taskRunner.run(task);
                    taskManager.finishTask(task.getId(), result);
                    workerSleep.reset();
                } else {
                    sleepQuietly(workerSleep.getSleep());
                    workerSleep.increase();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Unexpected InterruptedException when freezing thread.", e);
        }
    }
}
