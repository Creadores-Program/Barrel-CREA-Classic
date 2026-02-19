package org.barrelmc.barrel.network.translator.bedrock;

import com.github.steveice10.mc.classic.protocol.packet.server.ServerHackControlPacket;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import org.barrelmc.barrel.server.ProxyServer;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.AbilityLayer;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.Set;

public class UpdateAbilitiesPacket implements BedrockPacketTranslator {
    @Override
    public void translate(BedrockPacket pk, Player player) {
        org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket packet = (org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket) pk;

        if (packet.getUniqueEntityId() == player.getRuntimeEntityId()) {
            for (AbilityLayer abilityLayer : packet.getAbilityLayers().toArray(new AbilityLayer[0])) {
                if (abilityLayer.getLayerType() == AbilityLayer.Type.BASE) {
                    Set<Ability> abilityValues = abilityLayer.getAbilityValues();
                    if(Utils.containsExt(ProxyServer.getInstance().getExtDatapacks().get(6), player.getExtensionsClassic())){
                        player.getClassicSession().send(new ServerHackControlPacket(abilityValues.contains(Ability.FLYING), abilityValues.contains(Ability.NO_CLIP), abilityValues.contains(Ability.WALK_SPEED), false, true, 40f));
                    }
                }
            }
        }
    }
}
