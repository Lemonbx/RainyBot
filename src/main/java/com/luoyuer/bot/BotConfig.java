package com.luoyuer.bot;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.threadlocal.NamedThreadLocal;
import com.luoyuer.framework.ActionInvoke;
import com.luoyuer.framework.Holder;
import com.luoyuer.framework.converter.MessageConverter;
import com.luoyuer.framework.anno.Bean;
import com.luoyuer.framework.anno.Inject;
import com.luoyuer.framework.entity.WaitInfo;
import com.luoyuer.framework.extra.util.WaitUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.BotConfiguration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Bean
public class BotConfig {
    @Inject
    ActionInvoke invoke;
    @Inject
    Bot bot;

    @Bean
    public Bot bot() {
        Bot ins1 = BotFactory.INSTANCE.newBot(Convert.toLong(Holder.properties.getProperty("qq.acc")), Holder.properties.getProperty("qq.pwd"), new BotConfiguration() {{
            fileBasedDeviceInfo();
            setProtocol(MiraiProtocol.ANDROID_PAD);
        }});
        ins1.login();
        return ins1;
    }

    @PostConstruct
    public void init() {
        bot.getEventChannel().subscribeAlways(net.mamoe.mirai.event.events.UserMessageEvent.class, (event) -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println(Holder.messageType.get());
            Friend friend = event.getBot().getFriend(event.getSender().getId());
            Holder.messageType.set(1);
            Holder.friend.set(friend);
            String waitName = WaitUtil.getWaitName();
            WaitInfo waitInfo = Holder.waitInfoMap.get(waitName);
            boolean waitSuccess = false;
            if (waitInfo != null) {
                if (System.currentTimeMillis() < waitInfo.getDelayTime()) {
                    waitSuccess = true;
                    waitInfo.setReturnMessage(event.getMessage().contentToString());
                }
                try {
                    waitInfo.getThread().resume();
                } finally {
                    Holder.waitInfoMap.remove(waitName);
                }
            }
            if (!waitSuccess) {
                Object invoke1 = invoke.invoke(event.getMessage().contentToString());
                if (invoke1 != null) {
                    Message convert = MessageConverter.convert(invoke1);
                    if (convert != null)
                        friend.sendMessage(convert);
                }
            }
            Holder.messageType.remove();
            Holder.friend.remove();
        });
        bot.getEventChannel().subscribeAlways(net.mamoe.mirai.event.events.GroupMessageEvent.class, (event) -> {
            Group group = event.getBot().getGroup(event.getGroup().getId());
            Friend friend1 = event.getBot().getFriend(event.getSender().getId());
            Holder.messageType.set(2);
            Holder.friend.set(friend1);
            Holder.group.set(group);

            String waitName = WaitUtil.getWaitName();
            WaitInfo waitInfo = Holder.waitInfoMap.get(waitName);
            boolean waitSuccess = false;
            if (waitInfo != null) {
                if (System.currentTimeMillis() < waitInfo.getDelayTime()) {
                    waitSuccess = true;
                    waitInfo.setReturnMessage(event.getMessage().contentToString());
                }
                try {
                    waitInfo.getThread().resume();
                } finally {
                    Holder.waitInfoMap.remove(waitName);
                }
            }
            if (!waitSuccess) {
                Object invoke1 = invoke.invoke(event.getMessage().contentToString());
                if (invoke1 != null) {
                    Message convert = MessageConverter.convert(invoke1);
                    if (convert != null)
                        event.getGroup().sendMessage(convert);
                }
            }
            Holder.messageType.remove();
            Holder.friend.remove();
            Holder.group.remove();
        });

        bot.getEventChannel().subscribeAlways(net.mamoe.mirai.event.events.GroupMessageEvent.class, (event) -> {
            DateTime now = DateUtil.date();
            int hour = now.getField(DateField.HOUR_OF_DAY);
            String message = event.getMessage().contentToString();
            List<String> morning = Arrays.asList("早", "早上好", "早好");
            List<String> noon = Arrays.asList("午", "午安", "午好");
            List<String> afternoon = Collections.singletonList("下午好");
            List<String> night = Arrays.asList("晚", "晚安", "晚好");
            if (morning.contains(message)) {
                if (hour >= 5 && hour < 12) {
                    event.getGroup().sendMessage("早哦");
                } else {
                    event.getGroup().sendMessage("不早啦");
                }
            } else if (noon.contains(message)) {
                if (hour >= 10 && hour < 14) {
                    event.getGroup().sendMessage("午好哦");
                } else {
                    event.getGroup().sendMessage("emmmm好像不是中午了吧");
                }
            } else if (afternoon.contains(message)) {
                if (hour >= 12 && hour < 18) {
                    event.getGroup().sendMessage("下午好");
                } else {
                    event.getGroup().sendMessage("诶嘿");
                }
            } else if (night.contains(message)) {
                if (hour >= 16 || hour < 8) {
                    event.getGroup().sendMessage("晚好");
                } else {
                    event.getGroup().sendMessage("你什么时候去的美国哦");
                }
            }
        });
    }
}
