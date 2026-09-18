package org.vlaskin.bot.utils.concurrent;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LockByKeyTest
{
    @Test
    void serializesConcurrentAccessAndRemovesUnusedLocks() throws Exception
    {
        var locks = new LockByKey<Integer>();
        var active = new AtomicInteger();
        var violations = new AtomicInteger();
        var start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(8, Thread.ofPlatform().daemon(true).factory());
        try
        {
            var futures = new ArrayList<Future<?>>();
            for (int i = 0; i < 8; i++)
                futures.add(executor.submit(() -> {
                    start.await();
                    for (int n = 0; n < 500; n++)
                    {
                        locks.lockInterruptibly(42);
                        try
                        {
                            if (active.incrementAndGet() != 1) violations.incrementAndGet();
                            active.decrementAndGet();
                        }
                        finally { locks.unlock(42); }
                    }
                    return null;
                }));
            start.countDown();
            for (var future : futures) future.get(10, TimeUnit.SECONDS);
        }
        finally
        {
            start.countDown();
            stop(executor);
        }
        assertThat(violations.get()).isZero();
        assertThat(locks.activeLockCount()).isZero();
    }

    @Test
    void supportsReentrancyIndependentKeysAndIndependentInstances() throws Exception
    {
        var first = new LockByKey<String>();
        var second = new LockByKey<String>();
        first.lock("a");
        first.lock("a");
        var executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon(true).factory());
        try
        {
            executor.submit(() -> {
                second.lockInterruptibly("a");
                try { assertThat(second.activeLockCount()).isEqualTo(1); }
                finally { second.unlock("a"); }
                first.lockInterruptibly("b");
                try { assertThat(first.activeLockCount()).isEqualTo(2); }
                finally { first.unlock("b"); }
                assertThatThrownBy(() -> first.unlock("a")).isInstanceOf(IllegalMonitorStateException.class);
                return null;
            }).get(5, TimeUnit.SECONDS);
        }
        finally
        {
            // Освобождаем ключ до ожидания потоков, в том числе при регрессии изоляции экземпляров.
            first.unlock("a");
            first.unlock("a");
            stop(executor);
        }
        assertThat(first.activeLockCount()).isZero();
        assertThatThrownBy(() -> first.unlock("a")).isInstanceOf(IllegalMonitorStateException.class);
    }

    @Test
    void interruptionRemovesWaiterWithoutRemovingHeldLock() throws Exception
    {
        var locks = new LockByKey<String>();
        var executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon(true).factory());
        var started = new CountDownLatch(1);
        var interrupted = new CountDownLatch(1);
        var waitingThread = new AtomicReference<Thread>();
        locks.lock("a");
        try
        {
            Future<?> waiter = executor.submit(() -> {
                waitingThread.set(Thread.currentThread());
                started.countDown();
                try
                {
                    locks.lockInterruptibly("a");
                    try { throw new AssertionError("Waiter acquired a held lock"); }
                    finally { locks.unlock("a"); }
                }
                catch (InterruptedException e) { interrupted.countDown(); }
            });
            assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
            awaitLockWait(waitingThread.get());
            waiter.cancel(true);
            assertThat(interrupted.await(2, TimeUnit.SECONDS)).isTrue();
            assertThat(locks.activeLockCount()).isEqualTo(1);
        }
        finally
        {
            locks.unlock("a");
            stop(executor);
        }
        assertThat(locks.activeLockCount()).isZero();
    }

    @Test
    void cancellationRacingWithReleaseLeavesNoUnusedEntry() throws Exception
    {
        var locks = new LockByKey<String>();
        var executor = Executors.newFixedThreadPool(2, Thread.ofPlatform().daemon(true).factory());
        boolean held = false;
        try
        {
            for (int attempt = 0; attempt < 100; attempt++)
            {
                var started = new CountDownLatch(1);
                var race = new CountDownLatch(1);
                var waitingThread = new AtomicReference<Thread>();
                locks.lock("a");
                held = true;
                Future<?> waiter = executor.submit(() -> {
                    waitingThread.set(Thread.currentThread());
                    started.countDown();
                    try
                    {
                        locks.lockInterruptibly("a");
                        // Захват до прерывания тоже является допустимым исходом гонки.
                        try { assertThat(locks.activeLockCount()).isEqualTo(1); }
                        finally { locks.unlock("a"); }
                    }
                    catch (InterruptedException e) { /* При отмене unlock не требуется. */ }
                });
                assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
                awaitLockWait(waitingThread.get());
                Future<?> cancellation = executor.submit(() -> {
                    race.await();
                    waitingThread.get().interrupt();
                    return null;
                });
                race.countDown();
                locks.unlock("a");
                held = false;
                // Не отменяем Future: get должен подтвердить завершение тела задачи.
                cancellation.get(2, TimeUnit.SECONDS);
                waiter.get(2, TimeUnit.SECONDS);
                assertThat(locks.activeLockCount()).isZero();
            }
        }
        finally
        {
            if (held) locks.unlock("a");
            stop(executor);
        }
    }

    @Test
    void preInterruptedThreadLeavesNoUnusedEntryAndNullKeysAreRejected() throws Exception
    {
        var locks = new LockByKey<String>();
        Thread.currentThread().interrupt();
        try
        {
            assertThatThrownBy(() -> locks.lockInterruptibly("a")).isInstanceOf(InterruptedException.class);
        }
        finally { Thread.interrupted(); }
        assertThat(locks.activeLockCount()).isZero();
        locks.lock("a");
        locks.lock("a");
        locks.unlock("a");
        assertThat(locks.activeLockCount()).isEqualTo(1);
        locks.unlock("a");
        assertThatThrownBy(() -> locks.lock(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> locks.unlock(null)).isInstanceOf(NullPointerException.class);
    }

    private static void awaitLockWait(Thread thread)
    {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        // До этого ожидания задача не вызывает других блокирующих операций.
        while (System.nanoTime() < deadline)
        {
            if (thread.getState() == Thread.State.WAITING) return;
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
        }
        throw new AssertionError("Thread did not start waiting for the lock");
    }

    private static void stop(ExecutorService executor) throws InterruptedException
    {
        executor.shutdownNow();
        assertThat(executor.awaitTermination(2, TimeUnit.SECONDS)).isTrue();
    }
}
