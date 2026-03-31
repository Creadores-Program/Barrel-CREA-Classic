package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.Entity;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class RemoveEntityPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket packet = (org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket) pk;
        Entity foundEntity = null;
        long targetUniqueId = packet.getUniqueEntityId();
        if((foundEntity = player.getEntitysUnspawn().remove(targetUniqueId)) != null ||
            (foundEntity = player.getEntitysSpawned().remove(targetUniqueId)) != null){
                player.getEntitysIndex().remove(foundEntity.rEId);
                player.getClassicSession().send(new ServerDespawnPlayerPacket((int) foundEntity.rEId));
        }
    }
}
