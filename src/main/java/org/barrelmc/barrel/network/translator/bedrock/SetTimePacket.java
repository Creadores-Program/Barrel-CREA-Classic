package org.barrelmc.barrel.network.translator.bedrock;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvColorsPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
public class SetTimePacket implements BedrockPacketTranslator {
  private static final int TIMEL = 24000;
  @Override
  public void translate(BedrockPacket pk, Player player) {
    if(player.getDimension() != 0 || (!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic()))){
      return;
    }
    org.cloudburstmc.protocol.bedrock.packet.SetTimePacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetTimePacket) pk;
    int time = packet.getTime() % TIMEL;
    float f;
    ServerEnvColorsPacket color1 = null;
    ServerEnvColorsPacket color2 = null;
    ServerEnvColorsPacket color3 = null;
    ServerEnvColorsPacket color4 = null;
    if(time < 6000){
      f = time / 6000.0f;
      color1 = new ServerEnvColorsPacket(0, lerp(2, 255, f), lerp(2, 180, f), lerp(10, 100, f));
      color2 = new ServerEnvColorsPacket(2, lerp(2, 200, f), lerp(2, 150, f), lerp(5, 100, f));
      color3 = new ServerEnvColorsPacket(3, lerp(20, 100, f), lerp(20, 100, f), lerp(30, 120, f));
      color4 = new ServerEnvColorsPacket(4, lerp(50, 255, f), lerp(50, 200, f), lerp(80, 150, f));
    }else if(time < 12000){
      f = (time - 6000) / 6000.0f;
      color1 = new ServerEnvColorsPacket(0, lerp(255, 153, f), lerp(180, 204, f), lerp(100, 255, f));
      color2 = new ServerEnvColorsPacket(2, lerp(200, 153, f), lerp(150, 204, f), lerp(100, 255, f));
      color3 = new ServerEnvColorsPacket(3, lerp(100, 160, f), lerp(100, 160, f), lerp(120, 160, f));
      color4 = new ServerEnvColorsPacket(4, 255, lerp(200, 255, f), lerp(150, 255, f));
    }else if(time < 18000){
      f = (time - 12000) / 6000.0f;
      color1 = new ServerEnvColorsPacket(0, lerp(153, 255, f), lerp(204, 100, f), lerp(255, 50, f));
      color2 = new ServerEnvColorsPacket(2, lerp(153, 150, f), lerp(204, 50, f), lerp(255, 20, f));
      color3 = new ServerEnvColorsPacket(3, lerp(160, 80, f), lerp(160, 60, f), lerp(160, 60, f));
      color4 = new ServerEnvColorsPacket(4, 255, lerp(255, 120, f), lerp(255, 50, f));
    }else{
      f = (time - 18000) / 6000.0f;
      color1 = new ServerEnvColorsPacket(0, lerp(255, 2, f), lerp(100, 2, f), lerp(50, 10, f));
      color2 = new ServerEnvColorsPacket(2, lerp(150, 2, f), lerp(50, 2, f), lerp(20, 5, f));
      color3 = new ServerEnvColorsPacket(3, lerp(80, 20, f), lerp(60, 20, f), lerp(60, 30, f));
      color4 = new ServerEnvColorsPacket(4, lerp(255, 50, f), lerp(120, 50, f), lerp(50, 80, f));
    }

    player.getEnvCpe().updateAmbient(color1, color2, color3, color4);
  }
  private int lerp(int start, int end, float factor){
    return Math.round(start + (end - start) * factor);
  }
}
