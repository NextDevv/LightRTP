package com.nextdevv.lightrtp.commands;

import com.nextdevv.lightrtp.LightRTP;
import com.nextdevv.lightrtp.configs.ConfigLoader;
import com.nextdevv.lightrtp.enums.RandomOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

import static com.nextdevv.lightrtp.utils.ChatUtils.msg;

public class LightRtpAdminCommand implements CommandExecutor, TabExecutor {
    LightRTP plugin;

    public LightRtpAdminCommand(LightRTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("lightrtp.admin")) {
            sender.sendMessage("§cYou do not have permission to execute this command.");
            return true;
        }

        if(args.length == 0) {
            msg(sender, "&7===== &6LightRTP by NextDevv &7=====");
            msg(sender, "&6/lightrtp reload &7- Reload the plugin configuration.");
            msg(sender, "&6/lightrtp setseed <seed> &7- Set the seed for the random teleportation.");
            msg(sender, "&6/lightrtp setboundary <x> <z> &7- Set the boundary for random teleportation.");
            msg(sender, "&6/lightrtp settimeout <timeout> &7- Set the timeout for random teleportation.");
            msg(sender, "&6/lightrtp setcooldown <cooldown> &7- Set the cooldown for the command.");
            msg(sender, "&6/lightrtp setrandom <random> &7- Set the random option for random teleportation. [RANDOM, THREAD_LOCAL_RANDOM, SECURE_RANDOM]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                ConfigLoader configLoader = new ConfigLoader(plugin.getDataFolder());
                plugin.setSettings(configLoader.loadSettings());
                plugin.setMessages(configLoader.loadMessages());
                msg(sender, "&6Configuration reloaded.");
                break;
            case "setseed":
                try {
                    long seed = Long.parseLong(args[1]);
                    plugin.getSettings().setSeed(seed);
                    msg(sender, "&7Seed set to &6" + seed + ".");
                } catch (NumberFormatException e) {
                    msg(sender, "&cInvalid seed.");
                }
                break;
            case "setboundary":
                try {
                    int x = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);
                    plugin.getSettings().setXBoundary(x);
                    plugin.getSettings().setZBoundary(z);
                    msg(sender, "&7Boundary set to &6" + x + "&7, &6" + z + ".");
                } catch (NumberFormatException e) {
                    msg(sender, "&cInvalid boundary.");
                }
                break;
            case "settimeout":
                try {
                    long timeout = Long.parseLong(args[1]);
                    plugin.getSettings().setTimeout(timeout);
                    msg(sender, "&7Timeout set to &6" + timeout + ".");
                } catch (NumberFormatException e) {
                    msg(sender, "&cInvalid timeout.");
                }
                break;
            case "setcooldown":
                try {
                    long cooldown = Long.parseLong(args[1]);
                    plugin.getSettings().setCommandCooldown(cooldown);
                    msg(sender, "&7Cooldown set to &6" + cooldown + ".");
                } catch (NumberFormatException e) {
                    msg(sender, "&cInvalid cooldown.");
                }
                break;
            case "setrandom":
                try {
                    RandomOption randomOption = RandomOption.valueOf(args[1].toUpperCase());
                    plugin.getSettings().setRandomOption(randomOption);
                    msg(sender, "&7Random option set to &6" + args[1] + ".");
                } catch (IllegalArgumentException e) {
                    msg(sender, "&cInvalid random option.");
                }
                break;
            default:
                msg(sender, "&cInvalid subcommand.");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length > 1) {
            switch (args[0].toLowerCase()) {
                case "setrandom":
                    return List.of("RANDOM", "THREAD_LOCAL_RANDOM", "SECURE_RANDOM");
                case "setseed":
                    List<Long> randoms = new ArrayList<>();
                    for(int i = 0; i < 10; i++) {
                        randoms.add((long) (Math.random() * 10000));
                    }
                    return List.of(randoms.stream().map(String::valueOf).toArray(String[]::new));
                case "setboundary": return List.of("1000", "10000", "100000");
                case "settimeout", "setcooldown": return List.of("1000", "5000", "10000");
            }
        }
        return List.of("reload", "setseed", "setboundary", "settimeout", "setcooldown", "setrandom");
    }
}
