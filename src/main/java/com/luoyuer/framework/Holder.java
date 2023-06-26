package com.luoyuer.framework;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.thread.threadlocal.NamedThreadLocal;
import com.luoyuer.framework.converter.MessageConvert;
import com.luoyuer.framework.entity.WaitInfo;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Holder {
    protected static AntPathMatcher matcher = new AntPathMatcher(" ");
    protected static Map<String, Method> actionMap = new HashMap<>();
    protected static Map<String, Object> classIns = new HashMap<>();
    protected static Map<String, Class> cacheClass = new HashMap<>();

    protected static Map<String, String> nameToClass = new HashMap<>();
    public static Properties properties = new Properties();

    public static Map<Class, MessageConvert> messageConfigMap = new HashMap<>();


    //消息存储

    public static final ThreadLocal<Integer> messageType = new NamedThreadLocal<>("messageType");
    public static final ThreadLocal<User> user = new NamedThreadLocal<>("member");
    public static final ThreadLocal<Friend> friend = new NamedThreadLocal<>("friend");
    public static final ThreadLocal<Group> group = new NamedThreadLocal<>("group");

    /**
     * 这个超时处理用DelayQueue比较好，但是我懒得写了，在一个大循环里跑的
     */
    public static ConcurrentHashMap<String, WaitInfo> waitInfoMap = new ConcurrentHashMap<>();
}
