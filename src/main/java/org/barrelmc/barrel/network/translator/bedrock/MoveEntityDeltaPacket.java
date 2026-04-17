package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Entity;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerChangeModelPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddEntity2Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSpawnPlayerPacket;

public class MoveEntityDeltaPacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player){
        org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket packet = (org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket) pk;
        if(player.getEntitysIndex().get(packet.getRuntimeEntityId()) == null){
            return;
        }
        long targetUniqueId = player.getEntitysIndex().get(packet.getRuntimeEntityId());
        Entity foundEntity = (player.getEntitysSpawned().get(targetUniqueId) != null) ? player.getEntitysSpawned().get(targetUniqueId) : player.getEntitysUnspawn().get(targetUniqueId);
        updateEntity(foundEntity, packet);
        if(foundEntity.x > player.getMaxPosBedrock().getX() || foundEntity.x < player.getMinPosBedrock().getX() || foundEntity.z > player.getMaxPosBedrock().getZ() || foundEntity.z < player.getMinPosBedrock().getZ()){
            if(player.getEntitysSpawned().remove(targetUniqueId) != null){
                player.getEntitysUnspawn().put(targetUniqueId, foundEntity);
                player.getClassicSession().send(new ServerDespawnPlayerPacket((int) foundEntity.rEId));
            }
            return;
        }
        if(player.getEntitysUnspawn().remove(targetUniqueId) != null){
            player.getEntitysSpawned().put(targetUniqueId, foundEntity);
            if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
            player.getClassicSession().send(new ServerExtAddEntity2Packet(((int) foundEntity.rEId), foundEntity.name, foundEntity.name, Utils.mapCoords(((short) foundEntity.x), ((short) player.getMinPosBedrock().getX()), ((short) player.getMaxPosBedrock().getX()), ((short) player.getMinPosClassic().getX()), ((short) player.getMaxPosClassic().getX())), ((short) foundEntity.y), Utils.mapCoords(((short) foundEntity.z), ((short) player.getMinPosBedrock().getZ()), ((short) player.getMaxPosBedrock().getZ()), ((short) player.getMinPosClassic().getZ()), ((short) player.getMaxPosClassic().getZ())), ((int) foundEntity.yaw/*yawClassic*/), ((int) foundEntity.pitch)));
            }else{
                player.getClassicSession().send(new ServerSpawnPlayerPacket(((int) foundEntity.rEId), foundEntity.name, Utils.mapCoords(foundEntity.x, ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), foundEntity.y, Utils.mapCoords(foundEntity.z, ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), foundEntity.yaw/*yawClassic*/, foundEntity.pitch));
            }
            if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(4), player.getExtensionsClassic())){
                player.getClassicSession().send(new ServerChangeModelPacket(((int) foundEntity.rEId), foundEntity.type));
            }
        }
        player.getClassicSession().send(new ServerPositionRotationPacket(((int) packet.getRuntimeEntityId()), Utils.mapCoords(foundEntity.x, ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), foundEntity.y, Utils.mapCoords(foundEntity.z, ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), foundEntity.yaw/*yawClassic*/, foundEntity.pitch));
    }
    private void updateEntity(Entity entity, org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket packet){
        if(packet.getFlags().contains(org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket.Flag.HAS_X)){
            entity.x = packet.getX();
        }
        if(packet.getFlags().contains(org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket.Flag.HAS_Y)){
            entity.y = packet.getY() + 1.62f;
        }
        if(packet.getFlags().contains(org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket.Flag.HAS_Z)){
            entity.z = packet.getZ();
        }
        if(packet.getFlags().contains(org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket.Flag.HAS_HEAD_YAW)){
            entity.yaw = packet.getHeadYaw() + Utils.FIX_YAW;
        }
        if(packet.getFlags().contains(org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket.Flag.HAS_PITCH)){
            entity.pitch = packet.getPitch();
        }
    }
}
