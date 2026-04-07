package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;

public class SetPlayerGameTypePacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.SetPlayerGameTypePacket packet = (org.cloudburstmc.protocol.bedrock.packet.SetPlayerGameTypePacket) pk;
        player.setGameMode(GameType.from(packet.getGamemode()));
        if(player.getGameMode() == GameType.SURVIVAL || player.getGameMode() == GameType.ADVENTURE || player.getGameMode() == GameType.DEFAULT){
            player.getClassicSession().send(Player.REACH_SURVIVAL);
        }else{
            player.getClassicSession().send(Player.REACH_CREATIVE);
        }
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
            player.sendMessage(Player.GAMEMODE_STR + player.getGameMode().name().substring(0, 1).toUpperCase() + player.getGameMode().name().substring(1).toLowerCase(), PlayerIds.STATUS1);
        }
    }
}
