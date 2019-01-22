package jab.test.memoryleak;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** @author Josh */
public class CommandTest implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
    PluginMemoryLeak plugin = PluginMemoryLeak.getInstance();
    Test1 test1 = plugin.getTest1();
    Test2 test2 = plugin.getTest2();
    test1.stop();
    test2.stop();
    String arg = args.length > 0 ? args[0] : "stop";
    if (arg.equalsIgnoreCase("stop")) {
      commandSender.sendMessage("Test(s) stopped.");
      Test.clean();
      return true;
    }
    int test;
    try {
      test = Integer.parseInt(arg);
    } catch (NumberFormatException e) {
      commandSender.sendMessage("Invalid test number: " + arg);
      return true;
    }
    if (test == 1) {
      commandSender.sendMessage("Test 1 (no-leak) started.");
      test1.start();
    } else if (test == 2) {
      commandSender.sendMessage("Test 2 (leak) started.");
      test2.start();
    } else {
      commandSender.sendMessage("Invalid test number: " + arg);
    }
    return true;
  }
}
