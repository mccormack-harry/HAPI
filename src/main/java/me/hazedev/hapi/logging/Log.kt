package me.hazedev.hapi.logging

import me.hazedev.hapi.component.Component
import org.apache.commons.lang.exception.ExceptionUtils
import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

object Log {

    private val additionalLoggers = HashSet<Logger>()
    @JvmField
    val ERROR = ErrorLevel
    @JvmField
    val DEBUG = DebugLevel
    @JvmField
    val CHAT = ChatLevel
    @JvmField
    val VOTE = VoteLevel

    @JvmStatic
    fun registerLogger(logger: Logger) {
        additionalLoggers.add(logger)
    }

    @JvmStatic
    fun log(component: Component?, level: Level, message: String) {
        val formatted = prefix(component, message)
        Bukkit.getLogger().log(if (level is CustomLevel) Level.INFO else level, formatted)
        for (logger in additionalLoggers) {
            try {
                logger.log(level, formatted)
            } catch (e: Exception) {
                additionalLoggers.remove(logger)
                error(component, e, "Failed to log to additional logger: ${logger.name}")
            }
        }
    }

    @JvmStatic
    fun info(component: Component? = null, message: String) {
        log(component, Level.INFO, message)
    }

    @JvmStatic
    fun warning(component: Component? = null, message: String) {
        log(component, Level.WARNING, message)
    }

    @JvmStatic
    fun error(component: Component? = null, throwable: Throwable, message: String?) {
        val stackTrace = ExceptionUtils.getStackTrace(throwable)
        val formatted = if (message != null) {
            StringBuilder(message).appendLine().append(stackTrace).toString()
        } else {
            stackTrace
        }
        log(component, ErrorLevel, formatted)
    }

    @JvmStatic
    fun debug(component: Component? = null, message: String) {
        log(component, DebugLevel, message)
    }

    @JvmStatic
    fun chat(message: String) {
        log(null, ChatLevel, message)
    }

    @JvmStatic
    fun vote(message: String) {
        log(null, VoteLevel, message)
    }

    @JvmStatic
    fun prefix(component: Component?, message: String): String {
        if (component != null) {
            return "[${component.id}] $message"
        }
        return message
    }

}

sealed class CustomLevel(name: String?, value: Int) : Level(name, value)

object ErrorLevel: CustomLevel("ERROR", 900)

object DebugLevel: CustomLevel("DEBUG", 700)

object ChatLevel: CustomLevel("CHAT", INFO.intValue())

object VoteLevel: CustomLevel("VOTE", INFO.intValue())