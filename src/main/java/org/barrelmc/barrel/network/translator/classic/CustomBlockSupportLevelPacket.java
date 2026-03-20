package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientCustomBlockSupportLevelPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerIdentificationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelInitializePacket;
import com.github.steveice10.mc.classic.protocol.data.game.UserType;
public class CustomBlockSupportLevelPacket implements ClassicPacketTranslator{
    @Override
    public void translate(Packet pk, Player player) {
        ClientCustomBlockSupportLevelPacket packet = (ClientCustomBlockSupportLevelPacket) pk;
        player.setCustomBlocksLevel(packet.getSupportLevel());
        player.getClassicSession().send(new ServerIdentificationPacket(ProxyServer.getInstance().getConfig().getMotd(), ProxyServer.getInstance().getConfig().getMotd(), UserType.NOT_OP));
        player.getClassicSession().send(new ServerLevelInitializePacket());
        player.startSendingPing();
    }
}
