package org.barrelmc.barrel.server;

import java.util.concurrent.ConcurrentHashMap;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.barrelmc.barrel.Barrel;
import org.barrelmc.barrel.utils.FileManager;

public class LanguageManager {
    private final Map<String, String> translations = new ConcurrentHashMap<>();
    private final String regLin = "\\R";
    private final String commEs = "#";
    private final char setProp = '=';
    private final String placeHol1 = "{%";
    private final String placeHol2 = "}";
    private final String prefixTrans = "%";

    public LanguageManager(){
        InputStream inputStream = Barrel.class.getClassLoader().getResourceAsStream("lang/lang.ini");
        if (inputStream == null) {
            ProxyServer.getInstance().getLogger().error("Lang does not exist!");
            return;
        }
        try{
            parse(FileManager.getFileContents(inputStream));
        }catch(Exception e){
            ProxyServer.getInstance().getLogger().error("Lang Load Error!", e);
        }
    }

    private void parse(String content) {
        if (content == null || content.isEmpty()) return;

        String[] lines = content.split(regLin);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith(commEs)) continue;

            int sep = line.indexOf(setProp);
            if (sep != -1) {
                String key = line.substring(0, sep).trim();
                String value = line.substring(sep + 1).trim();
                translations.put(key, value);
            }
        }
    }

    public String translate(String text, List<String> args){
        if(translations.containsKey(text)){
            return translateKey(text, args);
        }
        for(String key : translations.keySet()){
            if(text.contains(prefixTrans+key)){
                text = text.replace(prefixTrans+key, translateKey(key, args));
            }
            if(text.contains(key)){
                text = text.replace(key, translateKey(key, args));
            }
        }
        return text;
    }

    private String translateKey(String key, List<String> args) {
        String message = translations.get(key);
        
        if (message == null) return key;
        if (args == null || args.isEmpty()) return message;

        for (int i = 0; i < args.size(); i++) {
            String placeholder = placeHol1 + i + placeHol2;
            if (message.contains(placeholder)) {
                message = message.replace(placeholder, args.get(i));
            }
        }
        
        return message;
    }

    public String get(String key) {
        return translations.getOrDefault(key, key);
    }
}