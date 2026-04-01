package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddEntity2Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerChangeModelPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.Entity;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.network.converter.EntityConverter;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;

public class AddEntityPacket implements BedrockPacketTranslator{
    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket packet = (org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket) pk;

        if(packet.getRuntimeEntityId() == player.getRuntimeEntityId()){
            return;
        }

        Vector3f position = packet.getPosition();
        Vector2f rotation = packet.getRotation();
        String name = (packet.getMetadata().getString(EntityDataTypes.NAME) != null) ? packet.getMetadata().getString(EntityDataTypes.NAME) : "";
        long runid = packet.getRuntimeEntityId();
        long uniid = packet.getUniqueEntityId();
        String classicType = EntityConverter.bedrockRuntimeToClassicStateId(packet.getEntityType(), packet.getIdentifier());
        Entity classicEntity = new Entity(name, runid, classicType, position, rotation);
        player.getEntitysIndex().put(runid, uniid);
        if (position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()) {
            player.getEntitysUnspawn().put(uniid, classicEntity);
            return;
        }
        player.getEntitysSpawned().put(uniid, classicEntity);
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
            player.getClassicSession().send(new ServerExtAddEntity2Packet(((int) runid), name, name, Utils.mapCoords(((short) position.getX()), ((short) player.getMinPosBedrock().getX()), ((short) player.getMaxPosBedrock().getX()), ((short) player.getMinPosClassic().getX()), ((short) player.getMaxPosClassic().getX())), ((short) position.getY()), Utils.mapCoords(((short) position.getZ()), ((short) player.getMinPosBedrock().getZ()), ((short) player.getMaxPosBedrock().getZ()), ((short) player.getMinPosClassic().getZ()), ((short) player.getMaxPosClassic().getZ())), ((int) rotation.getX()), ((int) rotation.getY())));
        }else{
            player.getClassicSession().send(new ServerSpawnPlayerPacket(((int) runid), name, Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), position.getY(), Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getX(), rotation.getY()));
        }
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(4), player.getExtensionsClassic())){
            player.getClassicSession().send(new ServerChangeModelPacket(((int) runid), classicType));
        }
    }
}
