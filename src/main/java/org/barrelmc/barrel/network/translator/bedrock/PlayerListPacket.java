package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddPlayerNamePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtRemovePlayerNamePacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class PlayerListPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        if(!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
            return;
        }
        org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket packet = (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket) pk;
        switch (packet.getAction()) {
            case ADD: {
                for (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket.Entry entry : packet.getEntries()) {
                    player.getClassicSession().send(new ServerExtAddPlayerNamePacket(((short) entry.getEntityId()), String.valueOf(entry.getName()).replace('§', '&'), String.valueOf(entry.getName()).replace('§', '&'), "Bedrock", 0));
                }
                break;
            }
            case REMOVE: {
                for (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket.Entry entry : packet.getEntries()) {
                    player.getClassicSession().send(new ServerExtRemovePlayerNamePacket(((short) entry.getEntityId())));
                }
                break;
            }
        }
    }
}
