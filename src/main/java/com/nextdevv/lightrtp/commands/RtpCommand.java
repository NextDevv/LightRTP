package com.nextdevv.lightrtp.commands;

import com.nextdevv.lightrtp.LightRTP;
import com.nextdevv.lightrtp.enums.TpStatus;
import com.nextdevv.lightrtp.managers.RtpManager;
import com.nextdevv.lightrtp.utils.ChatUtils;
import com.nextdevv.lightrtp.utils.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.nextdevv.lightrtp.utils.ChatUtils.actionBar;
import static com.nextdevv.lightrtp.utils.ChatUtils.msg;

public class RtpCommand implements CommandExecutor, TabExecutor {
    LightRTP plugin;
    RtpManager rtpManager;
    HashMap<UUID, Long> cooldowns = new HashMap<>();
    HashMap<UUID, Boolean> executing = new HashMap<>();
    HashMap<UUID, Boolean> stop = new HashMap<>();

    public RtpCommand(LightRTP plugin) {
        this.plugin = plugin;
        this.rtpManager = new RtpManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            msg(sender, plugin.getMessages().getOnlyPlayers());
            return true;
        }

        if(!sender.hasPermission("lightrtp.rtp")) {
            msg(sender, plugin.getMessages().getNoPermission());
            return true;
        }

        if(player.getWorld().getEnvironment() == World.Environment.NETHER && !plugin.getSettings().isNether()) {
            msg(sender, plugin.getMessages().getNetherNotAllowed());
            return true;
        }

        long cooldown = plugin.getSettings().getCommandCooldown();
        if(cooldowns.containsKey(player.getUniqueId())) {
            long last = cooldowns.get(player.getUniqueId());
            long now = System.currentTimeMillis();
            long diff = now - last;
            if(diff < cooldown) {
                long remaining = cooldown - diff;
                msg(player, plugin.getMessages().getCooldown()
                        .replace("{time}", String.valueOf(remaining / 1000)));
                return true;
            } else cooldowns.remove(player.getUniqueId());
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            if(!executing.get(player.getUniqueId())) {
                msg(player, plugin.getMessages().getNotTeleporting());
                return true;
            }

            executing.put(player.getUniqueId(), false);
            stop.put(player.getUniqueId(), true);
            msg(player, plugin.getMessages().getCancelled());
            return true;
        }

        if(executing.get(player.getUniqueId()) != null && executing.get(player.getUniqueId())) {
            msg(player, plugin.getMessages().getAlreadyTeleporting());
            return true;
        }

        executing.put(player.getUniqueId(), true);
        new BukkitRunnable() {
            @Override
            public void run() {
                processTp(player, 0, command.getName().equals("rtpnear"), command.getName().equals("rtpcave"));
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private void processTp(Player player, int tryCount, boolean near, boolean cave) {
        if(tryCount >= plugin.getSettings().getMaxAttempts()) {
            msg(player, plugin.getMessages().getError());
            return;
        }

        Future<Pair<Location, TpStatus>> future = rtpManager.generate(player, near, cave);
        Location initial = player.getLocation().clone();
        msg(player, plugin.getMessages().getLoadingPosition());
        while (!future.isDone()) {
            for (String loading : plugin.getMessages().getLoading()) {
                try {
                    ChatUtils.actionBar(player, loading);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        tpSync(future, player, initial, tryCount, near, cave);
    }

    private void tpSync(Future<Pair<Location, TpStatus>> future, Player player, Location initial, int tryCount, boolean near, boolean cave) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if(stop.get(player.getUniqueId()) != null && stop.get(player.getUniqueId())) {
                        stop.put(player.getUniqueId(), false);
                        return;
                    }

                    if(future.get().getB() == TpStatus.MOVED) {
                        executing.put(player.getUniqueId(), false);
                        return;
                    }

                    if(tryCount >= plugin.getSettings().getMaxAttempts()) {
                        msg(player, plugin.getMessages().getError());
                        executing.put(player.getUniqueId(), false);
                        return;
                    }

                    if(future.get().getA() == null) {
                        msg(player, plugin.getMessages().getRetrying()
                                .replace("{attempt}", String.valueOf(tryCount + 1))
                                .replace("{maxAttempts}", String.valueOf(plugin.getSettings().getMaxAttempts())));
                        doAsync(() -> processTp(player, tryCount + 1, near, cave));
                        return;
                    }

                    player.teleport(future.get().getA());

                    if(plugin.getSettings().isDebug()) {
                        plugin.getLogger().info("Teleported " + player.getName() + " to " + future.get().toString());
                        plugin.getLogger().info("Initial location: " + initial.toString());
                        plugin.getLogger().info("Try count: " + tryCount);
                        plugin.getLogger().info("Max attempts: " + plugin.getSettings().getMaxAttempts());
                        plugin.getLogger().info("Is in water: " + player.isInWater());
                    }

                    if(player.isInWater()) {
                        if(!plugin.getSettings().isWater()) {
                            msg(player, plugin.getMessages().getRetrying()
                                    .replace("{attempt}", String.valueOf(tryCount + 1))
                                    .replace("{maxAttempts}", String.valueOf(plugin.getSettings().getMaxAttempts())));
                            doAsync(() -> processTp(player, tryCount + 1, near, cave));
                            executing.put(player.getUniqueId(), false);
                            return;
                        }
                    }

                    actionBar(player, plugin.getMessages().getTeleported());
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                } catch (InterruptedException | ExecutionException e) {
                    msg(player, plugin.getMessages().getError());
                    throw new RuntimeException(e);
                } finally {
                    executing.put(player.getUniqueId(), false);
                }
            }
        }.runTask(plugin);
    }

    public void doAsync(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of("stop");
    }
}
