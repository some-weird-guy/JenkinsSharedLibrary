package kapil.base

import java.util.UUID

class Base implements Serializable {
    protected UUID id;
    protected def script; //WorkflowScript object
    protected def jenkins; // jenkins : class jenkins.model.Jenkins (it is a singleton class)
    protected String name;

    Base(script, jenkins) {   
        this.id = UUID.randomUUID();
        this.script = script;
        this.jenkins = jenkins
        this.name = "";
    }

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

    public void jenkinsPrint(String log, int loglevel) {
        log = "## [${java.time.LocalDateTime.now()}]: ${log}"
        if (this.script != null) {
            this.script.echo(message: log);
        } else {
            println(log);
        }
    }
    
}
