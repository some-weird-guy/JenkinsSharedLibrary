package utils

import hudson.tasks.Mailer
import hudson.model.User

class UserUtils { // singleton design pattern with Lazy Initilalization 

  private static UserUtils instance;
  private UserUtils() {
    
  }
  public static UserUtils getInstance() {
    if (instance == null) {
      instance = new UserUtils();
    }
    return instance;
  }

  
  @NonCPS
  public def _getUsermailFromUserId(String userId) {
    def userObj = __getUserObjFromUserId(userId);
    def mailUserPropertyObj = userObj.getProperty(Mailer.UserProperty.class);
    if(mailUserPropertyObj.hasExplicitlyConfiguredAddress()){
        return mailUserPropertyObj.getExplicitlyConfiguredAddress();
    }
    else{
        return mailUserPropertyObj.getAddress();
    }
      
  }
  @NonCPS
  public def __getUserObjFromUserId(String userId) {
    return User.get(userId)
    }
}
