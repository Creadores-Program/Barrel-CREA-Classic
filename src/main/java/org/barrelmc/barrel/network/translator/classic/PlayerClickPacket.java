package org.barrelmc.barrel.network.translator.classic;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket;
import org.cloudburstmc.math.vector.Vector3f;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientPlayerClickPacket;
public class PlayerClickPacket implements ClassicPacketTranslator{
    @Override
    public void translate(Packet pk, Player player){
        ClientPlayerClickPacket packet = (ClientPlayerClickPacket) pk;
        if(packet.getAction() == 1 || packet.getButton() == 2){
            return;
        }
        AnimatePacket anim = new AnimatePacket();
        anim.setAction(AnimatePacket.Action.SWING_ARM);
        anim.setRuntimeEntityId(player.getRuntimeEntityId());
        player.getBedrockClientSession().sendPacket(anim);
        if(packet.getTargetEntityID() < 0){
            return;
        }
        InteractPacket bedrockpacket = new InteractPacket();
        bedrockpacket.setRuntimeEntityId((long) packet.getTargetEntityID());
        switch(packet.getButton()){
            case 0:
                bedrockpacket.setAction(InteractPacket.Action.DAMAGE);
                break;
            case 1:
                bedrockpacket.setAction(InteractPacket.Action.INTERACT);
                break;
        }
        bedrockpacket.setMousePosition(Vector3f.ZERO);
        player.getBedrockClientSession().sendPacket(bedrockpacket);
    }
}
