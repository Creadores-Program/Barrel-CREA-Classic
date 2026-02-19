package org.barrelmc.barrel.network.translator.bedrock;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
public class SetTitlePacket implements BedrockPacketTranslator {
  @Override
    public void translate(BedrockPacket pk, Player player) {
      org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket) pk;
      int idMsg = PlayerIds.CONSOLE;
      if(Utils.getExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic()) != null){
          idMsg = PlayerIds.CHAT;
      }
      switch(packet.getType()){
        case TITLE_JSON:
        case TITLE: {
          if(Utils.getExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic()) != null){
            idMsg = PlayerIds.ANNOUNCEMENT;
          }
          break;
        }
        case SUBTITLE_JSON:
        case SUBTITLE: {
          //aqui
          player.getJavaSession().send(new ClientboundSetSubtitleTextPacket(Component.text(packet.getText())));
          break;
        }
        case ACTIONBAR_JSON:
        case ACTIONBAR: {
          player.getJavaSession().send(new ClientboundSetActionBarTextPacket(Component.text(packet.getText())));
          break;
        }
        default:
          return;
      }
      player.sendMessage(packet.getText(), idMsg);
    }
}
