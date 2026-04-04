/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.network;

import com.github.steveice10.mc.classic.protocol.packet.client.ClientIdentificationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtInfoPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerExtEntryPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerIdentificationPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelInitializePacket;
import com.github.steveice10.mc.classic.protocol.ClassicConstants;
import com.github.steveice10.mc.classic.protocol.data.game.UserType;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;

public class ClassicPacketHandler extends SessionAdapter {

    private Player player = null;

    private static final String serverSoftware = "Barrel Crea Classic";

    @Override
    public void packetSent(PacketSentEvent event) {
        //System.out.println("Sent Java " + event.getPacket().toString());
    }

    @Override
    public void disconnecting(DisconnectingEvent event){
        if(event.getCause() == null){
            return;
        }
        ProxyServer.getInstance().getLogger().error("An error occurred!", event.getCause());
    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        Session session = event.getSession();
        Packet packet = event.getPacket();
        //System.out.println("Received Classic " + packet.toString());
        if (this.player == null) {
            if (packet instanceof ClientIdentificationPacket) {
                ClientIdentificationPacket loginPacket = (ClientIdentificationPacket) packet;
                session.setFlag(ClassicConstants.USERNAME_KEY, loginPacket.getUsername());
                ProxyServer.getInstance().getLogger().info(session.getFlag(ClassicConstants.USERNAME_KEY) + " logged in");
                new Player(loginPacket, session);
                this.player = ProxyServer.getInstance().getPlayerByName(loginPacket.getUsername());
                if(loginPacket.isCPE()){
                    player.getClassicSession().send(new ServerExtInfoPacket(serverSoftware, ((short) ProxyServer.getInstance().getExtDatapacks().size())));
                    for(ServerExtEntryPacket extS : ProxyServer.getInstance().getExtDatapacks()){
                        player.getClassicSession().send(extS);
                    }
                }else{
                    player.getClassicSession().send(new ServerIdentificationPacket(ProxyServer.getInstance().getConfig().getMotd(), ProxyServer.getInstance().getSubMotd(), UserType.NOT_OP));
                    player.getClassicSession().send(new ServerLevelInitializePacket());
                    player.startSendingPing();
                }
            }
        } else {
            player.getPacketTranslatorManager().translate(packet);
        }
    }
}
