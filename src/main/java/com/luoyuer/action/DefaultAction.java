package com.luoyuer.action;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.luoyuer.framework.anno.Action;
import com.luoyuer.framework.anno.Bean;
import com.luoyuer.framework.anno.Inject;
import com.luoyuer.framework.extra.Aud;
import com.luoyuer.framework.extra.Img;
import com.luoyuer.framework.extra.util.MsgUtil;
import com.luoyuer.framework.extra.util.WaitUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Bean
public class DefaultAction {
    @Action("你好")
    public String hello() {
        return "你好";
    }

    @Action("随机图片")
    public Img randomImage() throws IOException {
        byte[] bytes = HttpUtil.downloadBytes("https://spider.mryt.vip/prod-api/outer/pic/random");//这个api是自用测试的，可能返回乳化图片
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            BufferedImage img = ImgUtil.read(inputStream);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ImgUtil.writeJpg(img, outputStream);
                bytes = outputStream.toByteArray();
            }
        }
        return new Img(bytes);
    }

    @Action("测试返回")
    public List testReturn() throws IOException {
        List lst = new ArrayList();
        lst.add(randomImage());
        lst.add("测试消息");
        lst.add(randomImage());
        return lst;
    }

    @Action("听{name}")
    public Object listen(String name) {
        HttpResponse execute = HttpUtil.createPost("https://www.qqwtt.com/")
                .form("filter", "name")
                .form("type", "netease")
                .form("page", 1)
                .form("input", name)
                .header("x-requested-with", "XMLHttpRequest")
                .execute();
        String body = execute.body();
        System.out.println(body);
        JSONObject entries = JSONUtil.parseObj(body);
        if (entries.getInt("code") == 200) {
            JSONArray list = entries.getJSONArray("data");
            JSONObject jsonObject = list.getJSONObject(0);
            String url = jsonObject.getStr("url");
            byte[] pics = HttpUtil.downloadBytes(jsonObject.getStr("pic"));
            Img img = new Img(pics);
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(jsonObject.getStr("title"));
            objects.add("\n");
            objects.add(jsonObject.getStr("author"));
            objects.add(img);
            objects.add(url);
            MsgUtil.sendToSource(objects);
            return url;
        } else {
            return entries.getStr("error");
        }
    }

    @Action("无返回值测试")
    public void noReturn() {
        MsgUtil.sendToSource("测试成功");
    }

    @Action("测试等待")
    public void testWait() throws InterruptedException {
        String s = WaitUtil.waitNextMessage(Duration.ofSeconds(10));
        System.out.println("获取到消息");
        MsgUtil.sendToSource(s);
    }
}
