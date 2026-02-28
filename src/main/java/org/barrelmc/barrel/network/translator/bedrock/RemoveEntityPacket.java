package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket;
import java.util.Optional;

public class RemoveEntityPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket packet = (org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket) pk;
        Optional<AddEntityPacket> entity1 = player.getEntitysUnspawn().stream().filter(entity -> entity.getUniqueEntityId() == packet.getUniqueEntityId()).findFirst();
        entity1.ifPresent(entity -> {
            player.getEntitysUnspawn().remove(entity);
        });
        Optional<AddEntityPacket> entity2 = player.getEntitysSpawned().stream().filter(entity -> entity.getUniqueEntityId() == packet.getUniqueEntityId()).findFirst();
        entity2.ifPresent(entity -> {
            player.getEntitysSpawned().remove(entity);
        });
        Optional<AddPlayerPacket> entity3 = player.getPlayersUnspawn().stream().filter(entity -> entity.getUniqueEntityId() == packet.getUniqueEntityId()).findFirst();
        entity3.ifPresent(entity -> {
            player.getPlayersUnspawn().remove(entity);
        });
        Optional<AddPlayerPacket> entity4 = player.getPlayersSpawned().stream().filter(entity -> entity.getUniqueEntityId() == packet.getUniqueEntityId()).findFirst();
        entity4.ifPresent(entity -> {
            player.getPlayersSpawned().remove(entity);
        });
        player.getClassicSession().send(new ServerDespawnPlayerPacket((int) packet.getRuntimeEntityId()));
    }
}
