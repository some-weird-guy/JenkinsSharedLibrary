package base

import properties.Env
import utils.GenUtils

class Repository extends Env {
    protected String url;
    protected String credentials;
    protected String branch;
    protected String origin;
    protected String clonePath;

    Repository(script){
        super(script);

        this.url = "";
        this.credentials = "";
        this.branch = "main";
        this.origin = "";
        this.clonePath = "";
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
    public String getClonePath(){
        return this.@clonePath
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
    public void setOrigin(String origin){
        this.@origin = origin
    }
    public void setClonePath(String clonePath){
        this.@clonePath = clonePath
    }
    //----------------------------------------------------------------------------

    public String getAbsoluteClonePath() {
        String absoluteClonePath = this.getWorkspacePath() + "/" + this.getClonePath();
        GenUtils.jenkinsPrint("Absolute clone path: ${absoluteClonePath}", 4)
        return absoluteClonePath;
    }

    public void checkout(){ // currently, only support Git as SCM tool

        def userRemoteConfigMap = [url: this.url];
        if(this.credentials){
            userRemoteConfigMap['credentialsId'] = this.credentials;
        }

        GenUtils.jenkinsPrint("cloning ${this.url} to ${this.getAbsoluteClonePath()}", 2)
        this.script.dir(this.getAbsoluteClonePath()){
            this.script.checkout([$class: 'GitSCM',
                                  branches: [[name: this.branch]],
                                  userRemoteConfigs: [userRemoteConfigMap]
            ]);
        }

    }

}
