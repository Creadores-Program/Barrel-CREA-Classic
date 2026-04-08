package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class TextPacket implements BedrockPacketTranslator {

    private static final String patternPosibleTrans = ".*[a-zA-Z0-9]+\\.[a-zA-Z0-9]+.*";
    private static final String SPOT = ".";

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.TextPacket packet = (org.cloudburstmc.protocol.bedrock.packet.TextPacket) pk;

        String msg = packet.getMessage();
        org.cloudburstmc.protocol.bedrock.packet.TextPacket.Type typeMsg = packet.getType();
        if(packet.isNeedsTranslation() || typeMsg == org.cloudburstmc.protocol.bedrock.packet.TextPacket.Type.TRANSLATION || (msg.contains(SPOT) && msg.matches(patternPosibleTrans))){
            msg = ProxyServer.getInstance().getLangManager().translate(msg, packet.getParameters());
        }

        switch (packet.getType()) {
            case TIP:
            case POPUP:
            case JUKEBOX_POPUP: {
                player.sendTip(msg);
                break;
            }
            default: {
                player.sendMessage(msg);
                break;
            }
        }
    }
}
