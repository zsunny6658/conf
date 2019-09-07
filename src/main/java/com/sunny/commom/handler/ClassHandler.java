package com.sunny.commom.handler;

import com.sunny.commom.annotation.ConfClass;
import com.sunny.commom.annotation.ConfClassIgnore;
import com.sunny.commom.annotation.ConfPath;
import com.sunny.commom.annotation.Dynamic;
import com.sunny.commom.utils.PackageUtils;
import com.sunny.source.bean.LoadFileName;

import java.lang.reflect.Field;
import java.util.*;

/**
 * create by zsunny
 * data: 2019/8/10
 **/
public class ClassHandler {

    private Set<Class<?>> classSet;
    // dynamic update sets
    private Set<Class<?>> dynamicClassSet = new HashSet<>();
    private Set<Field> dynamicFieldSet = new HashSet<>();
    private Map<Class<?>, LoadFileName> fixedClassMap = new HashMap<>();

    private static ClassHandler classHandler;

    private ClassHandler() {
    }

    public static ClassHandler getClassHandler() {
        // singleton
        if (Objects.isNull(classHandler)) {
            synchronized (ClassHandler.class) {
                if (Objects.isNull(classHandler)) {
                    classHandler = new ClassHandler();
                    classHandler.classSet = PackageUtils.getAllClassSet();
                    classHandler.getDynamics();
                }
            }
        }
        return classHandler;
    }

    private void getDynamics() {
        classSet.forEach(clazz -> {
            if (clazz.isAnnotationPresent(Dynamic.class)) {
                dynamicClassSet.add(clazz);
            } else {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    // not support for systemconf at present. look forward to future version
                    if ((field.isAnnotationPresent(ConfPath.class) || (clazz.isAnnotationPresent(ConfClass.class)
                            && !field.isAnnotationPresent(ConfClassIgnore.class))) && (field.getModifiers() & 8) != 0
                            && field.isAnnotationPresent(Dynamic.class)) {
                        dynamicFieldSet.add(field);
                    }
                }
            }
        });
    }

    public Set<Class<?>> getClassSet() {
        return classSet;
    }

    public Set<Class<?>> getDynamicClassSet() {
        return dynamicClassSet;
    }

    public Set<Field> getDynamicFieldSet() {
        return dynamicFieldSet;
    }

    public Map<Class<?>, LoadFileName> getFixedClassMap() {
        return fixedClassMap;
    }
}
