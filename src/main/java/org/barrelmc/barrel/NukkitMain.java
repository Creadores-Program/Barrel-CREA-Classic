package org.barrelmc.barrel;
import cn.nukkit.plugin.PluginBase;
import org.barrelmc.barrel.network.converter.BlockConverter;
import org.barrelmc.barrel.network.converter.ItemsConverter;
import org.barrelmc.barrel.server.ProxyServer;
import java.io.File;

public class NukkitMain extends PluginBase{
  private String data_pathJava;
  private ProxyServer javaServer;
  @Override
  public void onEnable(){
    this.getLogger().info("§eLoading...");
    this.getLogger().info("Starting Barrel Proxy CREA Edition software");
    this.getLogger().info("Barrel CREA Edition is distributed under the MIT License");
    BlockConverter.init();
    ItemsConverter.init();
    getDataFolder().mkdir();
    this.data_pathJava = getDataFolder().getAbsolutePath();
    File fileR = new File(getDataFolder(), "config.yml");
    if(!fileR.exists()){
      saveResource("config.yml");
    }
    this.javaServer = new ProxyServer(this.data_pathJava);
  }
}
