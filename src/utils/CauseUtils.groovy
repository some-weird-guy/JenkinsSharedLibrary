import hudson.model.Cause
import hudson.model.Cause.UserIdCause
import com.sonyericsson.rebuild.RebuildCause
import org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
import hudson.model.Cause.UpstreamCause
import hudson.triggers.SCMTrigger.SCMTriggerCause
import hudson.triggers.TimerTrigger.TimerTriggerCause




class CauseUtils {
  def buildObj;

  def causeTypes = [0 : 'class hudson.model.Cause$UserIdCause',
                    1 : 'class com.sonyericsson.rebuild.RebuildCause',
                    2 : 'class org.jenkinsci.plugins.workflow.cps.replay.ReplayCause',
                    3 : 'class hudson.model.Cause$UpstreamCause']

  CauseUtils(def script, def buildObj) {
    this.script = script;
    this.buildObj = buildObj;
  }

  @NonCPS
  def _getCauseActionsFromBuildObj(def buildObj) {
      def causeActions = buildObj.getActions(CauseAction.class);
      GenUtils.jenkinsPrint(this.script," Cause actions object: ${causeActions} of build object: ${builfObj}",4);
      return causeActions;
  }

  @NonCPS
  def _getBuildCausesFromBuildObj(def buildObj, def filter) {
    /* cause tree 

    [1]every build have associated causes
    [2] not every build have  
    */

    for(Action a : this._getCauseActionFromBuildObj()) {
      for(Cause c : a.getCauses()) {
        def causeDetails = [
          __causeClass : c.getClass(),
          __buildExternalizableId : __currentLevelbuildObj.getExternalizableId()
          ShortDescription : c.getShortDescription(),
          _primary : null,
          _secondary : [:],
          
        ];
        
      }
    }
    
  }
  
  

  
}
