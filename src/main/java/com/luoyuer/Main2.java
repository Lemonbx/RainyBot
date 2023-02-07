package com.luoyuer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.AntPathMatcher;
import com.luoyuer.action.DefaultAction;
import com.luoyuer.framework.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main2 {
    public static void main(String[] args) {
        ContextLoader.load();
        System.out.println("启动成功");
    }
}
