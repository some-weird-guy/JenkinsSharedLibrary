package base

import java.util.UUID
import java.time.LocalDateTime

import utils.GenUtils

class Base implements Serializable {
    protected UUID id;
    protected def script; //WorkflowScript object
    protected String name;

    Base(script) {
        this.id = UUID.randomUUID();
        this.script = script
        this.name = "";
    }
    //----------------------Getter & Setter methods------------------------------------
    public UUID getId() {
        return this.@id;
    }
    public def getScript() {
        return this.@script;
    }
    public String getName() {
        return this.@name;
    }
    public void setScript(script) {
        this.@script = script;
    }
    public void setName(String name) {
        this.@name = name;
    }
    //----------------------------------------------------------------------------
    public void cleanWorkspace() {
        GenUtils.jenkinsPrint(this.script,"cleaning the workspace....",2)
        this.script.cleanWs(cleanWhenAborted: false,
                cleanWhenFailure: false,
                cleanWhenNotBuilt: false,
                cleanWhenSuccess: true,
                cleanWhenUnstable: false
        )
    }

}
