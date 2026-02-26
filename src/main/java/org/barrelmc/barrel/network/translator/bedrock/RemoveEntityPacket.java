package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket;

public class RemoveEntityPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket packet = (org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket) pk;
        for(AddEntityPacket entity : player.getEntitysUnspawn()){
            if(entity.getUniqueEntityId() == packet.getUniqueEntityId()){
                player.getEntitysUnspawn().remove(entity);
                break;
            }
        }
        for(AddEntityPacket entity : player.getEntitysSpawned()){
            if(entity.getUniqueEntityId() == packet.getUniqueEntityId()){
                player.getEntitysSpawned().remove(entity);
                break;
            }
        }
        for(AddPlayerPacket entity : player.getPlayersUnspawn()){
            if(entity.getUniqueEntityId() == packet.getUniqueEntityId()){
                player.getPlayersUnspawn().remove(entity);
                break;
            }
        }
        for(AddPlayerPacket entity : player.getPlayersSpawned()){
            if(entity.getUniqueEntityId() == packet.getUniqueEntityId()){
                player.getPlayersSpawned().remove(entity);
                break;
            }
        }
        player.getClassicSession().send(new ServerDespawnPlayerPacket((int) packet.getUniqueEntityId()));
    }
}
