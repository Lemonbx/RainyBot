package com.luoyuer.framework.extra.util;

import cn.hutool.core.thread.threadlocal.NamedThreadLocal;
import cn.hutool.core.util.StrUtil;
import com.luoyuer.framework.Holder;
import com.luoyuer.framework.entity.WaitInfo;
import net.mamoe.mirai.contact.Group;

import java.time.Duration;

public class WaitUtil {
    public static String waitNextMessage(Duration duration) {
        WaitInfo info = new WaitInfo();
        System.out.println(duration.toMillis());
        info.setDelayTime(System.currentTimeMillis() + duration.toMillis());
        Thread currentThread = Thread.currentThread();
        info.setThread(currentThread);
        Holder.waitInfoMap.put(getWaitName(), info);
        currentThread.suspend();
        String s = info.getReturnMessage();
        if (StrUtil.isBlank(s)) {
            throw new RuntimeException("等待超时");
        }
        return s;
    }

    public static String getWaitName() {
        Integer type = Holder.messageType.get();
        if (type == 1) {
            return type + "_" + Holder.friend.get().getId();
        } else {
            return type + "_" + Holder.group.get().getId() + "_" + Holder.user.get().getId();
        }
    }
}
