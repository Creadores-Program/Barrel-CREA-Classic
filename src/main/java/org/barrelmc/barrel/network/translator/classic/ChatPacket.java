package org.barrelmc.barrel.network.translator.classic;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientChatPacket;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.cloudburstmc.protocol.bedrock.packet.CommandRequestPacket;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import java.util.UUID;

public class ChatPacket implements ClassicPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        ClientChatPacket chatPacket = (ClientChatPacket) pk;
        player.msgPlayer += chatPacket.getMessage();
        if(chatPacket.getUnused() == 1){
            return;
        }
        if(player.msgPlayer.startsWith("/")){
            CommandRequestPacket commandPacket = new CommandRequestPacket();
            commandPacket.setVersion(ProxyServer.getInstance().getBedrockPacketCodec().getProtocolVersion());
            commandPacket.setCommand("?"+player.msgPlayer.substring(1));
            CommandOriginData Cod = new CommandOriginData(CommandOriginType.PLAYER, UUID.fromString(player.getUUID()), player.getUUID(), 0);
            commandPacket.setCommandOriginData(Cod);
            player.msgPlayer = "";
            player.getBedrockClientSession().sendPacket(commandPacket);
            return;
        }
        TextPacket textPacket = new TextPacket();

        textPacket.setType(TextPacket.Type.CHAT);
        textPacket.setNeedsTranslation(false);
        textPacket.setSourceName(player.msgPlayer);
        textPacket.setMessage(player.msgPlayer);
        textPacket.setXuid("");
        textPacket.setPlatformChatId("");
        player.msgPlayer = "";
        player.getBedrockClientSession().sendPacket(textPacket);
    }
}
