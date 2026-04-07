package org.barrelmc.barrel.network.converter;

import java.util.Map;
import static java.util.Map.entry;
import java.util.concurrent.ConcurrentHashMap;

import com.github.steveice10.mc.classic.protocol.data.game.EntityIds;
public class EntityConverter{
  private static final Map<String, int[]> ENTITYSIDSTR = new ConcurrentHashMap<>(Map.ofEntries(
    entry(EntityIds.CHICKEN, new int[]{ 10, 122, 132, 30 }),
    entry(EntityIds.CREEPER, new int[]{ 33 }),
    entry(EntityIds.CROCODILE, new int[]{ 14, 121 }),
    entry(EntityIds.PIG, new int[]{ 12 }),
    entry(EntityIds.SHEEP, new int[]{ 13 }),
    entry(EntityIds.SKELETON, new int[]{ 34, 46, 48, 52 }),
    entry(EntityIds.SPIDER, new int[]{ 35 }),
    entry(EntityIds.ZOMBIE, new int[]{ 32, 36, 44, 47, 116, 123, 127, 110 }),
    entry(EntityIds.CHIBI, new int[]{ 105, 39, 55, 134 }),
    entry("46", new int[]{ 65 }),
    entry(EntityIds.HEAD, new int[]{ 91, 89 })
  ));
  private static final String gravelB = "gravel";
  private static final String[] gravelSand = new String[]{ "13", "12" };
  private static final String prefixB = ":";
  public static String bedrockRuntimeToClassicStateId(int entityType, String identifier){
    String name = identifier;
    if (identifier.contains(prefixB)) {
      name = identifier.substring(identifier.indexOf(prefixB) + 1);
    }
    if(entityType == 63){
      return name;
    }else if(entityType == 66){
      if(name.contains(gravelB)){
        return gravelSand[0];
      }else{
        return gravelSand[1];
      }
    }
    for(Map.Entry<String, int[]> classicIdEntry : ENTITYSIDSTR.entrySet()){
      int[] value = classicIdEntry.getValue();
      for(int num : value){
        if(entityType == num){
          return classicIdEntry.getKey();
        }
      }
    }
    if(name.contains(EntityIds.CHICKEN)){
      return EntityIds.CHICKEN;
    }else if(name.contains(EntityIds.CREEPER)){
      return EntityIds.CREEPER;
    }else if(name.contains(EntityIds.SHEEP)){
      return EntityIds.SHEEP;
    }else if(name.contains(EntityIds.SKELETON)){
      return EntityIds.SKELETON;
    }else if(name.contains(EntityIds.SPIDER)){
      return EntityIds.SPIDER;
    }else if(name.contains(EntityIds.ZOMBIE)){
      return EntityIds.ZOMBIE;
    }else if(name.contains(EntityIds.PIG)){
      return EntityIds.PIG;
    }else if(name.contains(EntityIds.CROCODILE)){
      return EntityIds.CROCODILE;
    }else{
      return name;
    }
  }
}
