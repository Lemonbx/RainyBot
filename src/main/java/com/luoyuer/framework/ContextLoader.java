package com.luoyuer.framework;

import net.mamoe.mirai.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ContextLoader {
    protected static Bot bot = null;
    protected static Consumer<Bot> afterLoginAction = null;
    private static List<String> scansPackage = new ArrayList(){{
        add("com.luoyuer");
    }};
    public static void addPackage(String pack){
        if (!scansPackage.contains(pack)){
            scansPackage.add(pack);
        }
    }
    public static void setBot(Bot bot){
        if(bot != null){
            ContextLoader.bot = bot;
        }
    }
    public static Bot getBot(){
        return bot;
    }
    public static void afterBotLogin(Consumer<Bot> action){
        afterLoginAction = action;
    }
    public static void doAfter(Bot bot){
        if(afterLoginAction != null)
            afterLoginAction.accept(bot);
    }
    public static void load(Class clazz) {
        String basePackage = clazz.getPackage().getName();
        addPackage(basePackage);
        printBanner();
        loadProperties();
        ActionScanner.scan(scansPackage);
    }

    private static void loadProperties() {
        List<String> properties = ResourceReader.read("application.properties");
        properties.forEach(it -> {
            String[] split = it.split("=");
            if (split.length != 2) return;
            Holder.properties.put(split[0].trim(), split[1].trim());
        });
    }

    private static void printBanner() {
        List<String> bannerLst = ResourceReader.read("banner.txt");
        bannerLst.forEach(System.out::println);
    }

}
