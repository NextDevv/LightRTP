package com.nextdevv.lightrtp.managers;

import com.nextdevv.lightrtp.LightRTP;
import com.nextdevv.lightrtp.enums.RandomOption;
import com.nextdevv.lightrtp.enums.TpStatus;
import com.nextdevv.lightrtp.utils.IntUtils;
import com.nextdevv.lightrtp.utils.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import static com.nextdevv.lightrtp.utils.ChatUtils.msg;

public class RtpManager {
    LightRTP plugin;
    SecureRandom secureRandom = new SecureRandom();
    Random random = new Random();

    final int MAX_NETHER_HEIGHT = 128;
    final int MIN_NETHER_HEIGHT = 0;

    public RtpManager(LightRTP plugin) {
        this.plugin = plugin;
    }

    public Future<Pair<Location, TpStatus>> generate(Player player, Boolean near, Boolean cave) {
        return CompletableFuture.supplyAsync(() -> {
            Location initial = player.getLocation().clone();
            int playerY = initial.getBlockY();
            long seed = plugin.getSettings().getSeed();
            int xBoundary = plugin.getSettings().getXBoundary();
            int zBoundary = plugin.getSettings().getZBoundary();

            int xBoundaryNear = plugin.getSettings().getXBoundaryNear();
            int zBoundaryNear = plugin.getSettings().getZBoundaryNear();

            int x, z;

            if(seed == 0) {
                seed = ThreadLocalRandom.current().nextLong();
            }

            if(plugin.getSettings().getRandomOption() == RandomOption.SECURE_RANDOM) {
                secureRandom.setSeed(seed);
                if(!near) {
                    x = secureRandom.nextInt(xBoundary * 2) - xBoundary;
                    z = secureRandom.nextInt(zBoundary * 2) - zBoundary;
                } else {
                    x = initial.getBlockX() + secureRandom.nextInt(xBoundaryNear) - xBoundaryNear;
                    z = initial.getBlockZ() + secureRandom.nextInt(zBoundaryNear) - zBoundaryNear;
                }

            } else if(plugin.getSettings().getRandomOption() == RandomOption.RANDOM) {
                random.setSeed(seed);
                if(!near) {
                    x = random.nextInt(xBoundary * 2) - xBoundary;
                    z = random.nextInt(zBoundary * 2) - zBoundary;
                } else {
                    x = initial.getBlockX() + random.nextInt(xBoundaryNear) - xBoundaryNear;
                    z = initial.getBlockZ() + random.nextInt(zBoundaryNear) - zBoundaryNear;
                }
            } else {
                if(!near) {
                    x = ThreadLocalRandom.current().nextInt(xBoundary * 2) - xBoundary;
                    z = ThreadLocalRandom.current().nextInt(zBoundary * 2) - zBoundary;
                } else {
                    x = initial.getBlockX() + ThreadLocalRandom.current().nextInt(xBoundaryNear) - xBoundaryNear;
                    z = initial.getBlockZ() + ThreadLocalRandom.current().nextInt(zBoundaryNear) - zBoundaryNear;
                }
            }

            boolean found = false;
            int y = !cave ? playerY : -64;
            long startTime = System.currentTimeMillis();
            while(!found) {
                if(System.currentTimeMillis() - startTime > plugin.getSettings().getTimeout()) {
                    msg(player, plugin.getMessages().getError());
                    return Pair.of(null, TpStatus.ERROR);
                }

                if(!plugin.getSettings().isAllowMovement()) {
                    if(!player.getLocation().equals(initial)) {
                        msg(player, plugin.getMessages().getYouMoved());
                        return Pair.of(null, TpStatus.MOVED);
                    }
                }

                if(player.getWorld().getEnvironment() == World.Environment.NETHER && plugin.getSettings().isNether())
                    if(y >= 128 || y < 0)
                        y = 0;

                if(y >= 320 || y <= -64)
                    y = 0;

                Block block = Objects.requireNonNull(initial.getWorld()).getBlockAt(x, y, z);
                Block blockBelow = block.getRelative(0, -1, 0);
                Block blockAbove = block.getRelative(0, 1, 0);

                if(plugin.getSettings().isDebug()) {
                    plugin.getLogger().info("blockBelow.isLiquid(): " + blockBelow.isLiquid());
                    plugin.getLogger().info("block.isLiquid(): " + block.isLiquid());
                    plugin.getLogger().info("block.isEmpty(): " + block.isEmpty());
                    plugin.getLogger().info("blockAbove.isEmpty(): " + blockAbove.isEmpty());
                    plugin.getLogger().info("Found a location for " + player.getName() + " at " + x + " " + y + " " + z + "\n____________________________");
                }

                if(block.isEmpty() && blockAbove.isEmpty() && !blockBelow.isEmpty()) {
                    if(plugin.getSettings().isDebug()) {
                        plugin.getLogger().info("blockBelow.isLiquid(): " + blockBelow.isLiquid());
                        plugin.getLogger().info("block.isLiquid(): " + block.isLiquid());
                        plugin.getLogger().info("block.isEmpty(): " + block.isEmpty());
                        plugin.getLogger().info("block.getRelative(0, 1, 0).isLiquid(): " + block.getRelative(0, 1, 0).isLiquid());
                        plugin.getLogger().info("block.getRelative(0, 2, 0).isLiquid(): " + block.getRelative(0, 2, 0).isLiquid());
                    }

                    if (!plugin.getSettings().isWater()) {
                        if (blockBelow.isLiquid() || block.isLiquid() || blockAbove.isLiquid()) {
                            x = randomInt(-xBoundary, xBoundary);
                            z = randomInt(-zBoundary, zBoundary);
                            continue;
                        }
                    }

                    if(!plugin.getSettings().isCaves() && !cave) {
                        if(player.getWorld().getEnvironment() != World.Environment.NETHER) {
                            int lightFromSky = block.getLightFromSky();
                            if(lightFromSky < 15) {
                                y++;
                                continue;
                            }
                        }
                    }

                    if(cave) {
                        int lightFromSky = block.getLightFromSky();
                        if(lightFromSky >= 5) {
                            y++;
                            continue;
                        }
                    }

                    found = true;
                } else {
                    y++;
                }
            }

            return Pair.of(new Location(initial.getWorld(), x, y, z), TpStatus.SUCCESS);
        });
    }

    private void doSync(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(plugin);
    }

    private int randomInt(int min, int max) {
        return switch (plugin.getSettings().getRandomOption()) {
            case SECURE_RANDOM -> secureRandom.nextInt(max - min) + min;
            case RANDOM -> random.nextInt(max - min) + min;
            default -> ThreadLocalRandom.current().nextInt(max - min) + min;
        };
    }
}
