package dev.superchirok1.wgtranslator.util;

import dev.superchirok1.wgtranslator.WorldGuardTranslator;
import dev.superchirok1.wgtranslator.config.Configuration;
import dev.superchirok1.wgtranslator.serializer.Text;
import dev.superchirok1.wgtranslator.serializer.impl.LegacyAmpersandSerializer;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private final WorldGuardTranslator plugin;
    private final LegacyAmpersandSerializer text = Text.getLegacyAmpersandSerializer();

    private static final String LINK = "https://raw.githubusercontent.com/SuperCHIROK1/WorldGuard-Translator/main/VERSION";
    public static boolean updateAvailable = false;
    public static String version;

    private TextComponent component;

    public UpdateChecker(WorldGuardTranslator plugin) {
        this.plugin = plugin;
        this.component = new TextComponent(text.colorize(" &8• &fDownload: &b"));
        TextComponent link = new TextComponent("GitHub Releases");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/SuperCHIROK1/WorldGuard-Translator/releases"));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(text.colorize("&3» &bhttps://github.com/SuperCHIROK1/WorldGuard-Translator/releases"))}));
        this.component.addExtra(link);
    }

    public static void check(WorldGuardTranslator plugin, Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(LINK).openStream()))) {
                version = reader.readLine().trim();
                consumer.accept(version);
            } catch (IOException e) {
                Logger.warn("Unable to check update: " + e.getMessage());
            }
        }, 60L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!UpdateChecker.updateAvailable) return;
        Player player = event.getPlayer();
        if (!player.hasPermission(Configuration.permissionUpdateLog)) return;
        player.sendMessage(text.colorize(""));
        player.sendMessage(text.colorize(" &3&lWG-Translator &8• &7Update available! &8• &c" + plugin.getDescription().getVersion() + " &7-> &a" + version));
        player.sendMessage(component);
        player.sendMessage(text.colorize(""));
    }

}
