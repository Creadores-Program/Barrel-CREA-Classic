package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddEntity2Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelInitializePacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Entity;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class MovePlayerPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket packet = (org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket) pk;
        Vector3f position = packet.getPosition(), rotation = packet.getRotation();
        float yawClassic = rotation.getY() + Utils.FIX_YAW;

        if (packet.getRuntimeEntityId() == player.getRuntimeEntityId()) {
            if(position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()){
                player.getClassicSession().send(new ServerLevelInitializePacket());
                player.setStatusWorld(StatusWorld.BUILD_WORLD);
                player.setMaxPosBedrock(Vector3i.from(((int) Math.round(position.getX() + 127)), 255, ((int) Math.round(position.getZ() + 127))));
                player.setMinPosBedrock(Vector3i.from(((int) Math.round(position.getX() + -128)), 0, ((int) Math.round(position.getZ() + -128))));
            }
            player.getClassicSession().send(new ServerPositionRotationPacket(PlayerIds.SELF, Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), position.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), yawClassic, rotation.getX()));
            player.setPosition(position.getX(), position.getY(), position.getZ());
            player.setLastServerPosition(position);
            player.setLastServerRotation(rotation.toVector2());
        } else {
            long targetUniqueId = player.getEntitysIndex().get(packet.getRuntimeEntityId());
            Entity foundEntity = null;
            if(position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()){
                if((foundEntity = player.getEntitysSpawned().remove(targetUniqueId)) != null){
                    player.getEntitysUnspawn().put(targetUniqueId, foundEntity);
                    player.getClassicSession().send(new ServerDespawnPlayerPacket((int) foundEntity.rEId));
                }
                return;
            }
            if((foundEntity = player.getEntitysUnspawn().remove(targetUniqueId)) != null){
                player.getEntitysSpawned().put(targetUniqueId, foundEntity);
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerExtAddEntity2Packet(((int) foundEntity.rEId), foundEntity.name, foundEntity.name, Utils.mapCoords(((short) position.getX()), ((short) player.getMinPosBedrock().getX()), ((short) player.getMaxPosBedrock().getX()), ((short) player.getMinPosClassic().getX()), ((short) player.getMaxPosClassic().getX())), ((short) position.getY()), Utils.mapCoords(((short) position.getZ()), ((short) player.getMinPosBedrock().getZ()), ((short) player.getMaxPosBedrock().getZ()), ((short) player.getMinPosClassic().getZ()), ((short) player.getMaxPosClassic().getZ())), ((int) yawClassic), ((int) rotation.getX())));
                }else{
                    player.getClassicSession().send(new ServerSpawnPlayerPacket(((int) foundEntity.rEId), foundEntity.name, Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), position.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), yawClassic, rotation.getX()));
                }
            }
            player.getClassicSession().send(new ServerPositionRotationPacket(((int) packet.getRuntimeEntityId()), Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), position.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), yawClassic, rotation.getX()));
        }
    }
}
