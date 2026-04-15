package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class RemoveObjectivePacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player){
        if(!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
            return;
        }
        org.cloudburstmc.protocol.bedrock.packet.RemoveObjectivePacket packet = (org.cloudburstmc.protocol.bedrock.packet.RemoveObjectivePacket) pk;
        if(!packet.getObjectiveId().equals(player.getScoreId())){
            return;
        }
        player.clearScore();
    }    
}
