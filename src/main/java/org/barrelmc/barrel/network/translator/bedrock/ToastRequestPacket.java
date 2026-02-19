package org.barrelmc.barrel.network.translator.bedrock;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
public class ToastRequestPacket implements BedrockPacketTranslator {
  @Override
  public void translate(BedrockPacket pk, Player player) {
    org.cloudburstmc.protocol.bedrock.packet.ToastRequestPacket packet = (org.cloudburstmc.protocol.bedrock.packet.ToastRequestPacket) pk;
    if(Utils.getExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic()) != null){
      player.sendMessage(packet.getTitle(), PlayerIds.BOTTOMRIGHT2);
      player.sendMessage(packet.getContent(), PlayerIds.BOTTOMRIGHT1);
      return;
    }
    player.sendMessage(packet.getTitle() + "\n" + packet.getContent());
  }
}
