package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtAddPlayerNamePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtRemovePlayerNamePacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.utils.nukkit.TextFormat;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class PlayerListPacket implements BedrockPacketTranslator {

    private static final String rolUser = "Bedrock";
    @Override
    public void translate(BedrockPacket pk, Player player) {
        if(!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(2), player.getExtensionsClassic())){
            return;
        }
        org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket packet = (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket) pk;
        switch (packet.getAction()) {
            case ADD: {
                for (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket.Entry entry : packet.getEntries()) {
                    long entityRId = entry.getEntityId();
                    String name = TextFormat.colorizeToCc(String.valueOf(entry.getName()));
                    player.getClassicSession().send(new ServerExtAddPlayerNamePacket(((short) entityRId), name, name, rolUser, 0));
                }
                break;
            }
            case REMOVE: {
                for (org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket.Entry entry : packet.getEntries()) {
                    long entityRId = entry.getEntityId();
                    player.getClassicSession().send(new ServerExtRemovePlayerNamePacket(((short) entityRId)));
                }
                break;
            }
        }
    }
}
