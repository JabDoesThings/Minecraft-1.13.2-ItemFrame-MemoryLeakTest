package jab.test.memoryleak;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Collection;

/** @author Josh */
abstract class Test implements Runnable {

  static World world;
  static final int wx = 180;
  static final int wy = 70;
  static final int wz = 60;

  static final int width = 8;
  static final int height = 6;

  static void wall() {
    for (int y = wy; y < wy + height; y++) {
      for (int x = wx; x < wx + width; x++) {
        world.getBlockAt(x, y, wz).setType(Material.STONE);
      }
    }
  }

  static void air() {
    for (int y = wy; y < wy + height; y++) {
      for (int x = wx; x < wx + width; x++) {
        world.getBlockAt(x, y, wz + 1).setType(Material.AIR);
      }
    }
  }

  static void clean() {
    for (Entity entity : world.getEntities()) {
      if (entity instanceof ItemFrame) {
        Location location = entity.getLocation();
        int lx = location.getBlockX();
        int ly = location.getBlockY();
        int lz = location.getBlockZ();
        boolean bX = lx >= wx && lx <= wx + width;
        boolean bY = ly >= wy && ly <= wy + height;
        boolean bZ = lz == wz + 1;
        if (bX && bY && bZ) {
          entity.remove();
        }
      }
    }
    for (int y = wy; y < wy + height; y++) {
      for (int x = wx; x < wx + width; x++) {
        world.getBlockAt(x, y, wz).setType(Material.AIR);
      }
    }
  }

  @SuppressWarnings("SameParameterValue")
  ItemFrame createItemFrame(
      Collection<ItemFrame> itemFrames, World world, int x, int y, int z, BlockFace blockFace) {
    Location loc = new Location(world, x, y, z);
    Chunk chunk = loc.getChunk();
    if (!chunk.isLoaded()) {
      chunk.load();
    }
    ItemFrame itemFrame = null;
    for (ItemFrame frame : itemFrames) {
      Location location = frame.getLocation();
      if (location.getBlockX() == x && location.getBlockY() == y && location.getBlockZ() == z) {
        itemFrame = frame;
        break;
      }
    }
    if (itemFrame == null) {
      itemFrame = world.spawn(new Location(world, x, y, z), ItemFrame.class);
      itemFrame.setFacingDirection(blockFace);
    }
    return itemFrame;
  }

  abstract void start();

  abstract void stop();
}
