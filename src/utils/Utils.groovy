package utils

import jenkins.model.Jenkins; // it is a singleton class
//-------------------------------
import hudson.model.CauseAction
import hudson.model.Action
import hudson.model.Cause
import hudson.model.Cause.UpstreamCause
import hudson.model.Cause.UserIdCause
import org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
import com.sonyericsson.rebuild.RebuildCause
//-------------------------------
import utils.GenUtils

class Utils {

    def script; // hudson.model.Hudson
    def jenkins;
    String currentJobName; // Name of the current job
    def currentJobObj; // org.jenkinsci.plugins.workflow.job.WorkflowJob
    def currentBuildObj;
    def causeList;

    Utils(script) {
        this.script = script
        this.jenkins = this._getJenkins()
        this.currentJobName = this._getCurrentJobName()
        this.currentJobObj = this._getCurrentJobObj()
        this.currentBuildObj = this._getCurrentBuildObj()
        this.causeList = [];
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

    @NonCPS
    public def _getAllCauses(def BuildObj, int currentLevelX) {
        def _currentLevelbuildObj = BuildObj;
        boolean deepestLevelReached = false;
        // deepest cause will in the last of list
        int currentLevelZ = 0;
        for(Action a : this._getAllCauseActions(_currentLevelbuildObj)){
            int currentLevelY = 0
            for(Cause c : a.getCauses()){
                def causeMap = [
                        level : [
                            Z : currentLevelZ,
                            X : currentLevelX,
                            Y : currentLevelY
                        ],
                        _class : c.getClass(),
                        ShortDescription : c.getShortDescription(),
                        primary : null,
                        secondary : null
                ]
                if(RebuildCause.class.isInstance(c)){
                    //  A cause specifying that the build was a rebuild of another build.
                    // rebuild a parametrized build without entering the parameters again
                    // Extends UpstreamCause; that is why control statement of this cause is checked before Upstream cause
                    // Rebuild Cause will always occur along with UserIdCause
                     causeMap["primary"] = [
                            UpStreamProject : c.getUpstreamProject(),
                            UpstreamBuild : c.getUpstreamBuild(),
                            UpSreamUrl : c.getUpstreamUrl()
                    ]
                    
                }
                if(UpstreamCause.class.isInstance(c)){
                    causeMap["primary"] = [
                            UpStreamProject : c.getUpstreamProject(),
                            UpstreamBuild : c.getUpstreamBuild(),
                            UpSreamUrl : c.getUpstreamUrl()
                    ]
                    _currentLevelbuildObj = c.getUpstreamRun()
                    this._getAllCauses(_currentLevelbuildObj, currentLevelX + 1)
                }
                else if(UserIdCause.class.isInstance(c)){
                    causeMap["primary"] = [
                            UserId : c.getUserId(),
                            UserName : c.getUserName(),
                            UserUrl : c.getUserUrl()
                    ]
                }
                else if(ReplayCause.class.isInstance(c)){
                    // Replay Cause will always occur along with UserIdCause
                    // Allows you to replay a Pipeline build with a modified script.
                    causeMap["primary"] = [
                            OriginalNumber : c.getOriginalNumber()
                    ]
                }
                this.causeList.add(causeMap);
                GenUtils.jenkinsPrint(this.script,"${causeMap}",4);
                currentLevelY = currentLevelY+1;
            }
            currentLevelZ = currentLevelZ + 1;
        }
    }


    @NonCPS
    public def _getCurrentBuildCauses() {
        this._getAllCauses(this.currentBuildObj, 0)
        return this.causeList;
    }





}
