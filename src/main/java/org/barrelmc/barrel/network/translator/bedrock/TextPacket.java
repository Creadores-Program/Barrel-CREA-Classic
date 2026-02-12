package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class TextPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.TextPacket packet = (org.cloudburstmc.protocol.bedrock.packet.TextPacket) pk;

        switch (packet.getType()) {
            case TIP:
            case POPUP: {
                player.sendTip(packet.getMessage());
                break;
            }
            case TRANSLATION: {
                if(player.getTraslateAd() != "true"){
                    player.setTraslateAd("true");
                    player.sendMessage("&cChat translation not implemented! Force language on Minecraft bedrock server");
                }
            }
            default: {
                player.sendMessage(packet.getMessage());
                break;
            }
        }
    }
}
