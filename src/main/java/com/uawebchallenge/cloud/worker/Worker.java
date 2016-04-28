package com.uawebchallenge.cloud.worker;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
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
                executeTask(taskOptional);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void executeTask(Optional<Task> taskOptional) throws TaskException {
        if (!taskOptional.isPresent()) {
            logger.debug("No pending task was found. Worker is currently idle.");
            sleepQuietly();
            workerSleep.increase();
        } else if (!taskManager.dependenciesResolved(taskOptional.get())) {
            logger.debug(String.format("Task '%s' has dependencies that were not resolved yet. Skipping this task..."));
            sleepQuietly();
            workerSleep.increase();
        } else {
            try {
                Task task = taskOptional.get();
                logger.info(String.format("Executing task '%s'.", task.getId()));
                taskManager.startTask(task.getId());
                Object result = taskRunner.run(task);
                logger.info("Task '%s' .");
                taskManager.finishTask(task.getId(), result);
                workerSleep.reset();
            } catch (ScriptException e) {

            }
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
