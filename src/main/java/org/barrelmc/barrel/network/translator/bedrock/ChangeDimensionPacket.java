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
                    player.getClassicSession().send(new ServerEnvColorsPacket(0, 153, 204, 255));
                    player.getClassicSession().send(new ServerEnvColorsPacket(1, 255, 255, 255));
                    player.getClassicSession().send(new ServerEnvColorsPacket(2, 153, 204, 255));
                    player.getClassicSession().send(new ServerEnvColorsPacket(3, 153, 153, 153));
                    player.getClassicSession().send(new ServerEnvColorsPacket(4, 255, 255, 255));
                    player.getClassicSession().send(new ServerEnvColorsPacket(5, 255, 255, 255));
                }
                break;
            case 1://Nether
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvColorsPacket(0, 30, 5, 5));
                    player.getClassicSession().send(new ServerEnvColorsPacket(1, 0, 0, 0));
                    player.getClassicSession().send(new ServerEnvColorsPacket(2, 50, 10, 10));
                    player.getClassicSession().send(new ServerEnvColorsPacket(3, 80, 40, 40));
                    player.getClassicSession().send(new ServerEnvColorsPacket(4, 180, 50, 40));
                    player.getClassicSession().send(new ServerEnvColorsPacket(5, 40, 10, 10));
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvSetWeatherTypePacket(0));
                }
                break;
            case 2://End
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(3), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvColorsPacket(0, 10, 10, 15));
                    player.getClassicSession().send(new ServerEnvColorsPacket(1, 0, 0, 0));
                    player.getClassicSession().send(new ServerEnvColorsPacket(2, 20, 10, 25));
                    player.getClassicSession().send(new ServerEnvColorsPacket(3, 60, 55, 70));
                    player.getClassicSession().send(new ServerEnvColorsPacket(4, 200, 190, 150));
                    player.getClassicSession().send(new ServerEnvColorsPacket(5, 5, 5, 10));
                }
                if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(5), player.getExtensionsClassic())){
                    player.getClassicSession().send(new ServerEnvSetWeatherTypePacket(0));
                }
                break;
        }
        player.getClassicSession().send(new ServerLevelInitializePacket());
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
