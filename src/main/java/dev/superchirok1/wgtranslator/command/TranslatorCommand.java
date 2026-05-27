package dev.superchirok1.wgtranslator.command;

import dev.superchirok1.wgtranslator.config.Configuration;
import dev.superchirok1.wgtranslator.WorldGuardTranslator;
import dev.superchirok1.wgtranslator.serializer.Text;
import dev.superchirok1.wgtranslator.serializer.impl.LegacyAmpersandSerializer;
import dev.superchirok1.wgtranslator.translation.Translation;
import dev.superchirok1.wgtranslator.translation.TranslationManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TranslatorCommand implements CommandExecutor, TabCompleter {

    private final WorldGuardTranslator plugin;
    private final LegacyAmpersandSerializer text = Text.getLegacyAmpersandSerializer();

    private static final List<String> TAB_1 = Arrays.asList("reload", "restart", "component", "help", "denyMessage");
    private static final List<String> TAB_COMPONENT = Arrays.asList("add", "set", "list");
    private static final List<String> TAB_DM = Arrays.asList("set", "setTemp");

    public TranslatorCommand(WorldGuardTranslator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean permission = sender.hasPermission(Configuration.permissionAdmin);
        if (!permission || args.length == 0) {
            return defaultMessage(sender, permission, label);
        }

        Translation.Messages messages = TranslationManager.getTranslation().messages;

        switch (args[0].toLowerCase()) {
            case "reload", "restart", "r":
                long start = System.currentTimeMillis();
                plugin.reload(sender.getName());
                sender.sendMessage(String.format(messages.reloaded(), System.currentTimeMillis() - start));
                return true;
            case "denymessage": {
                if (args.length < 2) {
                    sender.sendMessage(messages.help().replace("%cmd%", label));
                    return true;
                }
                String msg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                switch (args[1].toLowerCase()) {
                    case "set": {
                        TranslationManager.setDenyMessage(msg);
                        sender.sendMessage(messages.denyMessageInstalled());
                        break;
                    }
                    case "settemp": {
                        TranslationManager.setMemoryDenyMessage(msg);
                        sender.sendMessage(messages.denyMessageInstalledTemp());
                        break;
                    }
                    default:
                        sender.sendMessage(messages.help().replace("%cmd%", label));
                        break;
                }
                return true;
            }
            case "component":
                if (args.length < 2) {
                    sender.sendMessage(messages.help().replace("%cmd%", label));
                    return true;
                }

                Translation.Components components = TranslationManager.getTranslation().components;

                switch (args[1].toLowerCase()) {
                    case "add", "set":
                        String msg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                        if (msg.indexOf(';') == -1) {
                            sender.sendMessage(messages.componentUsage());
                            return true;
                        }
                        String[] strings = msg.split(";");
                        if (strings.length < 2) {
                            sender.sendMessage(messages.componentUsage());
                            return true;
                        }

                        components.set(TranslationManager.getConfig().getConfigurationSection("components"), strings[0].trim(), strings[1].trim());
                        sender.sendMessage(String.format(messages.componentAdded(), strings[0].trim(), strings[1].trim()));
                        break;

                    case "list":
                        sender.sendMessage(text.colorize(messages.componentListHeader()));
                        for (Map.Entry<String, String> map : components.getMap().entrySet()) {
                            sender.sendMessage(
                                    messages.componentListFormat()
                                            .replace("{0}", map.getKey())
                                            .replace("{1}", map.getValue())
                            );
                        }
                        break;

                    default:
                        sender.sendMessage(messages.help().replace("%cmd%", label));
                        break;
                }
                return true;

            case "help":
                sender.sendMessage(messages.help().replace("%cmd%", label));
                return true;

            default:
                return defaultMessage(sender, true, label);
        }
    }


    private boolean defaultMessage(CommandSender sender, boolean permission, String command) {
        sender.sendMessage(" ");
        sender.sendMessage(text.colorize("  &3&lWorldGuard-Translator &8• &7v" + plugin.getDescription().getVersion()));
        sender.sendMessage(text.colorize("  &8• &fAuthors: &b" + String.join(", ", plugin.getDescription().getAuthors())));
        TextComponent component = new TextComponent(text.colorize("  &8• &fRepository: "));
        TextComponent link = new TextComponent(text.colorize("&b&nGitHub (Click for open)&r"));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/SuperCHIROK1/WorldGuard-Translator"));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(text.colorize("&3» &bhttps://github.com/SuperCHIROK1/WorldGuard-Translator"))}));
        component.addExtra(link);
        sender.spigot().sendMessage(component);
        sender.sendMessage(" ");
        if (permission) {
            sender.sendMessage(text.colorize("  &8• &fHelp: &e/" + command + " help"));
            sender.sendMessage(" ");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(Configuration.permissionAdmin)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return TAB_1;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("component")) {
                return TAB_COMPONENT;
            }
            if (args[0].equalsIgnoreCase("denymessage")) {
                return TAB_DM;
            }
        }
        return Collections.emptyList();
    }
}
