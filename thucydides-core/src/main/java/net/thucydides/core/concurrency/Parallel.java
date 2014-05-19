package net.thucydides.core.concurrency;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parallel {

    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();

    private static final ForkJoinPool fjPool = new ForkJoinPool(NUM_CORES, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public static <T> void blockingFor(
            final Iterable<? extends T> elements,
            final Operation<T> operation) {
        blockingFor(2 * NUM_CORES, elements, operation);
    }

    public static <T> void blockingFor(
            int numThreads,
            final Iterable<? extends T> elements,
            final Operation<T> operation) {
        For(numThreads, elements, operation,
                Integer.MAX_VALUE, TimeUnit.DAYS);
    }

    public static <S extends T, T> void For(
            int numThreads,
            final Iterable<S> elements,
            final Operation<T> operation,
            Integer wait,
            TimeUnit waitUnit) {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(numThreads, numThreads,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        final ThreadSafeIterator<S> itr = new ThreadSafeIterator<S>(elements.iterator());

        for (int i = 0; i < threadPoolExecutor.getMaximumPoolSize(); i++){
            threadPoolExecutor.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    T element;
                    while ((element = itr.next()) != null) {
                        try {
                            operation.perform(element);
                        } catch (Exception e) {
                            Logger.getLogger(Parallel.class.getName())
                                    .log(Level.SEVERE, "Exception during execution of parallel task", e);
                        }
                    }
                    return null;
                }
            });
        }

        threadPoolExecutor.shutdown();

        if (wait != null) {
            try {
                threadPoolExecutor.awaitTermination(wait, waitUnit);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static class ThreadSafeIterator<T> {

        private final Iterator<T> itr;

        public ThreadSafeIterator(Iterator<T> itr) {
            this.itr = itr;
        }

        public synchronized T next() {
            return itr.hasNext() ? itr.next() : null;
        }
    }

    public static interface Operation<T> {
        public void perform(T pParameter);
    }

}