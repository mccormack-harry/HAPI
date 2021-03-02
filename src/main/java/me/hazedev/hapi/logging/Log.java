package me.hazedev.hapi.logging;

import me.hazedev.hapi.chat.CCUtils;
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
    public static final Level DEBUG_LEVEL = new DebugLevel();
    public static final Level ERROR_LEVEL = new ErrorLevel();
    public static final Level CHAT_LEVEL = new ChatLevel();
    public static final Level VOTE_LEVEL = new VoteLevel();

    public static void registerLogger(Logger logger) {
        if (logger != null) {
            additionalLoggers.add(logger);
        }
    }

    public static void log(Component component, Level level, String message) {
        String formatted = formatMessage(component, message);
        Bukkit.getLogger().log(level instanceof CustomLevel ? Level.INFO : level, formatted);
        for (Logger logger : additionalLoggers) {
            try {
                logger.log(level, formatted);
            } catch (Exception e) {
                additionalLoggers.remove(logger);
                Bukkit.getLogger().warning("Failed to log to additional logger: " + logger.getName());
                Log.error(component, e);
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
        log(component, DEBUG_LEVEL, message);
    }

    public static void debug(String message) {
        debug(null, message);
    }

    public static void error(Component component, Throwable throwable) {
        log(component, ERROR_LEVEL, ExceptionUtils.getStackTrace(throwable));
    }

    public static void error(Throwable throwable) {
        error(null, throwable);
    }

    public static void chat(String entry) {
        log(null, CHAT_LEVEL, entry);
    }

    public static void vote(String message) {
        log(null, VOTE_LEVEL, message);
    }

    private static String formatMessage(Component component, String message) {
        message = CCUtils.stripColor(message);
        if (component != null) {
            message = "[" + component.getId() + "] " + message;
        }
        return message;
    }

    private abstract static class CustomLevel extends Level {
        protected CustomLevel(String name, int value) {
            super(name, value);
        }

        protected CustomLevel(String name) {
            this(name, 800);
        }
    }

    public static class DebugLevel extends CustomLevel {
        protected DebugLevel() {
            super("DEBUG", 700);
        }
    }

    public static class ErrorLevel extends CustomLevel {
        protected ErrorLevel() {
            super("ERROR", 900);
        }
    }

    public static class ChatLevel extends CustomLevel {
        protected ChatLevel() {
            super("CHAT");
        }
    }

    public static class VoteLevel extends CustomLevel {
        public VoteLevel() {
            super("VOTE");
        }
    }

}
