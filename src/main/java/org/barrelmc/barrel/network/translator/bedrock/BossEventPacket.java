package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.utils.nukkit.TextFormat;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import com.github.steveice10.mc.classic.protocol.data.game.PlayerIds;

public class BossEventPacket implements BedrockPacketTranslator {
    private static final String PREFIX = "-- ";
    private static final String POR = "%";
    private static final String COLOR = TextFormat.ESCAPE_CLASSIC_SERVER+"d";
    @Override
    public void translate(BedrockPacket pk, Player player){
        if(!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(8), player.getExtensionsClassic())){
            return;
        }
        org.cloudburstmc.protocol.bedrock.packet.BossEventPacket packet = (org.cloudburstmc.protocol.bedrock.packet.BossEventPacket) pk;
        if(packet.getAction() == org.cloudburstmc.protocol.bedrock.packet.BossEventPacket.Action.REMOVE && packet.getBossUniqueEntityId() == player.getBossbarId()){
            player.clearBoss();
            return;
        }
        if(packet.getBossUniqueEntityId() != player.getBossbarId()){
            player.setBossbarId(packet.getBossUniqueEntityId());
        }
        String rawTitle = packet.getTitle();
        if(rawTitle == null && player.getBossbarTitle() != null){
            rawTitle = player.getBossbarTitle();
        }else if(player.getBossbarTitle() == null && rawTitle == null){
            return;
        }
        if(player.getBossbarTitle() == null || !player.getBossbarTitle().equals(rawTitle)){
            player.setBossbarTitle(TextFormat.colorizeToCc(Utils.lengthCutter(rawTitle, 55)));
            rawTitle = player.getBossbarTitle();
        }
        String title = COLOR + rawTitle + PREFIX + ((int) packet.getHealthPercentage()) + POR;
        player.sendMessage(title, PlayerIds.STATUS2);
    }
}
