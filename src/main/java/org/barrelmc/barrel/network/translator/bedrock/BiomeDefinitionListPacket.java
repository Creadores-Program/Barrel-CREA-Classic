package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
public class BiomeDefinitionListPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        RequestChunkRadiusPacket chunkRadiusPacket = new RequestChunkRadiusPacket();
        chunkRadiusPacket.setRadius(16);
        player.getBedrockClientSession().sendPacketImmediately(chunkRadiusPacket);
    }
}
