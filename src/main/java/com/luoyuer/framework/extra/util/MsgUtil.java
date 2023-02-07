package com.luoyuer.framework.extra.util;

import com.luoyuer.framework.Holder;
import com.luoyuer.framework.converter.MessageConverter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.ExternalResource;

public class MsgUtil {
    public static void sendToSource(Object obj) {
        Message msg = MessageConverter.convert(obj);
        Integer messageType = Holder.messageType.get();
        if (messageType == null) {
            return;
        }
        if (messageType == 1) {
            Contact friend = Holder.friend.get();
            friend.sendMessage(msg);
        } else {
            Contact group = Holder.group.get();
            group.sendMessage(msg);
        }
    }
}
