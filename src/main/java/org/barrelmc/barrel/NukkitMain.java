package org.barrelmc.barrel;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.event.EventHandler;
import cn.nukkit.network.protocol.TextPacket;
import org.barrelmc.barrel.network.converter.BlockConverter;
import org.barrelmc.barrel.server.ProxyServer;
import java.io.File;
import lombok.Getter;
import java.util.List;
import java.util.ArrayList;

public class NukkitMain extends PluginBase implements Listener{
  private String data_pathClassic;
  @Getter
  private ProxyServer classicServer;
  @Getter
  private static NukkitMain instance;
  
  private Thread threadClassicServer;
  @Override
  public void onEnable(){
    this.getLogger().info("§eLoading...");
    this.getLogger().info("Starting Barrel Proxy CREA Classic software");
    this.getLogger().info("Barrel CREA Edition is distributed under the MIT License");
    getDataFolder().mkdir();
    this.data_pathClassic = getDataFolder().getAbsolutePath();
    File fileR = new File(getDataFolder(), "config.yml");
    if(!fileR.exists()){
      saveResource("config.yml");
    }
    this.threadClassicServer = new Thread(()->{
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
      BlockConverter.init();
      this.classicServer = new ProxyServer(data_pathClassic);
    });
    this.threadClassicServer.start();
    NukkitMain.instance = this;
  }
  public boolean isClassicPlayer(Player player){
    return player.getLoginChainData().getDeviceModel() == "Barrel CREA Classic" && player.getLoginChainData().getDeviceOS() == 7;
  }
  public List<Player> getClassicPlayers(){
    List<Player> classicPlayers = new ArrayList<>();
    for(Player player : this.getServer().getOnlinePlayers().values()){
      if(this.isClassicPlayer(player)){
        classicPlayers.add(player);
      }
    }
    return classicPlayers;
  }
  @EventHandler
  public void onDataPacketSendEvent(DataPacketSendEvent event){
    Player player = event.getPlayer();
    if(getServer().isLanguageForced() || player == null || !(event.getPacket() instanceof TextPacket) || !this.isClassicPlayer(player) || event.isCancelled()){
      return;
    }
    TextPacket packet = (TextPacket) event.getPacket();
    if(packet.type != TextPacket.TYPE_TRANSLATION){
      return;
    }
    event.setCancelled(true);
    TextPacket pk = new TextPacket();
    pk.type = TextPacket.TYPE_RAW;
    pk.message = getServer().getLanguage().translateString(packet.message, packet.parameters);
    player.dataPacket(pk);
  }
}
