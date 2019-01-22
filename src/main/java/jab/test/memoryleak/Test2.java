package jab.test.memoryleak;

import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

/** @author Josh */
@SuppressWarnings("unused")
public class Test2 extends Test implements Listener {

  private ItemFrame[][] itemFrames;
  private ItemStack stackA;
  private ItemStack stackB;
  private PacketPlayOutMap packetMapA;
  private PacketPlayOutMap packetMapB;
  private BukkitRunnable runnable;
  private boolean flag;

  Test2() {
    flag = false;
  }

  @Override
  void start() {
    runnable =
        new BukkitRunnable() {
          @Override
          public void run() {
            Test2.this.run();
          }
        };
    JavaPlugin plugin = PluginMemoryLeak.getInstance();
    plugin.getServer().getPluginManager().registerEvents(Test2.this, plugin);
    wall();
    air();
    createItemFrames();
    createMaps();
    runnable.runTaskTimer(plugin, 0L, 10L);
    for (Player player : Bukkit.getOnlinePlayers()) {
      PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      connection.sendPacket(packetMapA);
      connection.sendPacket(packetMapB);
      player.sendMessage("MapImages sent.");
    }
  }

  @Override
  void stop() {
    HandlerList.unregisterAll(this);
    try {
      runnable.cancel();
    } catch (Exception e) {
      // No need to throw.
    }
  }

  @Override
  public void run() {
    // Select the next stack to set.
    ItemStack stack = flag ? stackB : stackA;
    flag = !flag;
    // Go through and set all frames to use the stack.
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        itemFrames[x][y].setItem(stack);
      }
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      connection.sendPacket(packetMapA);
      connection.sendPacket(packetMapB);
    }
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
    new BukkitRunnable() {

      @Override
      public void run() {
        connection.sendPacket(packetMapA);
        connection.sendPacket(packetMapB);
      }
    }.runTaskLater(PluginMemoryLeak.getInstance(), 30L);
  }

  private void createMaps() {
    MapImage mapImageA = new MapImage((byte) 16);
    MapImage mapImageB = new MapImage((byte) 32);
    packetMapA = mapImageA.createPacket((short) -1);
    packetMapB = mapImageB.createPacket((short) -2);
    stackA = new ItemStack(Material.FILLED_MAP);
    stackB = new ItemStack(Material.FILLED_MAP);
    MapMeta mapMeta = (MapMeta) stackA.getItemMeta();
    mapMeta.setMapId(mapImageA.getPacketId());
    stackA.setItemMeta(mapMeta);
    mapMeta = (MapMeta) stackB.getItemMeta();
    mapMeta.setMapId(mapImageB.getPacketId());
    stackB.setItemMeta(mapMeta);
  }

  private void createItemFrames() {
    Collection<ItemFrame> frames = world.getEntitiesByClass(ItemFrame.class);
    itemFrames = new ItemFrame[width][height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        itemFrames[x][y] = createItemFrame(frames, world, wx + x, wy + y, wz + 1, BlockFace.SOUTH);
      }
    }
  }
}
