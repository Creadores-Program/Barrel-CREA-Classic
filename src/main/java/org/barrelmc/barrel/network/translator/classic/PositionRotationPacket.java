package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
public class PositionRotationPacket implements ClassicPacketTranslator{
  private static final float OP_MOD = 360.0f;
  private static final float MAXDSQ_STEP = 0.14f;
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
      float bedrockX = Utils.mapCoords(((float) packet.getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX()), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()));
      float bedrockZ = Utils.mapCoords(((float) packet.getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ()), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()));
      Vector3f pos = player.getVector3f();
      if(pos.getX() == bedrockX && pos.getY() == packet.getY() && pos.getZ() == bedrockZ && player.getYaw() == packet.getYaw() && player.getPitch() == packet.getPitch()){
        return;
      }
      player.setOldPosition(pos);
      if(isSprinting(player, bedrockX, bedrockZ) && !player.isSprinting()){
        if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT) {
          PlayerActionPacket playerActionPacket = new PlayerActionPacket();
          playerActionPacket.setAction(PlayerActionType.START_SPRINT);
          playerActionPacket.setBlockPosition(Vector3i.ZERO);
          playerActionPacket.setFace(0);
          playerActionPacket.setRuntimeEntityId(player.getRuntimeEntityId());
          player.getBedrockClientSession().sendPacket(playerActionPacket);
        } else {
          player.getPlayerAuthInputData().add(PlayerAuthInputData.START_SPRINTING);
        }
        player.setSprinting(true);
      }else if(player.isSprinting()){
        if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT) {
          PlayerActionPacket playerActionPacket = new PlayerActionPacket();
          playerActionPacket.setAction(PlayerActionType.STOP_SPRINT);
          playerActionPacket.setBlockPosition(Vector3i.ZERO);
          playerActionPacket.setFace(0);
          playerActionPacket.setRuntimeEntityId(player.getRuntimeEntityId());
          player.getBedrockClientSession().sendPacket(playerActionPacket);
        } else {
          player.getPlayerAuthInputData().add(PlayerAuthInputData.STOP_SPRINTING);
        }
        player.setSprinting(false);
      }
      player.setPosition(bedrockX, packet.getY(), bedrockZ);
      player.setRotation((packet.getYaw() + Utils.FIX_YAW) % OP_MOD, packet.getPitch());
      if (player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT) {
        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(player.getRuntimeEntityId());
        Vector3f npos = player.getVector3f();
        movePlayerPacket.setPosition(npos);
        movePlayerPacket.setRotation(Vector3f.from(player.getPitch(), player.getYaw(), player.getYaw()));
        movePlayerPacket.setMode(MovePlayerPacket.Mode.NORMAL);
        movePlayerPacket.setOnGround(player.getOldPosition().getY() == npos.getY());
        movePlayerPacket.setRidingRuntimeEntityId(0);
        movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
        movePlayerPacket.setEntityType(0);

        player.getBedrockClientSession().sendPacket(movePlayerPacket);
      }
    }
    private static boolean isSprinting(Player player, float newX, float newZ){
      float oldX = (float) player.getX();
      float oldZ = (float) player.getZ();
      float dx = newX - oldX;
      float dz = newZ - oldZ;
      float disSq = (dx * dx) + (dz * dz);
      return disSq > MAXDSQ_STEP;
    }
}
