package net.thucydides.junit.runners;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.junit.runners.model.RunnerScheduler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JUnit scheduler for parallel parameterized tests.
 */
class ParameterizedRunnerScheduler implements RunnerScheduler {

    private  int threadCount;
    private ExecutorService executorService;
    private CompletionService<Void> completionService;
    private Queue<Future<Void>> tasks;

    public ParameterizedRunnerScheduler(Class klass, int threadCount) {


        executorService = Executors.newFixedThreadPool(threadCount,
                                                       new NamedThreadFactory(klass.getSimpleName()));
        completionService = new ExecutorCompletionService<Void>(executorService);
        tasks = new LinkedList<Future<Void>>();
    }

    protected Queue<Future<Void>> getTaskQueue() {
        return new LinkedList(ImmutableList.copyOf(tasks));
    }

    public void schedule(Runnable childStatement) {

        tasks.offer(completionService.submit(childStatement, null));
    }

    public void finished() {
        try {
            while (!tasks.isEmpty()) {
                tasks.remove(completionService.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            while (!tasks.isEmpty()) {
                tasks.poll().cancel(true);
            }
            executorService.shutdownNow();
        }
    }

    static final class NamedThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final ThreadGroup group;

        NamedThreadFactory(String poolName) {
            group = new ThreadGroup(poolName + "-" + poolNumber.getAndIncrement());
        }

        public Thread newThread(Runnable r) {
            return new Thread(group, r, group.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
        }
    }

}
