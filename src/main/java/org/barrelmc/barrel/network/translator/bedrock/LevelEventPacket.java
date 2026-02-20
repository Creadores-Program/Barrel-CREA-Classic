package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvSetWeatherTypePacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.server.ProxyServer;
import org.barrelmc.barrel.utils.Utils;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class LevelEventPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket packet = (org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket) pk;

        if(!Utils.containsExt(ProxyServer.getExtDatapacks().get(5), player.getExtensionsClassic())){
            return;
        }

        switch(packet.getType()){
            case LevelEvent.START_RAINING:
            case LevelEvent.START_THUNDERSTORM:
                break;
            case LevelEvent.STOP_RAINING:
            case LevelEvent.STOP_THUNDERSTORM:
                break;
        }
    }
}
