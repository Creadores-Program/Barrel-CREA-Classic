package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvSetWeatherTypePacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class LevelEventPacket implements BedrockPacketTranslator {
    private static final ServerEnvSetWeatherTypePacket RAIN = new ServerEnvSetWeatherTypePacket(1);
    public static final ServerEnvSetWeatherTypePacket CLEAR = new ServerEnvSetWeatherTypePacket(0);

    @Override
    public void translate(BedrockPacket pk, Player player) {
        if(player.getDimension() != 0 || (!Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic()))){
            return;
        }
        org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket packet = (org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket) pk;

        switch((LevelEvent) packet.getType()){
            case START_RAINING:
            case START_THUNDERSTORM:
                player.getEnvCpe().setWeather(RAIN);
                break;
            case STOP_RAINING:
            case STOP_THUNDERSTORM:
                player.getEnvCpe().setWeather(CLEAR);
                break;
            default:
                break;
        }
    }
}
