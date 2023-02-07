package com.luoyuer.framework.converter;

import cn.hutool.core.img.ImgUtil;
import com.luoyuer.framework.Holder;
import com.luoyuer.framework.anno.Bean;
import com.luoyuer.framework.extra.Aud;
import com.luoyuer.framework.extra.Img;
import com.luoyuer.framework.extra.util.FileUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Bean
public class MessageConverter {

    public static Message convert(Object message) {
        Class<?> aClass = message.getClass();
        MessageConvert messageConvert = Holder.messageConfigMap.get(aClass);
        if (messageConvert == null){
            for (Class clz :Holder.messageConfigMap.keySet()){
                if (clz.isAssignableFrom(aClass)){
                    messageConvert = Holder.messageConfigMap.get(clz);
                    break;
                }
            }
            if (messageConvert==null){
                messageConvert = (msg)-> null;
            }
            register(aClass,messageConvert);
        }
        if (messageConvert != null) {
            return messageConvert.convert(message);
        }
        return null;
    }

    public static void register(Class<?> aClass, MessageConvert messageConvert) {
        Holder.messageConfigMap.put(aClass, messageConvert);
    }

    @PostConstruct
    public void init() {
        MessageConverter.register(String.class, (message) -> new PlainText((String) message));
        MessageConverter.register(List.class, message -> {
            List<Message> result = new ArrayList<>();
            List list = (List) message;
            for (Object o : list) {
                result.add(MessageConverter.convert(o));
            }
            return MessageUtils.newChain(result.toArray(new Message[0]));
        });
        MessageConverter.register(Message.class, message -> (Message) message);
        MessageConverter.register(Img.class, message -> {
            Img image = (Img) message;
            return FileUtil.uploadImage(image.getBytes());
        });
        MessageConverter.register(Aud.class, message -> {
            Aud aud = (Aud) message;
            return FileUtil.uploadAudio(aud.getBytes());
        });
    }

}
