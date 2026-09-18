package org.vlaskin.bot.utils.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Objects;

/**
 * Последовательный доступ по ключу внутри одного процесса и экземпляра.
 * Ключи не могут быть null и должны сохранять equals/hashCode до освобождения.
 * Блокировки реентерабельны; порядок захвата разных ключей определяет приложение.
 */
public final class LockByKey<K>
{
    private static final class Entry
    {
        private final ReentrantLock lock = new ReentrantLock();
        private int users = 1;
    }

    private final ConcurrentHashMap<K, Entry> locks = new ConcurrentHashMap<>();

    /** Захватывает блокировку; каждый успешный вызов требует unlock в finally. */
    public void lock(K key)
    {
        retain(key).lock.lock();
    }

    /** Захватывает блокировку с отменой; при прерывании освобождать её не требуется. */
    public void lockInterruptibly(K key) throws InterruptedException
    {
        Entry entry = retain(key);
        try
        {
            entry.lock.lockInterruptibly();
        }
        catch (InterruptedException e)
        {
            locks.compute(key, (k, current) -> --current.users == 0 ? null : current);
            throw e;
        }
    }

    private Entry retain(K key)
    {
        Objects.requireNonNull(key, "Lock key must not be null");
        return locks.compute(key, (k, current) -> {
            if (current == null)
                return new Entry();
            current.users++;
            return current;
        });
    }

    /** Освобождает блокировку текущего потока и удаляет неиспользуемый ключ. */
    public void unlock(K key)
    {
        Objects.requireNonNull(key, "Lock key must not be null");
        locks.compute(key, (k, entry) -> {
            if (entry == null)
                throw new IllegalMonitorStateException("No lock found for key: " + key);
            entry.lock.unlock();
            return --entry.users == 0 ? null : entry;
        });
    }

    int activeLockCount()
    {
        return locks.size();
    }
}
