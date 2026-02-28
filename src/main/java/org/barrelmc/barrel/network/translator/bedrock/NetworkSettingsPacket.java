package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class NetworkSettingsPacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket packet = (org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket) pk;
        player.getBedrockClientSession().setCompression(packet.getCompressionAlgorithm());

        player.getBedrockClientSession().sendPacketImmediately(player.getLoginPacket());
    }
}
