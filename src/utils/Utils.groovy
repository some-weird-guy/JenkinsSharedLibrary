package utils

import jenkins.model.Jenkins; // it is a singleton class
//-------------------------------
import hudson.model.CauseAction
import hudson.model.Action
import hudson.model.Cause
import hudson.model.Cause.UpstreamCause
import hudson.model.Cause.UserIdCause
import hudson.triggers.SCMTrigger.SCMTriggerCause
import hudson.triggers.TimerTrigger.TimerTriggerCause
import org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
import com.sonyericsson.rebuild.RebuildCause
//-------------------------------
import hudson.tasks.Mailer
import hudson.model.User
//-------------------------------
import utils.GenUtils

class Utils {

    def script; // hudson.model.Hudson
    def jenkins;
    String currentJobName; // Name of the current job
    def currentJobObj; // org.jenkinsci.plugins.workflow.job.WorkflowJob
    def currentBuildObj;
    def causeList;
    int causeDepthIndex;

    Utils(script) {
        this.script = script
        this.jenkins = this._getJenkins()
        this.currentJobName = this._getCurrentJobName()
        this.currentJobObj = this._getCurrentJobObj()
        this.currentBuildObj = this._getCurrentBuildObj()
        this.causeList = [];
        this.causeDepthIndex = 1
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
    //----------------------------Cause Handling-------------------------------------
    @NonCPS
    def _getAllCauseActions(def buildObj) {
        def causeActions = buildObj.getActions(CauseAction.class);
        GenUtils.jenkinsPrint(this.script,"All cause actions object: ${causeActions}",4);
        return causeActions;
    }

    @NonCPS
    public def _getAllCauses(def BuildObj, int currentLevelX, def ParentCauseBuildObj) {
        def __previousLevelbuildObj = ParentCauseBuildObj;
        def __currentLevelbuildObj = BuildObj;
        def __nextLevelbuildObj = null; // do not take this variable seriously
        // deepest cause will in the last of list
        int currentLevelZ = 0;
        for(Action a : this._getAllCauseActions(__currentLevelbuildObj)){
            int currentLevelY = 0
            for(Cause c : a.getCauses()){
                def causeMap = [
                        _level : [
                            Z : currentLevelZ,
                            X : currentLevelX,
                            Y : currentLevelY
                        ],
                        _class : c.getClass(),
                        ShortDescription : c.getShortDescription(),
                        _ExternalizableId : __currentLevelbuildObj.getExternalizableId(),
                        _parentCauseExternalizableId : ((__previousLevelbuildObj == null) ? null : __previousLevelbuildObj.getExternalizableId()),
                        _primary : null,
                        _secondary : [:]
                ]
                if(RebuildCause.class.isInstance(c)){
                    //  A cause specifying that the build was a rebuild of another build.
                    // rebuild a parametrized build without entering the parameters again
                    // Extends UpstreamCause; that is why control statement of this cause is checked before Upstream cause
                    // Rebuild Cause will always occur along with UserIdCause
                     causeMap["_primary"] = [
                            UpStreamProject : c.getUpstreamProject(),
                            UpstreamBuild : c.getUpstreamBuild(),
                            UpSreamUrl : c.getUpstreamUrl()
                    ]
                    this.causeList.add(causeMap); // this call is intentionally added to every cause to preserve the calling dfs order rather than actual dfs order
                    if(this.causeDepthIndex){
                        __nextLevelbuildObj = c.getUpstreamRun();
                        this._getAllCauses(__nextLevelbuildObj, currentLevelX + 1, __currentLevelbuildObj)   
                    }   
                }
                else if(UpstreamCause.class.isInstance(c)){
                    causeMap["_primary"] = [
                            UpStreamProject : c.getUpstreamProject(),
                            UpstreamBuild : c.getUpstreamBuild(),
                            UpSreamUrl : c.getUpstreamUrl()
                    ]
                    this.causeList.add(causeMap);
                    if(this.causeDepthIndex){
                        __nextLevelbuildObj = c.getUpstreamRun();
                        this._getAllCauses(__nextLevelbuildObj, currentLevelX + 1, __currentLevelbuildObj)   
                    }
                }
                else if(UserIdCause.class.isInstance(c)){
                    causeMap["_primary"] = [
                            UserId : c.getUserId(),
                            UserName : c.getUserName(),
                            UserUrl : c.getUserUrl()
                    ]
                    causeMap["_secondary"]["Mail"] = this._getUsermailFromUserId(c.getUserId())
                    this.causeList.add(causeMap);
                }
                else if(ReplayCause.class.isInstance(c)){
                    // Replay Cause will always occur along with UserIdCause
                    // Allows you to replay a Pipeline build with a modified script.
                    causeMap["_primary"] = [
                            OriginalNumber : c.getOriginalNumber()
                    ]
                    this.causeList.add(causeMap);
                    if(this.causeDepthIndex){
                         __nextLevelbuildObj = c.getOriginal();
                         this._getAllCauses(__nextLevelbuildObj, currentLevelX + 1, __currentLevelbuildObj)    
                    }
                }
                else if(SCMTriggerCause.class.isInstance(c)){
                    this.causeList.add(causeMap);
                }
                else if(TimerTriggerCause.class.isInstance(c)){
                    this.causeList.add(causeMap);
                }
                currentLevelY = currentLevelY+1;
            }
            currentLevelZ = currentLevelZ + 1;
        }
    }


    @NonCPS
    public def _getCurrentBuildCauses() {
        this._getAllCauses(this.currentBuildObj, 0, null)
        return this.causeList;
    }
    //----------------------------Mail Handling-------------------------------------
    @NonCPS
    public def _getUsermailFromUserId(String userId) {
        def userObj = _getUserObjFromUserId(userId);
        def mailUserPropertyObj = userObj.getProperty(Mailer.UserProperty.class);
        if(mailUserPropertyObj.hasExplicitlyConfiguredAddress()){
            return mailUserPropertyObj.getExplicitlyConfiguredAddress();
        }
        else{
            return mailUserPropertyObj.getAddress();
        }
        
    }
    //----------------------------User Handling-------------------------------------
    @NonCPS
    public def _getUserObjFromUserId(String userId) {
        return User.get(userId)
    }



}
