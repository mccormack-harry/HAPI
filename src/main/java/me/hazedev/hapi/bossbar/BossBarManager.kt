package me.hazedev.hapi.bossbar

import me.hazedev.hapi.component.Component
import me.hazedev.hapi.config.YamlConfigReader
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

class BossBarManager : Component("bossbar") {

    private val bossBarKey by lazy { getNamespacedKey("bossbar") }
    private val configuration by lazy { BossBarConfigurationFile(getYamlFileHandler("bossbars.yml")) }
    private val bossBar by lazy { Bukkit.createBossBar(bossBarKey, "", BarColor.PINK, BarStyle.SOLID) }
    private val bossBarRenderer by lazy { BossBarRenderer() }
    private var renderingTask: BukkitTask? = null

    override fun onEnable(): Boolean {
        registerListener(BossBarListener())
        return reload()
    }

    override fun reload(): Boolean {
        bossBar.isVisible = false
        renderingTask?.cancel()
        renderingTask = null
        YamlConfigReader.reload(configuration)
        if (configuration.configurations.get().isNotEmpty()) {
            renderingTask = bossBarRenderer.start()
        }
        return true
    }

    private inner class BossBarRenderer : Runnable {

        private lateinit var activeConfiguration: BossBarConfiguration
        private var tickDelay = 1L
        private var activeIndex = 0
        private var activeDuration = 0L

        fun start() : BukkitTask {
            val configurations = configuration.configurations.get()
            tickDelay = configuration.tickDelay.get()
            activeIndex = 0
            activeDuration = 0
            activeConfiguration = configurations[activeIndex]
            Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer)
            bossBar.isVisible = true
            return Bukkit.getScheduler().runTaskTimer(plugin, this, 0, tickDelay)
        }

        override fun run() {
            if (activeDuration >= activeConfiguration.duration.get()) {
                activeDuration = 0
                activeIndex++
                val configurations = configuration.configurations.get()
                if (activeIndex >= configurations.size) {
                    activeIndex = 0
                }
                activeConfiguration = configurations[activeIndex]
                bossBar.color = activeConfiguration.color.get()
                bossBar.title
            }
            bossBar.progress = activeDuration.toDouble() / activeConfiguration.duration.get()
            activeDuration += tickDelay
        }

    }

    private inner class BossBarListener : Listener {

        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            bossBar.addPlayer(event.player)
        }

        @EventHandler
        fun onPlayerQuit(event: PlayerQuitEvent) {
            bossBar.removePlayer(event.player)
        }

    }
}