package me.hazedev.hapi.bossbar;

import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class BossBarComponent extends Component implements Listener {

    private BossBarRenderer bossBarRenderer;

    public BossBarComponent() {
        super("bossbar");
    }

    @Override
    public boolean onEnable() {
        YamlFileHandler fileHandler;
        try {
            fileHandler = getYamlFileHandler("bossbars.yml");
        } catch (IOException e) {
            Log.warning(this, "Failed to open config");
            Log.error(this, e);
            return false;
        }
        bossBarRenderer = new BossBarRenderer(this, fileHandler);
        return reload();
    }

    @Override
    protected boolean reload() {
        bossBarRenderer.reload();
        Bukkit.getOnlinePlayers().forEach(bossBarRenderer::addPlayer);
        return true;
    }

    @Override
    protected void onDisable() {
        bossBarRenderer.stop();
        bossBarRenderer = null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        bossBarRenderer.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        bossBarRenderer.removePlayer(event.getPlayer());
    }

}
