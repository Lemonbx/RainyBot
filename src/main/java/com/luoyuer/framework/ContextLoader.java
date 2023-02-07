package com.luoyuer.framework;

import java.util.List;

public class ContextLoader {
    public static void load() {
        printBanner();
        loadProperties();
        ActionScanner.scan();
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
