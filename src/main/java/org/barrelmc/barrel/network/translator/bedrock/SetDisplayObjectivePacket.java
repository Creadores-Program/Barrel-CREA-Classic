package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;

public class SetDisplayObjectivePacket implements BedrockPacketTranslator {
    private static final String SIDEBAR = "sidebar";
    @Override
    public void translate(BedrockPacket pk, Player player){
        if(!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
            return;
        }
        org.cloudburstmc.protocol.bedrock.packet.SetDisplayObjectivePacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetDisplayObjectivePacket) pk;
        if(!SIDEBAR.equals(packet.getDisplaySlot())){
            return;
        }
        String name = Utils.lengthCutter(packet.getDisplayName(), 64);
        String scoreId = packet.getObjectiveId();
        if(player.getScoreId() == null || !scoreId.equals(player.getScoreId())){
            player.setScoreId(packet.getObjectiveId());
        }
        player.sendMessage(name, PlayerIds.STATUS3);
    }
}
