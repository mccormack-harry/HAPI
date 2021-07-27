package me.hazedev.hapi.bossbar;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.config.YamlConfigReader;
import me.hazedev.hapi.config.YamlConfigurableFile;
import me.hazedev.hapi.config.YamlOption;
import me.hazedev.hapi.config.value.OptionListConfigurableSection;
import me.hazedev.hapi.config.value.OptionLong;
import me.hazedev.hapi.io.YamlFileHandler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class BossBarRenderer implements Runnable, YamlConfigurableFile {

    private final BossBarComponent component;
    private final YamlFileHandler fileHandler;
    private BukkitTask renderingTask = null;

    public final YamlOption<Long> tickDelay = new OptionLong("delay", 1L);
    public final OptionListConfigurableSection<BossBarConfiguration> bossBarConfigurations = new OptionListConfigurableSection<>("bossbars", (path) -> new BossBarConfiguration(this, path), "default");

    private BossBar bossBar;
    private BossBarConfiguration activeConfiguration;
    private int activeIndex;
    private int activeTicks;

    BossBarRenderer(BossBarComponent component, YamlFileHandler fileHandler) {
        this.component = component;
        this.fileHandler = fileHandler;
    }

    @Override
    public void run() {
        long duration = activeConfiguration.duration.get();
        if (activeTicks >= duration) {
            activeTicks = 0;
            activeIndex++;
            List<BossBarConfiguration> configurations = bossBarConfigurations.get();
            if (configurations.isEmpty()) {
                stop();
                return;
            }
            if (activeIndex >= configurations.size()) {
                activeIndex = 0;
            }
            activeConfiguration = configurations.get(activeIndex);
            bossBar.setTitle(CCUtils.addColor(activeConfiguration.title.get()));
            bossBar.setColor(activeConfiguration.color.get());
            duration = activeConfiguration.duration.get();
        }
        bossBar.setProgress((float) activeTicks / duration);
        activeTicks += tickDelay.get();
    }

    public void addPlayer(@NotNull Player player) {
        if (bossBar != null) {
            bossBar.addPlayer(player);
        }
    }

    public void removePlayer(@NotNull Player player) {
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    public boolean isActive() {
        return renderingTask != null;
    }

    public void reload() {
        stop();
        fileHandler.reloadConfig();
        start();
    }

    public void start() {
        if (!isActive()) {
            YamlConfigReader.saveDefaults(this);
            YamlConfigReader.read(this);
            if (!bossBarConfigurations.get().isEmpty()) {
                activeIndex = 0;
                activeTicks = 0;
                activeConfiguration = bossBarConfigurations.get().get(0);
                if (bossBar == null) {
                    bossBar = Bukkit.createBossBar(component.getBossBarKey(), CCUtils.addColor(activeConfiguration.title.get()), activeConfiguration.color.get(), BarStyle.SOLID);
                }
                bossBar.setVisible(true);
                renderingTask = Bukkit.getScheduler().runTaskTimer(component.getPlugin(), this, 0, tickDelay.get());
            }
        }
    }

    public void stop() {
        if (isActive()) {
            renderingTask.cancel();
            renderingTask = null;
        }
        if (bossBar != null) {
            bossBar.setVisible(false);
        }
    }

    @NotNull
    @Override
    public YamlConfiguration getConfiguration() {
        return fileHandler.getConfiguration();
    }

    @NotNull
    @Override
    public String getFileName() {
        return fileHandler.getFile().getName();
    }

    @Override
    public void saveConfig() throws IOException {
        fileHandler.saveConfig();
    }
}
