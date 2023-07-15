import hudson.model.Cause
import hudson.model.Cause.UserIdCause
import com.sonyericsson.rebuild.RebuildCause
import org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
import hudson.model.Cause.UpstreamCause
import hudson.triggers.SCMTrigger.SCMTriggerCause
import hudson.triggers.TimerTrigger.TimerTriggerCause




class CauseUtils {
  def buildObj;

  def causeTypes = [0 : [_class : 'class hudson.model.Cause$UserIdCause', associatedCauses : [], haveAssociatedBuild : false],
                    1 : [_class : 'class com.sonyericsson.rebuild.RebuildCause', associatedCauses : [0], haveAssociatedBuild : true],
                    2 : [_class : 'class org.jenkinsci.plugins.workflow.cps.replay.ReplayCause', associatedCauses : [0], haveAssociatedBuild : true],
                    3 : [_class : 'class hudson.model.Cause$UpstreamCause', associatedCauses : [], haveAssociatedBuild : true ]

  CauseUtils(def script, def buildObj) {
    this.script = script;
    this.buildObj = buildObj;
  }

  @NonCPS
  def __getCauseTypeMetaInfo(def causeClass) {
    this.causeTypes.each {
      if(it.value['_class'] == causeClass) {
        return it.value
      }
    }
    
  }

  @NonCPS
  def _getCauseActionsFromBuildObj(def buildObj) {
      def causeActions = buildObj.getActions(CauseAction.class);
      GenUtils.jenkinsPrint(this.script," Cause actions object: ${causeActions} of build object: ${builfObj}",4);
      return causeActions;
  }

  @NonCPS
  def _getBuildCausesFromBuildObj(def buildObj, def filter, def causeList, def currentLevel) {

    for(Action a : this._getCauseActionFromBuildObj()) {
      for(Cause c : a.getCauses()) {
        def causeDetails = [
          __causeClass : c.getClass(),
          __buildExternalizableId : __currentLevelbuildObj.getExternalizableId()
          _primary : [:],
          _secondary : [:],
          __level : currentLevel,
          _childs : []
        ];
        causeDetails['_primary']['ShortDescription'] = c.getShortDescription;
        def causeTypeMetaInfo = __getCauseTypeMetaInfo(causeDetails["__causeClass"])
        if(causeDetails["__causeClass"] == this.causeTypes["0"]["_class"]) {
          
        }
        
        
      }
    }
    
  }

  def getBuildCauses(def filter) {
    def causeList = []; // a list of map
    int initialLevel = 0;
    _getBuildCausesFromBuildObj(this.buildObj, filter, causeList, initialLevel);
    
  }
  
  

  
}
