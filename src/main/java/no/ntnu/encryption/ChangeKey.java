package no.ntnu.encryption;

public class ChangeKey {
  // The private static instance of the class
  private static ChangeKey instance;
  private String key;
  private String greenhouseKey;

  // Private constructor so no instances can be created externally
  private ChangeKey() {
  }

  // Public static method to get the instance
  public static ChangeKey getInstance() {
    if (instance == null) {
      instance = new ChangeKey();
    }
    return instance;
  }

  public void setKey(String key){
    System.out.println("KEY c: " + key);
    this.key = key;
  }

  public String getKey(){
    return key;
  }

  public void setGreenhouseKeyKey(String key){
    System.out.println("KEY g: " + key);
    this.greenhouseKey = key;
  }

  public String getGreenhouseKeyKey(){
    return greenhouseKey;
  }

}
