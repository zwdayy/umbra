package com.umbra.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhangweiding on 15/9/21.
 */
public class FieldUtil {



    private FieldUtil() {
    }

    public static Field getField(Object one, String name, Class<?>... c)
            throws Exception {
        Field f = null;
        if (null == c || 0 == c.length) {
            f = one.getClass().getDeclaredField(name);
        } else {
            f = c[0].getDeclaredField(name);
        }
        f.setAccessible(true);
        return f;
    }

    public static Object get(Object one, String name, Class<?>... c) {
        try {
            Field f = getField(one, name, c);
            return f.get(one);
        } catch (Exception e) {
        }
        return null;
    }

    public static Method getMethod(String name, Class<?> which, Class<?>... c) {
        try {
            Method m = which.getMethod(name, c);
            m.setAccessible(true);
            return m;
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }



    public static void set(Object one, String name, Object value, Class<?>... c) {
        try {
            Field f = getField(one, name, c);
            if (null != f) {
                f.set(one, value);
            }
        } catch (Exception e) {
        }
    }
}
