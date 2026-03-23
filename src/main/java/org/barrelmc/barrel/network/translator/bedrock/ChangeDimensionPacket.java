package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.math.vector.Vector3i;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvColorsPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvSetWeatherTypePacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerLevelInitializePacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class ChangeDimensionPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        player.setStatusWorld(StatusWorld.CHANGE_DIMENSION);
        org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket packet = (org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket) pk;
        player.setMaxPosBedrock(Vector3i.from(((int) Math.round(packet.getPosition().getX() + 127)), 255, ((int) Math.round(packet.getPosition().getZ() + 127))));
        player.setMinPosBedrock(Vector3i.from(((int) Math.round(packet.getPosition().getX() + -128)), 0, ((int) Math.round(packet.getPosition().getZ() + -128))));
        player.getEntitysSpawned().clear();
        player.getEntitysUnspawn().clear();
        player.getPlayersSpawned().clear();
        player.getPlayersUnspawn().clear();
        switch(packet.getDimension()){
            case 0://Overworld
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getEnvCpe().updateAmbient(new ServerEnvColorsPacket(0, 153, 204, 255), new ServerEnvColorsPacket(2, 153, 204, 255), new ServerEnvColorsPacket(3, 153, 153, 153), new ServerEnvColorsPacket(4, 255, 255, 255));
                    player.getEnvCpe().updateDimention(new ServerEnvColorsPacket(1, 255, 255, 255), new ServerEnvColorsPacket(5, 255, 255, 255));
                }
                break;
            case 1://Nether
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getEnvCpe().updateAmbient(new ServerEnvColorsPacket(0, 30, 5, 5), new ServerEnvColorsPacket(2, 50, 10, 10), new ServerEnvColorsPacket(3, 80, 40, 40), new ServerEnvColorsPacket(4, 180, 50, 40));
                    player.getEnvCpe().updateDimention(new ServerEnvColorsPacket(1, 0, 0, 0), new ServerEnvColorsPacket(5, 40, 10, 10));
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getEnvCpe().setWeather(new ServerEnvSetWeatherTypePacket(0));
                }
                break;
            case 2://End
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getEnvCpe().updateAmbient(new ServerEnvColorsPacket(0, 10, 10, 15), new ServerEnvColorsPacket(2, 20, 10, 25), new ServerEnvColorsPacket(3, 60, 55, 70), new ServerEnvColorsPacket(4, 200, 190, 150));
                    player.getEnvCpe().updateDimention(new ServerEnvColorsPacket(1, 0, 0, 0), new ServerEnvColorsPacket(5, 5, 5, 10));
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getEnvCpe().setWeather(new ServerEnvSetWeatherTypePacket(0));
                }
                break;
        }
        player.getClassicSession().send(new ServerLevelInitializePacket());
        player.setOldPosition(player.getVector3f());
        player.setPosition(packet.getPosition());
        player.setLastServerPosition(packet.getPosition());
    }
}
