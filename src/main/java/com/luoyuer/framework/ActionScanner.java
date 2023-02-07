package com.luoyuer.framework;

import cn.hutool.core.util.StrUtil;
import com.luoyuer.framework.anno.Action;
import com.luoyuer.framework.anno.Bean;
import com.luoyuer.framework.anno.Inject;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class ActionScanner {
    public static void scan() {
        Reflections refs = new Reflections();
        Set<Class<?>> beans = refs.getTypesAnnotatedWith(Bean.class);
        for (Class<?> bean : beans) {
//            System.out.println(bean.getName());
            Method[] methods = bean.getMethods();
            Action annotation = bean.getAnnotation(Action.class);
            String classAct = annotation==null?"":annotation.value();
            Object o = null;
            try {
                Holder.cacheClass.put(bean.getName(),bean);
                Holder.classIns.put(bean.getName(),o = bean.newInstance());
                String value = bean.getAnnotation(Bean.class).value();
                if (StrUtil.isBlank(value)){
                    String className = bean.getSimpleName();
                    String firstLetter = className.substring(0, 1).toLowerCase();
                    String otherLetter = className.substring(1);
                    value = firstLetter + otherLetter;
                }
                Holder.nameToClass.put(value,bean.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (Method method : methods) {
                Action action = method.getAnnotation(Action.class);
                if(action!=null){
                    String act = action.value();
                    act = Holder.matcher.combine(classAct,act);
                    Holder.actionMap.put(act,method);
                }
                Bean annotation1 = method.getAnnotation(Bean.class);
                if (annotation1!=null){
                    Object methodBean = null;
                    try {
                        methodBean = method.invoke(o);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    Holder.classIns.put(methodBean.getClass().getName(),methodBean);
                    Holder.cacheClass.put(methodBean.getClass().getName(),methodBean.getClass());
                    String value = annotation1.value();
                    if (StrUtil.isBlank(value)){
                        value = method.getName();
                    }
                    Holder.nameToClass.put(value,methodBean.getClass().getName());
                }
            }
        }
        Holder.classIns.forEach((k,v)->{
            Class clazz = Holder.cacheClass.get(k);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Inject annotation = field.getAnnotation(Inject.class);
                if (annotation!=null){
                    String value = annotation.value();
                    Object o1 = null;
                    if (StrUtil.isNotBlank(value)){
                        //根据名称注入
                        o1 = Holder.classIns.get(Holder.nameToClass.get(value));
                    }
                    if (o1==null){
                        //根据类型注入
                        o1 = Holder.classIns.get(field.getType().getName());
                    }
                    if (o1==null){
                        //根据变量名注入
                        o1 = Holder.classIns.get(Holder.nameToClass.get(field.getName()));
                    }
                    if (o1!=null){
                        try {
                            field.setAccessible(true);
                            field.set(v,o1);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                PostConstruct annotation = method.getAnnotation(PostConstruct.class);
                if (annotation!=null){
                    try {
                        method.invoke(v);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
