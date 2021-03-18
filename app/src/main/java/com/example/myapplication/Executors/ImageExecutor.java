package com.example.myapplication.Executors;

import java.util.concurrent.Executor;

/**
 * Generic executor.
 */
public class ImageExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
