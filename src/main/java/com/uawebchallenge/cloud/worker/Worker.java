package com.uawebchallenge.cloud.worker;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskLookup;
import com.uawebchallenge.cloud.task.TaskRunner;
import com.uawebchallenge.cloud.task.impl.DefaultTaskLookup;
import com.uawebchallenge.cloud.task.impl.DefaultTaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Worker {

    private final static Logger logger = LoggerFactory.getLogger(Worker.class);
    private final TaskRunner taskRunner;
    private final TaskLookup taskLookup;
    private final WorkerSleep workerSleep;
    private boolean stop;

    public Worker(Store store) {
        this.taskRunner = new DefaultTaskRunner(store);
        this.taskLookup = new DefaultTaskLookup(store);
        this.workerSleep = new WorkerSleep();
    }

    public void stop() {
        this.stop = true;
    }

    public void work() {
        while (!stop) {
            try {
                Optional<Task> taskOptional = taskLookup.nextPendingTask();
                executeTask(taskOptional);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void executeTask(Optional<Task> taskOptional) throws TaskException {
        if (!taskOptional.isPresent()) {
            logger.debug("No pending task was found. Worker is currently idle.");
            sleepQuietly();
            workerSleep.increase();
        } else {
            Task task = taskOptional.get();
            taskRunner.run(task);
            workerSleep.reset();
        }
    }

    private void sleepQuietly() {
        try {
            logger.trace(String.format("I'll wait for %d milliseconds.", workerSleep.getSleep()));
            Thread.sleep(workerSleep.getSleep());
            workerSleep.increase();
        } catch (InterruptedException e) {
            logger.error("Unexpected InterruptedException when freezing thread.", e);
        }
    }
}
