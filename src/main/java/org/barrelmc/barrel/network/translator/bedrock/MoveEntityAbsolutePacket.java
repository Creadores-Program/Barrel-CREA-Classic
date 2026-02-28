package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddEntity2Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerPositionRotationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerChangeModelPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.network.converter.EntityConverter;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import java.util.Optional;

public class MoveEntityAbsolutePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket packet = (org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket) pk;
        Vector3f position = packet.getPosition(), rotation = packet.getRotation();

        if(position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()){
            Optional<org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket> entityop = player.getEntitysSpawned().stream().filter(entity -> entity.getRuntimeEntityId() == packet.getRuntimeEntityId()).findFirst();
            entityop.ifPresent(entity -> {
                player.getClassicSession().send(new ServerDespawnPlayerPacket((int) packet.getRuntimeEntityId()));
                player.getEntitysSpawned().remove(entity);
                player.getEntitysUnspawn().add(entity);
            });
            return;
        }
        Optional<org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket> entityop1 = player.getEntitysUnspawn().stream().filter(entity -> entity.getRuntimeEntityId() == packet.getRuntimeEntityId()).findFirst();
        entityop1.ifPresent(entity -> {
            player.getEntitysUnspawn().remove(entity);
            player.getEntitysSpawned().add(entity);
            if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
                player.getClassicSession().send(new ServerExtAddEntity2Packet(((int) packet.getRuntimeEntityId()), "", "", Utils.mapCoords(((short) position.getX()), ((short) player.getMinPosBedrock().getX()), ((short) player.getMaxPosBedrock().getX()), ((short) player.getMinPosClassic().getX()), ((short) player.getMaxPosClassic().getX())), ((short) pos.getY()), Utils.mapCoords(((short) position.getZ()), ((short) player.getMinPosBedrock().getZ()), ((short) player.getMaxPosBedrock().getZ()), ((short) player.getMinPosClassic().getZ()), ((short) player.getMaxPosClassic().getZ())), ((int) rotation.getX()), ((int) rotation.getY())));
            }else{
                player.getClassicSession().send(new ServerSpawnPlayerPacket(((int) packet.getRuntimeEntityId()), "", Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), pos.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getX(), rotation.getY()));
            }
            if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(4), player.getExtensionsClassic())){
                player.getClassicSession().send(new ServerChangeModelPacket(((int) packet.getRuntimeEntityId()), EntityConverter.bedrockRuntimeToClassicStateId(entity.getEntityType(), entity.getIdentifier())));
            }
        });
        player.getClassicSession().send(new ServerPositionRotationPacket(((int) packet.getRuntimeEntityId()), Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), pos.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getX(), rotation.getY()));
    }
}
