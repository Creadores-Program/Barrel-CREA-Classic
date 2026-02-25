package org.barrelmc.barrel.network.translator.classic;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientExtInfoPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerIdentificationPacket;
import com.github.steveice10.mc.classic.protocol.data.game.UserType;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class ExtInfoPacket implements ClassicPacketTranslator {

    @Override
    public void translate(Packet pk, Player player){
        ClientExtInfoPacket packet = (ClientExtEntryPacket) pk;
        player.setExtSize((int) packet.getExtensionCount());
        if(player.getExtSize() < 1){
            player.getClassicSession().send(new ServerIdentificationPacket(ProxyServer.getInstance().getConfig().getMotd(), ProxyServer.getInstance().getConfig().getMotd(), UserType.NOT_OP));
            player.startSendingPing();
        }
    }
}
