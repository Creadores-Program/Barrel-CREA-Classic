package org.barrelmc.barrel.network.translator.bedrock;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
public class SetTimePacket implements BedrockPacketTranslator {
  @Override
  public void translate(BedrockPacket pk, Player player) {
    if(player.getDimension() != 0 || (!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic()))){
      return;
    }
    org.cloudburstmc.protocol.bedrock.packet.SetTimePacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetTimePacket) pk;
    int time = packet.getTime();
    if(time < 6000){
      int timep = time / 6000;
      player.getClassicSession().send(new ServerEnvColorsPacket(0, lerp(2, 255, timep), lerp(2, 180, timep), lerp(10, 100, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(2, lerp(2, 200, timep), lerp(2, 150, timep), lerp(5, 100, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(3, lerp(20, 100, timep), lerp(20, 100, timep), lerp(30, 120, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(4, lerp(50, 255, timep), lerp(50, 200, timep), lerp(80, 150, timep)));
    }else if(time < 12000){
      int timep = (time - 6000) / 6000;
      player.getClassicSession().send(new ServerEnvColorsPacket(0, lerp(255, 153, timep), lerp(180, 204, timep), lerp(100, 255, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(2, lerp(200, 153, timep), lerp(150, 204, timep), lerp(100, 255, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(3, lerp(100, 160, timep), lerp(100, 160, timep), lerp(120, 160, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(4, 255, lerp(200, 255, timep), lerp(150, 255, timep)));
    }else if(time < 18000){
      int timep = (time - 12000) / 6000;
      player.getClassicSession().send(new ServerEnvColorsPacket(0, lerp(153, 255, timep), lerp(204, 100, timep), lerp(255, 50, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(2, lerp(153, 150, timep), lerp(204, 50, timep), lerp(255, 20, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(3, lerp(160, 80, timep), lerp(160, 60, timep), lerp(160, 60, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(4, 255, lerp(255, 120, timep), lerp(255, 50, timep)));
    }else{
      int timep = (time - 18000) / 6000;
      player.getClassicSession().send(new ServerEnvColorsPacket(0, lerp(255, 2, timep), lerp(100, 2, timep), lerp(50, 10, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(2, lerp(150, 2, timep), lerp(50, 2, timep), lerp(20, 5, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(3, lerp(80, 20, timep), lerp(60, 20, timep), lerp(60, 30, timep)));
      player.getClassicSession().send(new ServerEnvColorsPacket(4, lerp(255, 50, timep), lerp(120, 50, timep), lerp(50, 80, timep)));
    }
  }
  private int lerp(int start, int end, int time){
    return Math.round(start + (end - start) * time);
  }
}
