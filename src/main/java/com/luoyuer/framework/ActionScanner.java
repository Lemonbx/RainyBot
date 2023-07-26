package com.luoyuer.framework;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.luoyuer.framework.anno.Action;
import com.luoyuer.framework.anno.Bean;
import com.luoyuer.framework.anno.Inject;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionScanner {
    private static List<LastInit> lastInit = new ArrayList<>();

    public static void scan() {
        Reflections refs = new Reflections();
        Set<Class<?>> beans = refs.getTypesAnnotatedWith(Bean.class);
        for (Class<?> bean : beans) {
//            System.out.println(bean.getName());
            Method[] methods = bean.getMethods();
            Action annotation = bean.getAnnotation(Action.class);
            String classAct = annotation == null ? "" : annotation.value();
            Object o = null;
            try {
                Holder.cacheClass.put(bean.getName(), bean);
                Holder.classIns.put(bean.getName(), o = bean.newInstance());
                String value = bean.getAnnotation(Bean.class).value();
                if (StrUtil.isBlank(value)) {
                    String className = bean.getSimpleName();
                    String firstLetter = className.substring(0, 1).toLowerCase();
                    String otherLetter = className.substring(1);
                    value = firstLetter + otherLetter;
                }
                Holder.nameToClass.put(value, bean.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (Method method : methods) {
                Action action = method.getAnnotation(Action.class);
                if (action != null) {
                    String act = action.value();
                    act = Holder.matcher.combine(classAct, act);
                    Holder.actionMap.put(act, method);
                }
                Bean annotation1 = method.getAnnotation(Bean.class);
                if (annotation1 != null) {
//                    Object methodBean = null;
//                    try {
//                        Parameter[] parameters = method.getParameters();
//                        List<Object> parmsVal = new ArrayList<>();
//                        boolean success = true;
//                        for (Parameter parameter : parameters) {
//                            Object o1 = getBeanByField(parameter);
//                            if (o1 == null) {
//                                success = false;
//                                break;
//                            }
//                            parmsVal.add(Convert.convert(parameter.getType(), o1));
//                        }
//                        if (!success) {
                    addToLastInit(new LastInit(o, method));
//                            continue;
//                        }
//                        methodBean = method.invoke(o, parmsVal.toArray());
//                    } catch (IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    } catch (InvocationTargetException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Holder.classIns.put(methodBean.getClass().getName(), methodBean);
//                    Holder.cacheClass.put(methodBean.getClass().getName(), methodBean.getClass());
//                    String value = annotation1.value();
//                    if (StrUtil.isBlank(value)) {
//                        value = method.getName();
//                    }
//                    Holder.nameToClass.put(value, methodBean.getClass().getName());
                }
            }
        }
        //实例化lastInit
        lastInit.forEach(init -> {
            Method method = init.method;
            Object methodBean = null;
            Bean annotation1 = method.getAnnotation(Bean.class);
            try {
                Parameter[] parameters = method.getParameters();
                List<Object> parmsVal = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    Object o1 = getBeanByField(parameter);
                    parmsVal.add(Convert.convert(parameter.getType(), o1));

                }
                methodBean = method.invoke(init.o, parmsVal.toArray());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            Holder.classIns.put(methodBean.getClass().getName(), methodBean);
            Holder.cacheClass.put(methodBean.getClass().getName(), methodBean.getClass());
            String value = annotation1.value();
            if (StrUtil.isBlank(value)) {
                value = method.getName();
            }
            Holder.nameToClass.put(value, methodBean.getClass().getName());
        });
        lastInit.clear();
        Holder.classIns.forEach((k, v) -> {
            Class clazz = Holder.cacheClass.get(k);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Inject annotation = field.getAnnotation(Inject.class);
                if (annotation != null) {
                    Object o1 = getBeanByField(field);
                    if (o1 != null) {
                        try {
                            field.setAccessible(true);
                            field.set(v, o1);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                PostConstruct annotation = method.getAnnotation(PostConstruct.class);
                if (annotation != null) {
                    try {
                        method.invoke(v);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private static class LastInit {
        Object o;
        Method method;

        public LastInit(Object o, Method method) {
            this.o = o;
            this.method = method;
        }
    }

    private static void addToLastInit(LastInit o) {
        //检查是否循环依赖
        Parameter[] parameters = o.method.getParameters();
        if (Arrays.stream(parameters).anyMatch(a -> a.getClass().getName().equals(o.o.getClass().getName()))) {
            throw new RuntimeException("形参不允许包含该类");
        }
        //获取未实例化完成的列表
        List<Parameter> unInit = Arrays.stream(parameters).filter(a -> {
            Object beanByField = getBeanByField(a);
            return beanByField == null;
        }).collect(Collectors.toList());
        //检查未完成的是否包含在未完成列表中
        List<LastInit> collect = lastInit.stream().filter(a -> unInit.stream().anyMatch(b -> b.getClass().getName().equals(a.o.getClass().getName()))).collect(Collectors.toList());
        //添加到collect[0]之前
        if (collect.size() == 0) {
            lastInit.add(o);
        } else {
            if (collect.stream().anyMatch(b -> Arrays.stream(b.method.getParameters()).anyMatch(a -> a.getClass().getName().equals(o.method.getReturnType().getName())))) {
                throw new RuntimeException("循环依赖");
            }
            int index = lastInit.indexOf(collect.get(0));
            lastInit.add(index, o);
        }
    }

    public static Object getBeanByField(Parameter parameter) {
        Inject annotation = parameter.getAnnotation(Inject.class);
        Object o1 = null;
        if (annotation != null) {
            String value = annotation.value();
            if (StrUtil.isNotBlank(value)) {
                //根据名称注入
                o1 = Holder.classIns.get(Holder.nameToClass.get(value));
            }
        }
        if (o1 == null) {
            //根据类型注入
            o1 = Holder.classIns.get(parameter.getType().getName());
        }
        if (o1 == null) {
            //根据变量名注入
            o1 = Holder.classIns.get(Holder.nameToClass.get(parameter.getName()));
        }
        return o1;
    }

    public static Object getBeanByField(Field field) {
        Inject annotation = field.getAnnotation(Inject.class);
        Object o1 = null;
        if (annotation != null) {
            String value = annotation.value();
            if (StrUtil.isNotBlank(value)) {
                //根据名称注入
                o1 = Holder.classIns.get(Holder.nameToClass.get(value));
            }
        }
        if (o1 == null) {
            //根据类型注入
            o1 = Holder.classIns.get(field.getType().getName());
        }
        if (o1 == null) {
            //根据变量名注入
            o1 = Holder.classIns.get(Holder.nameToClass.get(field.getName()));
        }
        return o1;
    }
}

