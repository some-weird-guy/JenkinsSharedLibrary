package utils;

import hudson.model.CauseAction
import hudson.model.Action
import hudson.model.Cause
import hudson.model.Cause.UserIdCause
import com.sonyericsson.rebuild.RebuildCause
import org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
import hudson.model.Cause.UpstreamCause
import hudson.triggers.SCMTrigger.SCMTriggerCause
import hudson.triggers.TimerTrigger.TimerTriggerCause
import com.cloudbees.jenkins.GitHubPushCause

import utils.GenUtils
import utils.UserUtils
import utils.Utils

class CauseUtils {
  
  def buildObj;
  def script;
  def causeTypes = [
    -1 : [
      _class : 'class unknown',
      associatedCauses : [],
      haveAssociatedBuild : false,
      extendCause : null
    ],
    0 : [
      _class : 'class hudson.model.Cause$UserIdCause',
      associatedCauses : [],
      haveAssociatedBuild : false,
      extendCause : null
    ],
    1 : [
      _class : 'class com.sonyericsson.rebuild.RebuildCause',
      associatedCauses : [0],
      haveAssociatedBuild : true,
      extendCause : 3
    ],
    2 : [
      _class : 'class org.jenkinsci.plugins.workflow.cps.replay.ReplayCause',
      associatedCauses : [0],
      haveAssociatedBuild : true,
      extendCause : null
    ],
    3 : [
      _class : 'class hudson.model.Cause$UpstreamCause',
      associatedCauses : [],
      haveAssociatedBuild : true,
      extendCause : null
    ],
    4 : [
      _class : 'class hudson.triggers.TimerTrigger$TimerTriggerCause',
      associatedCauses : [],
      haveAssociatedBuild : false,
      extendCause : null
    ],
    5 : [
      _class : 'class hudson.triggers.SCMTrigger$SCMTriggerCause',
      associatedCauses : [],
      haveAssociatedBuild : false,
      extendCause : null
    ],
    6 : [
      _class : 'class com.cloudbees.jenkins.GitHubPushCause',
      associatedCauses : [],
      haveAssociatedBuild : false,
      extendCause : 5
    ]
    
  ];

  
  CauseUtils(def script, def buildObj) {
    this.script = script;
    this.buildObj = buildObj;
  }

  CauseUtils(def script) {
    this.script = script;
    // TO DO : Think about more better and optimized solution
    Utils u = new Utils(this.script);
    this.buildObj = u._getCurrentBuildObj();
  }

  
  @NonCPS
  def __getCauseTypeMetaInfo(def causeClass) {
    for(causeType in this.causeTypes) {
      GenUtils.jenkinsPrint(this.script,"${causeType}",4);
      if(causeType.value['_class'] == causeClass.toString()) {
        return [causeType.key, causeType.value];
      }
    }
    // An unknown cause type
    return [-1, 'class unknown']
  }

  @NonCPS
  def _getCauseActionsFromBuildObj(def buildObj) {
      def causeActions = buildObj.getActions(CauseAction.class);
      GenUtils.jenkinsPrint(this.script," Cause actions object: ${causeActions} of build object: ${buildObj}",4);
      return causeActions;
  }

  @NonCPS
  def _getBuildCausesFromBuildObj(def buildObj, def filter, def causeList, def currentLevel) {

    def __currentLevelBuildObj = buildObj;
    def __nextLevelBuildObj = null;
    
    for(Action a : this._getCauseActionsFromBuildObj(__currentLevelBuildObj)) {
      for(Cause c : a.getCauses()) {
        def causeDetails = [
          __causeClass : c.getClass(),
          __buildExternalizableId : __currentLevelBuildObj.getExternalizableId(),
          _primary : null,
          _secondary : null,
          __level : currentLevel,
          ShortDescription : c.getShortDescription(),
          _childs : []
        ];
        def causeTypeMetaInfo = __getCauseTypeMetaInfo(causeDetails["__causeClass"]);
        causeDetails["__causeTypeIndex"] = causeTypeMetaInfo[0];
        if(filter['allowedCauseTypes'].contains(causeTypeMetaInfo[0])) {
          if(causeTypeMetaInfo[1]['_class'] == this.causeTypes[1]["_class"]) {
            /* Rebuild Cause
            [1] A cause specifying that the build was a rebuild of another build.
            [2] by its plugin we can rebuild a parametrized build without entering the parameters again
            [3] this cause extends UpstreamCause; that is why control statement of this cause is checked before Upstream cause
            */
            causeDetails['_primary'] = [
              UpStreamProject : c.getUpstreamProject(),
              UpstreamBuild : c.getUpstreamBuild(),
              UpSreamUrl : c.getUpstreamUrl()
            ];
            causeList.add(causeDetails);
            if(currentLevel+1 <= filter['maxLevel']){
              __nextLevelBuildObj = c.getUpstreamRun();
              this._getBuildCausesFromBuildObj(__nextLevelBuildObj, filter, causeDetails['_childs'], currentLevel+1);
            }
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[3]["_class"]) {
            /* UpstreamCause
            */
            causeDetails['_primary'] = [
              UpStreamProject : c.getUpstreamProject(),
              UpstreamBuild : c.getUpstreamBuild(),
              UpSreamUrl : c.getUpstreamUrl()
            ];
            causeList.add(causeDetails);
            if(currentLevel+1 <= filter['maxLevel']){
              __nextLevelBuildObj = c.getUpstreamRun();
              this._getBuildCausesFromBuildObj(__nextLevelBuildObj, filter, causeDetails['_childs'], currentLevel+1);
            }
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[2]["_class"]) {
            /*Replay Cause
            by this plugin we can replay a pipeline build with a modified script
            */
            causeDetails['_primary'] = [
              OriginalNumber : c.getOriginalNumber()
            ];
            causeList.add(causeDetails);
            if(currentLevel+1 <= filter['maxLevel']){
              __nextLevelBuildObj = c.getOriginal();
              this._getBuildCausesFromBuildObj(__nextLevelBuildObj, filter, causeDetails['_childs'], currentLevel+1);
            }
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[0]["_class"]) {
            /*UserId Cuase
            */
            causeDetails["_primary"] = [
              UserId : c.getUserId(),
              UserName : c.getUserName(),
              UserUrl : c.getUserUrl()
            ];
            causeDetails["_secondary"] = [
              UserMail : UserUtils.getInstance()._getUsermailFromUserId(c.getUserId()),
            ];
            causeList.add(causeDetails);
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[4]["_class"]) {
            /*TimerTrigger Cause
            */
            causeList.add(causeDetails);
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[5]["_class"]) {
            /*SCMTrigger Cause
            */
            causeList.add(causeDetails);
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[6]["_class"]) {
            /*GitHubPush Cause
            */
            causeList.add(causeDetails);
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes[-1]["_class"]) {
            /*unknown Cause
            */
            causeList.add(causeDetails);
          }
          else {
            /*
            */
            causeList.add(causeDetails);
          }
        }
       }
      } 
     }

  @NonCPS
  def getBuildCauses(def filter) {
    def causeList = []; // a list of map
    int initialLevel = 0;
    def defaultFilter = [
      maxLevel : 999,
      allowedCauseTypes : [-1,0,1,2,3,4,5,6]
    ];
    filter = defaultFilter;
    _getBuildCausesFromBuildObj(this.buildObj, filter, causeList, initialLevel);
    return causeList;
  }
  
  

  
}
