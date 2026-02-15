package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
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
      player.setPosition(Utils.mapCoords(packet.getX(), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX())), packet.getY(), Utils.mapCoords(packet.getZ(), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ())));
      if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT) {
        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(player.getRuntimeEntityId());
        movePlayerPacket.setPosition(player.getVector3f());
        movePlayerPacket.setRotation(Vector3f.from(player.getPitch(), player.getYaw(), player.getYaw()));
        movePlayerPacket.setMode(MovePlayerPacket.Mode.NORMAL);
        movePlayerPacket.setOnGround(packet.isOnGround());
        movePlayerPacket.setRidingRuntimeEntityId(0);
        movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
        movePlayerPacket.setEntityType(0);

        player.getBedrockClientSession().sendPacket(movePlayerPacket);
      }
    }
}
