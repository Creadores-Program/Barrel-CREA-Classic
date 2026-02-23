package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetSpawnpointPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class SetSpawnPositionPacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.SetSpawnPositionPacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetSpawnPositionPacket) pk;
        Vector3i pos = packet.getSpawnPosition();
        int classicX = Utils.mapCoords(pos.getX(), player.getMinPosBedrock().getX(), player.getMaxPosBedrock().getX(), player.getMinPosClassic().getX(), player.getMaxPosClassic().getX());
        int classicY = pos.getY();
        int classicZ = Utils.mapCoords(pos.getZ(), player.getMinPosBedrock().getZ(), player.getMaxPosBedrock().getZ(), player.getMinPosClassic().getZ(), player.getMaxPosClassic().getZ());
        player.getClassicSession().send(new ServerSetSpawnpointPacket(classicX, classicY, classicZ, ((int) player.yaw), ((int) player.pitch)));
    }
}
