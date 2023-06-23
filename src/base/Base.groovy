package base

import java.util.UUID
import java.time.LocalDateTime

class Base implements Serializable {
    protected UUID id;
    protected def script; //WorkflowScript object
    protected def jenkins; 
    protected String name;

    Base(script, jenkins) {   // jenkins : class jenkins.model.Jenkins (it is a singleton class)
        this.id = UUID.randomUUID();
        this.script = script;
        this.jenkins = jenkins
        this.name = "";
    }
    //----------------------Getter & Setter methods------------------------------------
    public UUID getId() {
        return this.@id;
    }
    public def getScript() {
        return this.@script;
    }
    public def getJenkins() {
        return this.@jenkins;
    }
    public String getName() {
        return this.@name;
    }
    public void setScript(script) {
        this.@script = script;
    }
    public void setJenkins(jenkins) {
        this.@jenkins = jenkins
    }
    public void setName(String name) {
        this.@name = name;
    }
    //----------------------------------------------------------------------------
    public void cleanWorkspace() {
        this.jenkinsPrint("cleaning the workspace....", 3)
        this.script.cleanWs(cleanWhenAborted: false,
                            cleanWhenFailure: false,
                            cleanWhenNotBuilt: false,
                            cleanWhenSuccess: true,
                            cleanWhenUnstable: false
                            )
    }
    
    public void jenkinsPrint(String log, int loglevel) {
        log = "##[${LocalDateTime.now()}]: ${log}"
        if (this.script != null) {
            this.script.echo(message: log);
        } else {
            println(log);
        }
    }

}
