package org.barrelmc.barrel.utils;
import org.barrelmc.barrel.utils.nukkit.TextFormat;
public class Logger{
  public String prefix;
  private static final String prefixFinal = TextFormat.RESET.getAnsiCode()+"] ";
  private static final String prefixInfo = "["+TextFormat.GREEN.getAnsiCode()+"INFO"+TextFormat.RESET.getAnsiCode()+"] [";
  private static final String prefixEmergency = "["+TextFormat.RED.getAnsiCode()+"EMERGENCY"+TextFormat.RESET.getAnsiCode()+"] [";
  private static final String prefixAlert = "["+TextFormat.YELLOW.getAnsiCode()+"WARN"+TextFormat.RESET.getAnsiCode()+"] [";
  private static final String prefixFatal = "["+TextFormat.DARK_RED.getAnsiCode()+"FATAL"+TextFormat.RESET.getAnsiCode()+"] [";
  private static final String prefixError = "["+TextFormat.RED.getAnsiCode()+"ERROR"+TextFormat.RESET.getAnsiCode()+"] [";
  private static final String prefixDebug = "[DEBUG] [";
  public Logger(String prefix){
    this.prefix = prefix;
  }
  public Logger getLogger(){
    return this;
  }
  public String getPrefix(){
    return this.prefix;
  }
  public void emergency(String message){
    System.err.println(prefixEmergency+ this.prefix +prefixFinal+message);
  }
  public void emergency(String message, Throwable er){
    System.err.println(prefixEmergency+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void info(String message){
    System.out.println(prefixInfo+ this.prefix +prefixFinal+message);
  }
  public void info(String message, Throwable er){
    System.out.println(prefixInfo+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void alert(String message){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
  }
  public void alert(String message, Throwable er){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void critical(String message){
    System.err.println(prefixFatal+ this.prefix +prefixFinal+message);
  }
  public void critical(String message, Throwable er){
    System.err.println(prefixFatal+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void error(String message){
    System.err.println(prefixError+ this.prefix +prefixFinal+message);
  }
  public void error(String message, Throwable er){
    System.err.println(prefixError+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void warning(String message){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
  }
  public void warning(String message, Throwable er){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void notice(String message){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
  }
  public void notice(String message, Throwable er){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void debug(String message){
    System.out.println(prefixDebug+ this.prefix +prefixFinal+message);
  }
  public void debug(String message, Throwable er){
    System.out.println(prefixDebug+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
  public void logException(Throwable er){
    er.printStackTrace();
  }
  public void log(String message){
    System.out.println(message);
  }
  public void log(String message, Throwable er){
    System.out.println(message);
    er.printStackTrace();
  }
  public void warn(String message){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
  }
  public void warn(String message, Throwable er){
    System.out.println(prefixAlert+ this.prefix +prefixFinal+message);
    er.printStackTrace();
  }
}
