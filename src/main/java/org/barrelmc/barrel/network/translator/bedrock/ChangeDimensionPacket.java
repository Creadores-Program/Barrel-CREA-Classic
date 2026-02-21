package org.barrelmc.barrel.network.translator.bedrock;

import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.player.StatusWorld;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.math.vector.Vector3i;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvColorsPacket;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerEnvSetWeatherTypePacket;
//import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
//import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

public class ChangeDimensionPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {
        player.setStatusWorld(StatusWorld.CHANGE_DIMENSION);
        org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket packet = (org.cloudburstmc.protocol.bedrock.packet.ChangeDimensionPacket) pk;
        switch(packet.getDimension()){
            case 0://Overworld
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvColorsPacket(/*aqui*/));
                }
                break;
            case 1://Nether
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvColorsPacket(/*aqui*/));
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvSetWeatherTypePacket(0));
                }
                break;
            case 2://End
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvColorsPacket(/*aqui*/));
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvSetWeatherTypePacket(0));
                }
                break;
        }
        /*
        PlayerActionPacket playerActionPacket = new PlayerActionPacket();
        playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS);
        playerActionPacket.setBlockPosition(Vector3i.ZERO);
        playerActionPacket.setResultPosition(Vector3i.ZERO);
        playerActionPacket.setFace(0);
        playerActionPacket.setRuntimeEntityId(player.getRuntimeEntityId());
        player.getBedrockClientSession().sendPacket(playerActionPacket);*/
    }
}
