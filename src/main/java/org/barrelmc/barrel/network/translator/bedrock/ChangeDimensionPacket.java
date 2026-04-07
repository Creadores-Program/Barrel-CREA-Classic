package org.barrelmc.barrel.network.translator.bedrock;

import java.util.Map;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Entity;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.math.vector.Vector3i;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerDespawnPlayerPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvColorsPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class ChangeDimensionPacket implements BedrockPacketTranslator {
    private static final ServerEnvColorsPacket[] OVERWORLD_COLORS = {
        new ServerEnvColorsPacket(0, 153, 204, 255),
        new ServerEnvColorsPacket(2, 153, 204, 255),
        new ServerEnvColorsPacket(3, 153, 153, 153),
        new ServerEnvColorsPacket(4, 255, 255, 255),
        new ServerEnvColorsPacket(1, 255, 255, 255),
        new ServerEnvColorsPacket(5, 255, 255, 255)
    };

    private static final ServerEnvColorsPacket[] NETHER_COLORS = {
        new ServerEnvColorsPacket(0, 30, 5, 5),
        new ServerEnvColorsPacket(2, 50, 10, 10),
        new ServerEnvColorsPacket(3, 80, 40, 40),
        new ServerEnvColorsPacket(4, 180, 50, 40),
        new ServerEnvColorsPacket(1, 0, 0, 0),
        new ServerEnvColorsPacket(5, 40, 10, 10)
    };

    private static final ServerEnvColorsPacket[] END_COLORS = {
        new ServerEnvColorsPacket(0, 10, 10, 15),
        new ServerEnvColorsPacket(2, 20, 10, 25),
        new ServerEnvColorsPacket(3, 60, 55, 70),
        new ServerEnvColorsPacket(4, 200, 190, 150),
        new ServerEnvColorsPacket(1, 0, 0, 0),
        new ServerEnvColorsPacket(5, 5, 5, 10)
    };

    @Override
    public void translate(BedrockPacket pk, Player player) {
        player.setStatusWorld(StatusWorld.CHANGE_DIMENSION);
        org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket packet = (org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket) pk;
        player.setMaxPosBedrock(Vector3i.from(((int) Math.round(packet.getPosition().getX() + 127)), 255, ((int) Math.round(packet.getPosition().getZ() + 127))));
        player.setMinPosBedrock(Vector3i.from(((int) Math.round(packet.getPosition().getX() + -128)), 0, ((int) Math.round(packet.getPosition().getZ() + -128))));
        Map<Long, Entity> entitysSpawned = player.getEntitysSpawned();
        for(Entity entity : entitysSpawned.values()){
            player.getClassicSession().send(new ServerDespawnPlayerPacket((int) entity.rEId));
        }
        entitysSpawned.clear();
        player.getEntitysUnspawn().clear();
        player.getEntitysIndex().clear();
        switch(packet.getDimension()){
            case 0://Overworld
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getEnvCpe().updateAmbient(OVERWORLD_COLORS[0], OVERWORLD_COLORS[1], OVERWORLD_COLORS[2], OVERWORLD_COLORS[3]);
                    player.getEnvCpe().updateDimention(OVERWORLD_COLORS[4], OVERWORLD_COLORS[5]);
                }
                break;
            case 1://Nether
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getEnvCpe().updateAmbient(NETHER_COLORS[0], NETHER_COLORS[1], NETHER_COLORS[2], NETHER_COLORS[3]);
                    player.getEnvCpe().updateDimention(NETHER_COLORS[4], NETHER_COLORS[5]);
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getEnvCpe().setWeather(LevelEventPacket.CLEAR);
                }
                break;
            case 2://End
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getEnvCpe().updateAmbient(END_COLORS[0], END_COLORS[1], END_COLORS[2], END_COLORS[3]);
                    player.getEnvCpe().updateDimention(END_COLORS[4], END_COLORS[5]);
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getEnvCpe().setWeather(LevelEventPacket.CLEAR);
                }
                break;
        }
        player.getClassicSession().send(Utils.INITCCWORPK);
        player.setOldPosition(player.getVector3f());
        player.setPosition(packet.getPosition());
        player.setLastServerPosition(packet.getPosition());
    }
}
