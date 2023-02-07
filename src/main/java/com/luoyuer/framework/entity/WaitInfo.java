package com.luoyuer.framework.entity;

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
