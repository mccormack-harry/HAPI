package me.hazedev.hapi.logging;

import me.hazedev.hapi.component.Component;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

//
public class Log {

    private static final Set<Logger> additionalLoggers = new HashSet<>();
    private static final Level DEBUG = new DebugLevel();
    private static final Level ERROR = new ErrorLevel();
    private static final Level CHAT = new ChatLevel();

    public static void registerLogger(Logger logger) {
        if (logger != null) {
            additionalLoggers.add(logger);
        }
    }

    public static void log(Component component, Level level, String message) {
        String formatted = formatMessage(component, message);
        Bukkit.getLogger().log(level, formatted);
        for (Logger logger : additionalLoggers) {
            try {
                logger.log(level, formatted);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to log to additional logger: " + logger.getName());
                Log.error(component, e);
                additionalLoggers.remove(logger);
            }
        }
    }

    public static void info(Component component, String message) {
        log(component, Level.INFO, message);
    }

    public static void info(String message) {
        info(null, message);
    }

    public static void warning(Component component, String message) {
        log(component, Level.WARNING, message);
    }

    public static void warning(String message) {
        warning(null, message);
    }

    public static void debug(Component component, String message) {
        log(component, DEBUG, message);
    }

    public static void debug(String message) {
        debug(null, message);
    }

    public static void error(Component component, Throwable throwable) {
        log(component, ERROR, ExceptionUtils.getStackTrace(throwable));
    }

    public static void error(Throwable throwable) {
        error(null, throwable);
    }

    public static void chat(String entry) {
        log(null, CHAT, entry);
    }

    private static String formatMessage(Component component, String message) {
        if (component != null) {
            return "[" + component.getId() + "] " + message;
        }
        return message;
    }

    public static class DebugLevel extends Level {
        protected DebugLevel() {
            super("DEBUG", 801);
        }
    }

    public static class ErrorLevel extends Level {
        protected ErrorLevel() {
            super("ERROR", 802);
        }
    }

    public static class ChatLevel extends Level {
        protected ChatLevel() {
            super("CHAT", 803);
        }
    }

}
