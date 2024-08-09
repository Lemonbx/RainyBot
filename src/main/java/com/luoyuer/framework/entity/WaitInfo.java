package com.luoyuer.framework.entity;

import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitInfo {
    /**
     * 超时时间
     */
    private Long delayTime;
    /**
     * 等待线程
     */
    private Thread thread;

    private String returnMessage;
    private Lock lock;
    private Condition condition;

    public boolean conditionWait() throws InterruptedException {
        if (lock == null) {
            lock = new ReentrantLock();
            condition = lock.newCondition();
            lock.lock();
            try {
                return condition.awaitUntil(new Date(delayTime));
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public void conditionSignal() {
        if (lock == null) return;
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
