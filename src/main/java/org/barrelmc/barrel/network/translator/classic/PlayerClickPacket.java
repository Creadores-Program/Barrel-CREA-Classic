package org.barrelmc.barrel.network.translator.classic;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
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
        InventoryTransactionPacket bepk = new InventoryTransactionPacket();
        bepk.setTransactionType(InventoryTransactionType.ITEM_USE_ON_ENTITY);
        bepk.setRuntimeEntityId((long) packet.getTargetEntityID());
        bepk.setActionType(packet.getButton());
        Vector3f pos = player.getVector3f();
        bepk.setPlayerPosition(pos);
        bepk.setHotbarSlot(player.getHotbarSlot());
        bepk.setItemInHand(ItemData.AIR);
        bepk.setHeadPosition(pos);
        bepk.setClickPosition(Vector3f.ZERO);
        player.getBedrockClientSession().sendPacket(bepk);
    }
}
