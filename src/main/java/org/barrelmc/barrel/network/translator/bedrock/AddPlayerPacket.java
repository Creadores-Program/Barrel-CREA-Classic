package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddEntity2Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSpawnPlayerPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Entity;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.utils.nukkit.TextFormat;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class AddPlayerPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket packet = (org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket) pk;

        if(packet.getRuntimeEntityId() == player.getRuntimeEntityId()){
            return;
        }
        Vector3f position = packet.getPosition();
        Vector3f rotation = packet.getRotation();
        String name = TextFormat.colorizeToCc(packet.getUsername(), Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(17), player.getExtensionsClassic()));
        long runId = packet.getRuntimeEntityId();
        long uniId = packet.getUniqueEntityId();
        Entity classicEntity = new Entity(name, runId, name, position, rotation.toVector2());
        player.getEntitysIndex().put(runId, uniId);
        if(position.getX() > player.getMaxPosBedrock().getX() || position.getX() < player.getMinPosBedrock().getX() || position.getZ() > player.getMaxPosBedrock().getZ() || position.getZ() < player.getMinPosBedrock().getZ()){
            player.getEntitysUnspawn().put(uniId, classicEntity);
            return;
        }
        player.getEntitysSpawned().put(uniId, classicEntity);
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
            player.getClassicSession().send(new ServerExtAddEntity2Packet(((int) runId), name, name, Utils.mapCoords(((short) position.getX()), ((short) player.getMinPosBedrock().getX()), ((short) player.getMaxPosBedrock().getX()), ((short) player.getMinPosClassic().getX()), ((short) player.getMaxPosClassic().getX())), (short) (position.getY() + 2.0f), Utils.mapCoords(((short) position.getZ()), ((short) player.getMinPosBedrock().getZ()), ((short) player.getMaxPosBedrock().getZ()), ((short) player.getMinPosClassic().getZ()), ((short) player.getMaxPosClassic().getZ())), ((int) rotation.getY()), ((int) rotation.getX())));
            return;
        }
        player.getClassicSession().send(new ServerSpawnPlayerPacket(((int) runId), name, Utils.mapCoords(position.getX(), ((float) player.getMinPosBedrock().getX()), ((float) player.getMaxPosBedrock().getX()), ((float) player.getMinPosClassic().getX()), ((float) player.getMaxPosClassic().getX())), position.getY() + 2.0f, Utils.mapCoords(position.getZ(), ((float) player.getMinPosBedrock().getZ()), ((float) player.getMaxPosBedrock().getZ()), ((float) player.getMinPosClassic().getZ()), ((float) player.getMaxPosClassic().getZ())), rotation.getY(), rotation.getX()));
    }
}
