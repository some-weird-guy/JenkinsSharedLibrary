package utils

import jenkins.model.Jenkins; // it is a singleton class

import utils.GenUtils

class Utils {

    def script; // hudson.model.Hudson
    def jenkins;
    String currentJobName; // Name of the current job
    def currentJobObj; // org.jenkinsci.plugins.workflow.job.WorkflowJob
    def currentBuildObj;

    Utils(script) {
        this.script = script
        this.jenkins = this.__getJenkins()
        this.currentJobName = this._getCurrentJobName()
        this.currentJobObj = this._getCurrentJobObj()
        this.currentBuildObj = this._getCurrentBuildObj()
    }

    //-----------------------------------------------------------------------------------
    @NonCPS
    public def __getJenkins() {
        def jenkins =  Jenkins.getInstanceOrNull()
        GenUtils.jenkinsPrint(this.script,"Jenkins singleton object: ${jenkins}",4)
        return jenkins
    }
    
    @NonCPS
    public def __getItemByName(String name) {
        return this.jenkins.getItem(name)
    }
    //-----------------------------------------------------------------------------------
    @NonCPS
    public def _getAllBuildsFromJob(def jobObj) {
        return jobObj.getBuilds()
    }
    //-----------------------------------------------------------------------------------
    @NonCPS
    public String _getCurrentJobName() {
        def currentJobName = this.script.env.getProperty('JOB_NAME')
        GenUtils.jenkinsPrint(this.script,"Current job name: ${currentJobName}",4)
        return currentJobName
    }
    
    @NonCPS
    public def _getCurrentJobObj() {
        return this.__getItemByName(this.currentJobName)
    }

    @NonCPS
    public def _getCurrentBuildObj() {
        // return this.currentBuild.rawBuild()
        return this._getAllBuildsFromJob(this.currentJobObj)[0];
    }
    //-----------------------------------------------------------------------------------
    @NonCPS
    public void _getBuildMetaInfoFromBuildObj(def buildObj) {
        // buildObj : hudson.model.Run

        def buildMetaInfo = [
            displayName : buildObj.getDisplayName(),
            fullDisplayName : buildObj.getFullDisplayName(),
            id : buildObj.getId(),
            number : buildObj.getNumber(),
            externalizableId : buildObj.getExternalizableId()  // static Run<?,?> fromExternalizableId​(String id)
        ];    
    }
    
}
