package utils

import jenkins.model.Jenkins; // it is a singleton class
import hudson.model.CauseAction
import hudson.model.Action
import hudson.model.Cause
import hudson.model.Cause.UpstreamCause
import hudson.model.Cause.UserIdCause
import utils.GenUtils

class Utils {

    def script; // hudson.model.Hudson
    def jenkins;
    String currentJobName; // Name of the current job
    def currentJobObj; // org.jenkinsci.plugins.workflow.job.WorkflowJob
    def currentBuildObj;

    Utils(script) {
        this.script = script
        this.jenkins = this._getJenkins()
        this.currentJobName = this._getCurrentJobName()
        this.currentJobObj = this._getCurrentJobObj()
        this.currentBuildObj = this._getCurrentBuildObj()
    }

    @NonCPS
    public def _getJenkins() {
        def jenkins =  Jenkins.getInstanceOrNull()
        GenUtils.jenkinsPrint(this.script,"Jenkins singleton object: ${jenkins}",4)
        return jenkins
    }

    @NonCPS
    public String _getCurrentJobName() {
        def currentJobName = this.script.env.getProperty('JOB_NAME')
        GenUtils.jenkinsPrint(this.script,"Current job name: ${currentJobName}",4)
        return currentJobName
    }

    @NonCPS
    public def _getItemByName(String name) {
        return this.jenkins.getItem(name)
    }

    @NonCPS
    public def _getCurrentJobObj() {
        return this._getItemByName(this.currentJobName)
    }

    @NonCPS
    public def _getAllBuildsFromJob(def jobObj) {
        return jobObj.getBuilds()
    }

    @NonCPS
    public def _getCurrentBuildObj() {
        // return this.currentBuild.rawBuild()
        
        return this._getAllBuildsFromJob(this.currentJobObj)[0];
    }
    //-----------------------------------------------------------------
    @NonCPS 
    def _getAllCauseActions(def buildObj) {
        def causeActions = buildObj.getActions(CauseAction.class);
        GenUtils.jenkinsPrint(this.script,"All cause actions object: ${causeActions}",4);
        return causeActions;
    }
    
    public def _getAllCauses() {
        // for now we are onlu supporting Upstream and UserId cause
        def _currentLevelbuildObj = this.currentBuildObj;
        boolean deepestLevelReached = false;
        // i am assuming that cause chain will be linear so storing causes in a list as map
        // deepest cause will in the last of list
        def causeList = []
        while(!deepestLevelReached){
            for(Action a : this._getAllCauseActions(_currentLevelbuildObj)){
                for(Cause c : a.getCauses()){
                    if(UpstreamCause.class.isInstance(c)){
                        def causeMap = [
                            ShortDescription : c.getShortDescription(),
                            UpStreamProject : c.getUpstreamProject(),
                            UpstreamBuild : c.getUpstreamBuild(),
                            UpSreamUrl : c.getUpstreamUrl()    
                        ]
                        causeList.add(causeMap);
                        _currentLevelbuildObj = c.getUpstreamRun()
                    }
                    else if(UserIdCause.class.isInstance(c)){
                        def causeMap = [
                            ShortDescription : c.getShortDescription(),
                            UserId : c.getUserId(),
                            UserName : c.getUserName(),
                            UserUrl : c.getUserUrl()
                        ]
                        causeList.add(causeMap)
                        deepestLevelReached = true;
                    }
                    
                }
            }  
        }
        return causeList;
    }
        
            
    @NonCPS
    public def _getCurrentBuildCauses() {
      //  
    }





}
