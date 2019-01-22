package jab.test.memoryleak;

import net.minecraft.server.v1_13_R2.*;

import java.lang.reflect.Field;

/**
 * NOTE: This is a minimal version of a utility class that is published publicly for testing
 * purposes.
 *
 * @author Josh
 */
class PacketUtils {

  /** This field stores the raw byte array that is cloned from one given in the constructor. */
  private static Field fieldMapPacketByteArray;

  /** This field stores the index for the mini-map packet. */
  private static Field fieldMapPacketIndex;

  static int getMapId(PacketPlayOutMap packet) {
    int mapId = 0;
    try {
      mapId = fieldMapPacketIndex.getInt(packet);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return mapId;
  }

  /**
   * Sets the PacketPlayOutMap's byte array directly.
   *
   * @param packet The packet to modify.
   * @param data The data array to set for the packet.
   */
  static void setRawByteArrayForMapPacket(PacketPlayOutMap packet, byte[] data) {
    try {
      fieldMapPacketByteArray.set(packet, data);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  static {
    try {
      // The map index field for map packets.
      fieldMapPacketIndex = PacketPlayOutMap.class.getDeclaredField("a");
      fieldMapPacketIndex.setAccessible(true);
      // The byte[] field for map packets. Being able to replace this helps with linking cache data
      // with the packet directly, so when the cache changes, this already changes the map packet.
      fieldMapPacketByteArray = PacketPlayOutMap.class.getDeclaredField("i");
      fieldMapPacketByteArray.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
