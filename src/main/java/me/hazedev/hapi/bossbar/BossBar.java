package me.hazedev.hapi.bossbar;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;

public class BossBar extends Component implements Listener {

    private YamlFileHandler fileHandler;
    private org.bukkit.boss.BossBar bossBar = null;

    private BarColor color;
    private String title;
    private float progress;

    public BossBar() {
        super("bossbar");
    }

    @Override
    public boolean onEnable() {
        File file = new File(getDataFolder(), "bossbar.yml");
        try {
            fileHandler = new YamlFileHandler(file);
        } catch (IOException e) {
            Log.warning("Failed to open bossbar.yml");
            Log.error(this, e);
            return false;
        }
        YamlConfiguration c = fileHandler.getConfiguration();
        try {
            color = BarColor.valueOf(c.getString("color"));
        } catch (Exception e) {
            color = BarColor.BLUE;
        }
        title = c.getString("title");
        progress = (float) c.getDouble("progress", 1);
        bossBar = Bukkit.createBossBar(CCUtils.addColor(title), color, BarStyle.SOLID);
        bossBar.setProgress(progress);
        bossBar.setVisible(true);
        registerCommand(new BossBarCommandHandler(this));
        return true;
    }

    @Override
    public void save() {
        YamlConfiguration c = fileHandler.getConfiguration();
        c.set("color", color.name());
        c.set("title", title);
        c.set("progress", progress);
        try {
            fileHandler.save();
        } catch (IOException e) {
            Log.error(this, e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        bossBar.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        bossBar.removePlayer(event.getPlayer());
    }

    public void setColor(BarColor color) {
        this.color = color;
        bossBar.setColor(color);
    }

    public void setTitle(String title) {
        this.title = title;
        bossBar.setTitle(CCUtils.addColor(title));
    }

    public void setProgress(float progress) {
        this.progress = progress;
        bossBar.setProgress(progress);
    }

}
