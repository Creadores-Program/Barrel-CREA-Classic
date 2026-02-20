/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.barrelmc.barrel.network;

import com.github.steveice10.mc.classic.protocol.packet.client.ClientIdentificationPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;

import java.util.UUID;

public class ClassicPacketHandler extends SessionAdapter {

    private Player player = null;

    @Override
    public void packetSent(Session session, Packet packet) {
        //System.out.println("Sent Java " + packet.toString());
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        //System.out.println("Received Java " + packet.toString());
        if (this.player == null) {
            if (packet instanceof ClientIdentificationPacket) {
                ClientIdentificationPacket loginPacket = (ClientIdentificationPacket) packet;
                new Player(loginPacket, session);
                this.player = ProxyServer.getInstance().getPlayerByName(loginPacket.getUsername());
            }
        } else {
            player.getPacketTranslatorManager().translate(packet);
        }
    }
}
