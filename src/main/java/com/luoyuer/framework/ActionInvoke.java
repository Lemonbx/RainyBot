package com.luoyuer.framework;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.luoyuer.framework.anno.Bean;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Bean
public class ActionInvoke {
    public Object invoke(String action){
        Set<String> strings = Holder.actionMap.keySet();
        String act = "";
        for (String string : strings) {
            boolean match = Holder.matcher.match(string, action);
            if (!match){
                continue;
            }
            act = string;
        }
        if (StrUtil.isNotBlank(act)){
            Map<String, String> stringStringMap = Holder.matcher.extractUriTemplateVariables(act, action);
            Method method = Holder.actionMap.get(act);
            Object o = Holder.classIns.get(method.getDeclaringClass().getName());
            Parameter[] parameters = method.getParameters();
            List<Object> parmsVal = new ArrayList<>();
            for (Parameter parameter : parameters) {
                String name = parameter.getName();
                parmsVal.add(Convert.convert(parameter.getType(),stringStringMap.get(name)));
            }
            try {
                return method.invoke(o,parmsVal.toArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }
}
