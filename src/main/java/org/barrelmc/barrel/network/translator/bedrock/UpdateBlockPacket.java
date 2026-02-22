package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetBlockPacket;
import org.barrelmc.barrel.network.converter.BlockConverter;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class UpdateBlockPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket packet = (org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket) pk;

        if (packet.getDataLayer() == 0) {
            Vector3i pos = packet.getBlockPosition();
            if(player.getMaxPosBedrock().getX() < pos.getX() || player.getMinPosBedrock().getX() > pos.getX() || player.getMaxPosBedrock().getZ() < pos.getZ() || player.getMinPosBedrock().getZ() > pos.getZ() || player.getMaxPosBedrock().getY() < pos.getY() || player.getMinPosBedrock().getY() > pos.getY()){
                return;
            }
            int blockState = BlockConverter.bedrockRuntimeToClassicStateId(packet.getDefinition().getRuntimeId());
            int classicX = Utils.mapCoords(pos.getX(), player.getMinPosBedrock().getX(), player.getMaxPosBedrock().getX(), player.getMinPosClassic().getX(), player.getMaxPosClassic().getX());
            int classicY = pos.getY();
            int classicZ = Utils.mapCoords(pos.getZ(), player.getMinPosBedrock().getZ(), player.getMaxPosBedrock().getZ(), player.getMinPosClassic().getZ(), player.getMaxPosClassic().getZ());
            player.mapClassic[(classicY * 256 + classicZ) * 256 + classicX] = (byte) blockState;
            player.getClassicSession().send(new ServerSetBlockPacket(classicX, classicY, classicZ, blockState));
        }
    }
}
