package me.hazedev.hapi.nms;

import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {

    public static final String SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();
    private static final Map<String, Field> FIELD_CACHE = new HashMap<>();
    private static final Map<String, FieldAccessor<?>> FIELD_ACCESSOR_CACHE = new HashMap<>();
    private static final Map<String, Method> METHOD_CACHE = new HashMap<>();

    public static Class<?> getClass(String className) {
        Class<?> clazz = CLASS_CACHE.get(className);

        if (clazz != null) {
            return clazz;
        }

        try {
            clazz = Class.forName(className);
            CLASS_CACHE.put(className, clazz);
        }
        catch (Exception e) {
            Log.error(null, e, null);
        }
        return clazz;
    }

    public static Class<?> getNMSClass(String name) {
        return getClass("net.minecraft.server." + SERVER_VERSION + "." + name);
    }

    public static Class<?> getCraftBukkitClass(String name) {
        return getClass("org.bukkit.craftbukkit." + SERVER_VERSION + "." + name);
    }

    public static Object getHandle(Entity entity) {
        try {
            return getMethod(entity.getClass(), "getHandle").invoke(entity);
        }
        catch (Exception e) {
            Log.error(null, e, null);
            return null;
        }
    }

    public static Object getHandle(World world) {
        try {
            return getMethod(world.getClass(), "getHandle").invoke(world);
        }
        catch (Exception e) {
            Log.error(null, e, null);
            return null;
        }
    }

    private static String constructFieldCacheKey(Class<?> cl, String fieldName) {
        return cl.getName() + "." + fieldName;
    }

    public static Field getField(Class<?> cl, String fieldName) {
        String cacheKey = constructFieldCacheKey(cl, fieldName);

        Field field = FIELD_CACHE.get(cacheKey);

        if (field != null) {
            return field;
        }

        try {
            field = cl.getDeclaredField(fieldName);
            FIELD_CACHE.put(cacheKey, field);
        }
        catch (Exception e) {
            Log.error(null, e, null);
        }

        return field;
    }

    public static Field getPrivateField(Class<?> cl, String fieldName) {
        String cacheKey = constructFieldCacheKey(cl, fieldName);

        Field field = FIELD_CACHE.get(cacheKey);
        if (field != null) {
            return field;
        }

        try {
            field = cl.getDeclaredField(fieldName);
            field.setAccessible(true);
            FIELD_CACHE.put(cacheKey, field);
        }
        catch (Exception e) {
            Log.error(null, e, null);
        }

        return field;
    }

    public static Method getMethod(Class<?> cl, String methodName, Class<?>... args) {
        String cacheKey = cl.getName() + "." + methodName + "." + (args == null ? "NONE" : Arrays.toString(args));

        Method method = METHOD_CACHE.get(cacheKey);
        if (method != null) {
            return method;
        }

        for (Method classMethod : cl.getMethods()) {
            if (classMethod.getName().equals(methodName) && (args == null || classListEqual(args, classMethod.getParameterTypes()))) {
                method = classMethod;
                break;
            }
        }

        if (method != null) {
            METHOD_CACHE.put(cacheKey, method);
        }
        return method;
    }

    public static Method getMethod(Class<?> cl, String method) {
        return getMethod(cl, method, null);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... arguments) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), arguments)) {
                return constructor;
            }
        }

        return null;
    }

    public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
        if (l1.length != l2.length) {
            return false;
        }

        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i]) {
                return false;
            }
        }

        return true;
    }

    public interface ConstructorInvoker {
        Object invoke(Object... arguments);
    }

    public interface MethodInvoker {
        Object invoke(Object target, Object... arguments);
    }

    public interface FieldAccessor<T> {
        T get(Object target);

        void set(Object target, Object value);

        boolean hasField(Object target);
    }

    private ReflectionUtils() {}

}
