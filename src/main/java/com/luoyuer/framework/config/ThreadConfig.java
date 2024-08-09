package com.luoyuer.framework.config;

import com.luoyuer.framework.Holder;
import com.luoyuer.framework.anno.Bean;
import com.luoyuer.framework.anno.Inject;
import com.luoyuer.framework.entity.WaitInfo;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Bean
public class ThreadConfig {
    @Inject
    ThreadPoolExecutor executor;

    @Bean
    public ThreadPoolExecutor threadPoolTaskExecutor() {
        return new ThreadPoolExecutor(10, 20, 60, java.util.concurrent.TimeUnit.SECONDS, new java.util.concurrent.ArrayBlockingQueue<Runnable>(100));
    }

//    @PostConstruct
//    public void initTask() {
//        executor.execute(() -> {
//            while (true) {
//                ConcurrentHashMap.KeySetView<String, WaitInfo> keySet = Holder.waitInfoMap.keySet();
//                for (String s : keySet) {
//                    WaitInfo waitInfo = Holder.waitInfoMap.get(s);
//                    if (waitInfo == null) {
//                        continue;
//                    }
//                    if (waitInfo.getDelayTime() < System.currentTimeMillis()) {
//                        waitInfo.getThread().resume();
//                        Holder.waitInfoMap.remove(s);
//                    }
//                }
//                try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
}
