package me.hazedev.hapi.bossbar;

import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BossBarComponent extends Component implements Listener {

    private NamespacedKey bossBarKey;
    private BossBarRenderer bossBarRenderer;

    public BossBarComponent() {
        super("bossbar");
    }

    @Override
    public boolean onEnable() throws Exception {
        YamlFileHandler fileHandler;
        try {
            fileHandler = getYamlFileHandler("bossbars.yml");
        } catch (IOException e) {
            Log.warning(this, "Failed to open config");
            Log.error(this, e);
            return false;
        }
        bossBarKey = getNamespacedKey("bossbar");
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
        Bukkit.removeBossBar(bossBarKey);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        bossBarRenderer.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        bossBarRenderer.removePlayer(event.getPlayer());
    }

    public @NotNull NamespacedKey getBossBarKey() {
        return bossBarKey;
    }

    public @NotNull BossBarRenderer getBossBarRenderer() {
        return bossBarRenderer;
    }
}
