package me.hazedev.hapi.bossbar

import me.hazedev.hapi.config.ConfigurableSection
import me.hazedev.hapi.config.YamlConfigurableFile
import me.hazedev.hapi.config.value.OptionEnum
import me.hazedev.hapi.config.value.OptionListConfigurableSection
import me.hazedev.hapi.config.value.OptionLong
import me.hazedev.hapi.config.value.OptionString
import me.hazedev.hapi.io.YamlFileHandler
import org.bukkit.boss.BarColor
import org.bukkit.configuration.file.YamlConfiguration

class BossBarConfigurationFile(private val fileHandler: YamlFileHandler) : YamlConfigurableFile {

    @JvmField
    val tickDelay = OptionLong("delay", 1L)
    @JvmField
    val configurations = OptionListConfigurableSection("bossbars", "default") { BossBarConfiguration(this, it) }

    override fun getConfiguration(): YamlConfiguration {
        return fileHandler.configuration
    }

    override fun getFileName(): String {
        return fileHandler.configuration.name
    }

    override fun saveConfig() {
        fileHandler.saveConfig()
    }

}

class BossBarConfiguration(root: BossBarConfigurationFile, path: String) : ConfigurableSection(root, path) {

    @JvmField
    val color = OptionEnum("color", BarColor::class.java, BarColor.PINK)
    @JvmField
    val title = OptionString("title", "Welcome to the server!")
    @JvmField
    val duration = OptionLong("duration", 10 * 20L)

}