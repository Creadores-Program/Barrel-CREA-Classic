package org.barrelmc.barrel.network.translator.classic;
import org.barrelmc.barrel.network.translator.interfaces.ClassicPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.Utils;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.mc.classic.protocol.packet.server.ServerSetBlockPacket;
import com.github.steveice10.mc.classic.protocol.packet.client.ClientSetBlockPacket;
import com.github.steveice10.mc.classic.protocol.data.game.SetBlockMode;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.ItemUseTransaction;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.math.vector.Vector3f;
public class SetBlockPacket implements ClassicPacketTranslator{
  @Override
  public void translate(Packet pk, Player player) {
    ClientSetBlockPacket packet = (ClientSetBlockPacket) pk;
    if(player.getGameMode() != GameType.CREATIVE || packet.getMode() == SetBlockMode.CREATED || player.getStartGamePacketCache().getAuthoritativeMovementMode() == AuthoritativeMovementMode.CLIENT){
      player.getClassicSession().send(new ServerSetBlockPacket(packet.getX(), packet.getY(), packet.getZ(), ((int) player.getMapClassic().get((packet.getY() * Player.WORLDLEN + packet.getZ()) * Player.WORLDLEN + packet.getX()))));
      return;
    }
    player.getMapClassic().put((packet.getY() * Player.WORLDLEN + packet.getZ()) * Player.WORLDLEN + packet.getX(), (byte) 0);
    player.getPlayerAuthInputData().add(PlayerAuthInputData.PERFORM_ITEM_INTERACTION);
    ItemUseTransaction itemUseTransaction = new ItemUseTransaction();
    itemUseTransaction.setActionType(ACTION_BREAK_BLOCK);
    int bedrockX = Utils.mapCoords(packet.getX(), player.getMinPosClassic().getX(), player.getMaxPosClassic().getX(), player.getMinPosBedrock().getX(), player.getMaxPosBedrock().getX());
    int bedrockY = packet.getY();
    int bedrockZ = Utils.mapCoords(packet.getZ(), player.getMinPosClassic().getZ(), player.getMaxPosClassic().getZ(), player.getMinPosBedrock().getZ(), player.getMaxPosBedrock().getZ());
    Vector3i pos = Vector3i.from(bedrockX, bedrockY, bedrockZ);
    itemUseTransaction.setBlockPosition(pos);
    itemUseTransaction.setItemInHand(ItemData.AIR);
    itemUseTransaction.setBlockFace(0);
    itemUseTransaction.setPlayerPosition(player.getVector3f());
    itemUseTransaction.setBlockDefinition(() -> 0);
    itemUseTransaction.setClickPosition(Vector3f.ZERO);
    itemUseTransaction.setHotbarSlot(player.getHotbarSlot());
    itemUseTransaction.setLegacyRequestId(0);
    itemUseTransaction.setUsingNetIds(false);
    player.setPlayerAuthInputItemUseTransaction(itemUseTransaction);
  }
}
