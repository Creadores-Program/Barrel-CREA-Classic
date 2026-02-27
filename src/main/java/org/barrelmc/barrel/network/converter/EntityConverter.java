package org.barrelmc.barrel.network.converter;

import com.github.steveice10.mc.classic.protocol.data.game.EntityIds;
public class EntityConverter{
  public static String bedrockRuntimeToClassicStateId(int entityType, String identifier){
    switch(entityType){
      case 10:
      case 122:
      case 132:
      case 30:
        return EntityIds.CHICKEN;
      case 33:
        return EntityIds.CREEPER;
      case 14:
      case 121:
        return EntityIds.CROCODILE;
      case 12:
        return EntityIds.PIG;
      case 13:
        return EntityIds.SHEEP;
      case 34:
      case 46:
      case 48:
      case 52:
        return EntityIds.SKELETON;
      case 35:
        return EntityIds.SPIDER;
      case 32:
      case 36:
      case 44:
      case 47:
      case 116:
      case 123:
      case 127:
      case 110:
        return EntityIds.ZOMBIE;
      case 105:
      case 39:
      case 55:
      case 134:
        return EntityIds.CHIBI;
      case 63:
        return identifier.replace("minecraft:");
      case 65:
        return "46";
      case 66:
        if(identifier.contains("gravel")){
          return "13";
        }else{
          return "12";
        }
      case 91:
      case 89:
        return EntityIds.HEAD;
    }
    if(identifier.contains(EntityIds.CHICKEN)){
      return EntityIds.CHICKEN;
    }else if(identifier.contains(EntityIds.CREEPER)){
      return EntityIds.CREEPER;
    }else if(identifier.contains(EntityIds.SHEEP)){
      return EntityIds.SHEEP;
    }else if(identifier.contains(EntityIds.SKELETON)){
      return EntityIds.SKELETON;
    }else if(identifier.contains(EntityIds.SPIDER)){
      return EntityIds.SPIDER;
    }else if(identifier.contains(EntityIds.ZOMBIE)){
      return EntityIds.ZOMBIE;
    }else if(identifier.contains(EntityIds.PIG)){
      return EntityIds.PIG;
    }else if(identifier.contains(EntityIds.CROCODILE)){
      return EntityIds.CROCODILE;
    }else{
      return identifier.replace("minecraft:");
    }
  }
}
