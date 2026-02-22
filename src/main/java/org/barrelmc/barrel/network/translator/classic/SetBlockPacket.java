package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetBlockPacket;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientSetBlockPacket;
import com.github.steveice10.mc.classic.protocol.data.game.SetBlockMode;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.math.vector.Vector3i;
public class SetBlockPacket implements ClassicPacketTranslator{
  @Override
  public void translate(Packet pk, Player player) {
    ClientSetBlockPacket packet = (ClientSetBlockPacket) pk;
    if(player.getGameMode() != GameType.CREATIVE || packet.getMode() == SetBlockMode.CREATED){
      player.getClassicSession().send(new ServerSetBlockPacket(packet.getX(), packet.getY(), packet.getZ(), ((int) player.mapClassic[(packet.getY() * 256 + packet.getZ()) * 256 + packet.getX()])));
      return;
    }
    player.mapClassic[(packet.getY() * 256 + packet.getZ()) * 256 + packet.getX()] = 0;
    PlayerActionPacket playerActionPacket = new PlayerActionPacket();
    playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_REQUEST_OR_CREATIVE_DESTROY_BLOCK);
    int bedrockX = Utils.mapCoords(packet.getX(), player.getMinPosClassic().getX(), player.getMaxPosClassic().getX(), player.getMinPosBedrock().getX(), player.getMaxPosBedrock().getX());
    int bedrockY = packet.getY();
    int bedrockZ = Utils.mapCoords(packet.getZ(), player.getMinPosClassic().getZ(), player.getMaxPosClassic().getZ(), player.getMinPosBedrock().getZ(), player.getMaxPosBedrock().getZ());
    Vector3i pos = new Vector3i(bedrockX, bedrockY, bedrockZ);
    playerActionPacket.setBlockPosition(pos);
    playerActionPacket.setResultPosition(pos);
    playerActionPacket.setFace(1);
    playerActionPacket.setRuntimeEntityId(player.getRuntimeEntityId());
    player.getBedrockClientSession().sendPacket(playerActionPacket);
  }
}
