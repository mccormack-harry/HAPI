package me.hazedev.hapi.bossbar;

import me.hazedev.hapi.config.ConfigurableSection;
import me.hazedev.hapi.config.YamlConfigurableFile;
import me.hazedev.hapi.config.YamlOption;
import me.hazedev.hapi.config.value.OptionEnum;
import me.hazedev.hapi.config.value.OptionLong;
import me.hazedev.hapi.config.value.OptionString;
import org.bukkit.boss.BarColor;

public class BossBarConfiguration extends ConfigurableSection {

    public final YamlOption<BarColor> color = new OptionEnum<>("color", BarColor.class, BarColor.PINK);
    public final YamlOption<String> title = new OptionString("title", "Welcome to the server!");
    public final YamlOption<Long> duration = new OptionLong("duration", 10 * 20L);

    public BossBarConfiguration(YamlConfigurableFile root, String path) {
        super(root, path);
    }

}
