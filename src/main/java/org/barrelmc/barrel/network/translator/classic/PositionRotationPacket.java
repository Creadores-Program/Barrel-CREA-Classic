package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
public class PositionRotationPacket implements ClassicPacketTranslator{
  @Override
    public void translate(Packet pk, Player player) {
      if(player.getStatusWorld() == StatusWorld.LOGIN){
        return;
      }
      ClientPositionRotationPacket packet = (ClientPositionRotationPacket) pk;
      if (player.isImmobile()) {
          float classicX = Utils.mapCoords(((float) player.getX()), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()));
          float classicY = (float) player.getY();
          float classicZ = Utils.mapCoords(((float) player.getZ()), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()));
          player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, classicX, classicY, classicZ, ((float) player.yaw), ((float) player.pitch)));
          return;
      }
      float bedrockX = Utils.mapCoords(packet.getX(), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()));
      float bedrockZ = Utils.mapCoords(packet.getZ(), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()));
      if(player.getPosition().getX() == bedrockX && player.getPosition().getY() == packet.getY() && player.getPosition().getZ() == bedrockZ && player.getYaw() == packet.getYaw() && player.getPitch() == packet.getPitch()){
        return;
      }
      player.setOldPosition(player.getVector3f());
      player.setPosition(bedrockX, packet.getY(), bedrockZ);
      player.setRotation(packet.getYaw(), packet.getPitch());
      if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT) {
        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(player.getRuntimeEntityId());
        movePlayerPacket.setPosition(player.getVector3f());
        movePlayerPacket.setRotation(Vector3f.from(player.getPitch(), player.getYaw(), player.getYaw()));
        movePlayerPacket.setMode(MovePlayerPacket.Mode.NORMAL);
        movePlayerPacket.setOnGround(player.getOldPosition().getY() == player.getVector3f().getY());
        movePlayerPacket.setRidingRuntimeEntityId(0);
        movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
        movePlayerPacket.setEntityType(0);

        player.getBedrockClientSession().sendPacket(movePlayerPacket);
      }
    }
}
