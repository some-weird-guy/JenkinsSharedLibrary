import hudson.model.CauseAction
import hudson.model.Action
import hudson.model.Cause
import hudson.model.Cause.UserIdCause
import com.sonyericsson.rebuild.RebuildCause
import org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
import hudson.model.Cause.UpstreamCause
import hudson.triggers.SCMTrigger.SCMTriggerCause
import hudson.triggers.TimerTrigger.TimerTriggerCause

import utils.GenUtils




class CauseUtils {
  
  def buildObj;
  def script;
  def causeTypes = [
    0 : [
      _class : 'class hudson.model.Cause$UserIdCause',
      associatedCauses : [],
      haveAssociatedBuild : false
    ],
    1 : [
      _class : 'class com.sonyericsson.rebuild.RebuildCause',
      associatedCauses : [0],
      haveAssociatedBuild : true
    ],
    2 : [
      _class : 'class org.jenkinsci.plugins.workflow.cps.replay.ReplayCause',
      associatedCauses : [0],
      haveAssociatedBuild : true
    ],
    3 : [
      _class : 'class hudson.model.Cause$UpstreamCause',
      associatedCauses : [],
      haveAssociatedBuild : true
    ]
  ];
  
  CauseUtils(def script, def buildObj) {
    this.script = script;
    this.buildObj = buildObj;
  }

  @NonCPS
  def __getCauseTypeMetaInfo(def causeClass) {
    this.causeTypes.each {
      if(it.value['_class'] == causeClass) {
        return [it.key, it.value];
      }
    } 
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
        GenUtils.jenkinsPrint(this.script,"${causeDetails}",3)
        def causeTypeMetaInfo = __getCauseTypeMetaInfo(causeDetails["__causeClass"]);
        GenUtils.jenkinsPrint(this.script,"${causeTypeMetaInfo}",3)
        if(filter['allowedCauseTypes'].contains(causeTypeMetaInfo[0])) {
          if(causeTypeMetaInfo[1]["_class"] == this.causeTypes["1"]["_class"]) {
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
              __nextLevelbuildObj = c.getUpstreamRun();
              this._getAllCauses(__nextLevelbuildObj, filter, causeDetails['_childs'], currentLevel+1);
            }
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes["3"]["_class"]) {
            /* UpstreamCause
            */
            causeDetails['_primary'] = [
              UpStreamProject : c.getUpstreamProject(),
              UpstreamBuild : c.getUpstreamBuild(),
              UpSreamUrl : c.getUpstreamUrl()
            ];
            causeList.add(causeDetails);
            if(currentLevel+1 <= filter['maxLevel']){
              __nextLevelbuildObj = c.getUpstreamRun();
              this._getAllCauses(__nextLevelbuildObj, filter, causeDetails['_childs'], currentLevel+1);
            }
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes["2"]["_class"]) {
            /*Replay Cause
            by this plugin we can replay a pipeline build with a modified script
            */
            causeDetails['_primary'] = [
              OriginalNumber : c.getOriginalNumber()
            ];
            causeList.add(causeDetails);
            if(currentLevel+1 <= filter['maxLevel']){
              __nextLevelbuildObj = c.getOriginal();
              this._getAllCauses(__nextLevelbuildObj, filter, causeDetails['_childs'], currentLevel+1);
            }
          }
          else if(causeTypeMetaInfo[1]["_class"] == this.causeTypes["0"]["_class"]) {
            /*UserId Cuase
            */
            causeDetails["_primary"] = [
              UserId : c.getUserId(),
              UserName : c.getUserName(),
              UserUrl : c.getUserUrl()
            ];
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
      allowedCauseTypes : [0,1,2,3]
    ];
    filter = defaultFilter;
    _getBuildCausesFromBuildObj(this.buildObj, filter, causeList, initialLevel);
    return causeList;
  }
  
  

  
}
