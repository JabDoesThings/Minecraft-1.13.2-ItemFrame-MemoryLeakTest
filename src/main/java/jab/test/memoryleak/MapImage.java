package jab.test.memoryleak;

import java.util.ArrayList;

import net.minecraft.server.v1_13_R2.PacketPlayOutMap;

/**
 * NOTE: This is a minimal version of a utility class that is published publicly for testing
 * purposes.
 *
 * @author Josh
 */
@SuppressWarnings("FieldCanBeLocal")
class MapImage {

  private PacketPlayOutMap packet;
  private final byte[] bytes;
  private final int width = 128;
  private final int height = 128;

  MapImage(byte color) {
    this.bytes = new byte[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        bytes[getOffset(x, y)] = color;
      }
    }
  }

  PacketPlayOutMap createPacket(short index) {
    if (packet != null) {
      throw new IllegalStateException("Packet is already created for MapImage.");
    }
    byte b = (byte) 0;
    packet = new PacketPlayOutMap(index, b, true, new ArrayList<>(), bytes, 0, 0, 128, 128);
    PacketUtils.setRawByteArrayForMapPacket(packet, bytes);
    return packet;
  }

  private int getOffset(int x, int y) {
    return (y * width) + x;
  }

  int getPacketId() {
    return PacketUtils.getMapId(packet);
  }
}
