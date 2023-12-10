package no.ntnu.encryption;

public class ChangeKey {
  // The private static instance of the class
  private static ChangeKey instance;
  private String key;

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
    this.key = key;
  }

  public String getKey(){
    return key;
  }

}
