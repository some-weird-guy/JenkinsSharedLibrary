package utils

import jenkins.model.Jenkins; // it is a singleton class
import hudson.model.CauseAction
import hudson.model.Action
import hudson.model.Cause
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
        GenUtils.jenkinsPrint(this.script,"Jenkins singleton: ${jenkins}",4)
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
        return this._getAllBuildsFromJob(this.currentJobObj)[0]
    }
    //-----------------------------------------------------------------
    @NonCPS 
    def _getAllCauseActions() {
        def causeActions = this.currentBuildObj.getActions(CauseAction.class)
        GenUtils.jenkinsPrint(this.script,"${causeActions}",4)
        for(Action a : causeActions){
            GenUtils.jenkinsPrint(this.script,"${a}",4)
            for(Cause c : a.getCauses()){
                GenUtils.jenkinsPrint(this.script,"${c.getShortDescription()}",4)
            }
        }
        
    }
    @NonCPS
    public def _getCurrentBuildCauses() {
      //  
    }





}
