package com.sunny.processor.impl;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import com.sunny.commom.annotation.*;


public class ConfClassProcessor extends AbstractConfProcessor {

    private ConfClassProcessor() {}

    private static class ConfClassProcessorInner {
        private static ConfClassProcessor confClassProcessor = new ConfClassProcessor();
    }

    public static ConfClassProcessor getProcessor() {
        return ConfClassProcessorInner.confClassProcessor;
    }

    public void update() {
        dynamicClassSet.forEach(clazz -> putInConf(oo, clazz));
    }

    @Override
    public void process() {
        classSet.forEach(clazz -> putInConf(oo, clazz));
    }

    public void putInConf(Object oo, Class<?> clazz) {
        String prefix = "";
        if (!clazz.isAnnotationPresent(ConfClass.class)) {
            return;
        }
        if (clazz.isAnnotationPresent(ConfClassPrefix.class)) {
            ConfClassPrefix confClassPrefix = clazz.getAnnotation(ConfClassPrefix.class);
            prefix = confClassPrefix.value();
        }
        if (prefix.endsWith(".")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // ignored or not static
            if (field.isAnnotationPresent(ConfClassIgnore.class) || (field.getModifiers() & 8) == 0) {
                continue;
            }
            // about default value
            boolean isDefault = false;
            if (field.isAnnotationPresent(ConfClassDefault.class)) {
                ConfClassDefault confClassDefault = field.getAnnotation(ConfClassDefault.class);
                String v = confClassDefault.value();
                try {
                    field.setAccessible(true);
                    field.set(field, v);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                isDefault = true;
            }
            // about alias
            String alias = null;
            if (field.isAnnotationPresent(ConfClassAlias.class)) {
                ConfClassAlias confClassAlias = field.getAnnotation(ConfClassAlias.class);
                alias = confClassAlias.value();
            }
            // produce the prefix
            String tmpPrefix;
            if (null == alias)
                tmpPrefix = prefix + "." + field.getName();
            else
                tmpPrefix = prefix + "." + alias;
            String[] props = tmpPrefix.split("\\.");

            putInConfCore(oo, props, field, isDefault);
        }
    }

    public void putInConfCore(Object o, String[] props, Field field, boolean isDefault) {
        // speed up
        if (isDefault)
            return;

        int ind = 0;
        while (true) {
            if (ind < props.length && Objects.nonNull(o) && o instanceof Map) {
                o = ((Map<?, ?>) o).get(props[ind]);
            } else {
                break;
            }
            ind++;
        }

        if (Objects.isNull(o)) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(field, String.valueOf(o));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
