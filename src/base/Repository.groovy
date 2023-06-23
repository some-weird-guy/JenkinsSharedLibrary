package kapil.base

import kapil.base.Base

class Repository extends Base{
    protected String url;
    protected String credentials;
    protected String branch
    protected String origin
    
    Repository(script, jenkins){
        super(script, jenkins)
        
        this.url = ""
        this.credentials = ""
        this.branch = ""
        this.origin = ""
    }
    //----------------------Getter & Setter methods------------------------------------
    public String getUrl(){
        return this.@url
    }
    public String getCredentials(){
        return this.@credentials
    }
    public String getBranch(){
        return this.@branch
    }
    public String getOrigin(){
        return this.@origin
    }
    public void setUrl(String url){
        this.@url = url
    }
    public void setCredentials(String credentials){
        this.@credentials = credentials
    }
    public void setBranch(String branch){
        this.@branch = branch
    }
    public void setOrigin(String origin) {
        this.@origin = origin
    }
    //----------------------------------------------------------------------------

}
