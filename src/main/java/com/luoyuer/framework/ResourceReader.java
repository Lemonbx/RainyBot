package com.luoyuer.framework;

import cn.hutool.core.io.IoUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ResourceReader {
    public static List<String> read(String path) {
        try(InputStream resourceAsStream = ResourceReader.class.getClassLoader().getResourceAsStream(path)) {
            List<String> result = new ArrayList<>();
            IoUtil.readLines(resourceAsStream, Charset.defaultCharset(),result);
            return result;
        } catch (Exception e) {
            System.err.println("Warning: " + path + " not found");
            return new ArrayList<>();
        }
    }
}
