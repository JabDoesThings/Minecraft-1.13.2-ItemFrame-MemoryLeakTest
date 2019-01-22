package jab.test.memoryleak;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

/** @author Josh */
@SuppressWarnings("unused")
public class PluginMemoryLeak extends JavaPlugin implements Listener {

  private static PluginMemoryLeak instance;

  private Test1 test1;
  private Test2 test2;

  static PluginMemoryLeak getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;
    getServer().getPluginManager().registerEvents(this, this);
    test1 = new Test1();
    test2 = new Test2();
    getCommand("test").setExecutor(new CommandTest());
    Test.world = Bukkit.getWorld("world");
  }

  @Override
  public void onDisable() {
    instance = null;
    test2.stop();
  }

  @EventHandler
  public void on(ChunkUnloadEvent event) {
    event.setCancelled(true);
  }

  Test1 getTest1() {
    return test1;
  }

  Test2 getTest2() {
    return test2;
  }
}
