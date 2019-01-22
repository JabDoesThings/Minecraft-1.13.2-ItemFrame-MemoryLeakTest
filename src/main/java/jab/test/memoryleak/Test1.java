package jab.test.memoryleak;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

/** @author Josh */
public class Test1 extends Test {

  private BukkitRunnable runnable;

  private ItemFrame[][] itemFrames;
  private ItemStack stackA;
  private ItemStack stackB;
  private boolean flag;

  Test1() {
    flag = false;
  }

  @Override
  void start() {
    runnable =
        new BukkitRunnable() {
          @Override
          public void run() {
            Test1.this.run();
          }
        };
    wall();
    air();
    createItemFrames();
    createMaps();
    runnable.runTaskTimer(PluginMemoryLeak.getInstance(), 0L, 10L);
  }

  @Override
  void stop() {
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
  }

  @SuppressWarnings("deprecation")
  private void createMaps() {
    MapView mapViewA = Bukkit.createMap(world);
    MapView mapViewB = Bukkit.createMap(world);
    mapViewA.removeRenderer(mapViewA.getRenderers().get(0));
    mapViewB.removeRenderer(mapViewB.getRenderers().get(0));
    mapViewA.addRenderer(new MyMapRenderer(MapPalette.BLUE));
    mapViewB.addRenderer(new MyMapRenderer(MapPalette.RED));
    stackA = new ItemStack(Material.FILLED_MAP);
    stackB = new ItemStack(Material.FILLED_MAP);
    MapMeta mapMeta = (MapMeta) stackA.getItemMeta();
    mapMeta.setMapId(mapViewA.getId());
    stackA.setItemMeta(mapMeta);
    mapMeta = (MapMeta) stackB.getItemMeta();
    mapMeta.setMapId(mapViewB.getId());
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

  /**
   * The MapRenderer that sets the color for a map.
   *
   * @author Josh
   */
  private class MyMapRenderer extends MapRenderer {

    private final Byte color;

    MyMapRenderer(Byte color) {
      super(false);
      this.color = color;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
      for (int y = 0; y < 128; y++) {
        for (int x = 0; x < 128; x++) {
          mapCanvas.setPixel(x, y, color);
        }
      }
    }
  }
}
