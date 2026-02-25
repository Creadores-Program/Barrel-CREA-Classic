package org.barrelmc.barrel.network.translator.classic;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientExtEntryPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerIdentificationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerCustomBlockSupportLevelPacket;
import com.github.steveice10.mc.classic.protocol.data.game.UserType;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;

public class ExtEntryPacket implements ClassicPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        ClientExtEntryPacket packet = (ClientExtEntryPacket) pk;
        player.getExtensionsClassic().add(packet);
        if(player.getExtSize() < player.getExtensionsClassic().size()){
            return;
        }
        if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(1), player.getExtensionsClassic())){
            player.getClassicSession().send(new ServerCustomBlockSupportLevelPacket(1));
            return;
        }
        player.getClassicSession().send(new ServerIdentificationPacket(ProxyServer.getInstance().getConfig().getMotd(), ProxyServer.getInstance().getConfig().getMotd(), UserType.NOT_OP));
        player.startSendingPing();
    }
}
