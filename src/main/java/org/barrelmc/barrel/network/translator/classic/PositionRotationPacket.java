package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtEntityTeleportPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
public class PositionRotationPacket implements ClassicPacketTranslator{
  @Override
    public void translate(Packet pk, Player player) {
      ClientPositionRotationPacket packet = (ClientPositionRotationPacket) pk;
      if (player.isImmobile()) {
          player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, player.x, player.y, player.z, player.yaw, player.pitch));
          return;
      }
      player.setOldPosition(player.getVector3f());
    }
}
