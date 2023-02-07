package com.luoyuer.framework.converter;

import net.mamoe.mirai.message.data.Message;

public interface MessageConvert{
    Message convert(Object message);
}
