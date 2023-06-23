package properties;

import base.Base;

class Env extends Base {
    /* Example values for a item named "sample"
     * workspacePath = '/var/lib/jenkins/workspace/practice3/sample'
     * jobName = 'sample'
    */
    protected String workspacePath;
    protected String jobName;

    Env(script, jenkins){
        super(script, jenkins);

        this.workspacePath = this.script.env.getProperty('WORKSPACE');
        this.jobName = this.script.env.getProperty('JOB_NAME')
    }
    //----------------------Getter & Setter methods------------------------------------
    public String getWorkspacePath() {
        return this.@workspacePath
    }
    public String getJobName() {
        return this.@jobName
    }
    public void setWorkspacePath(String workspacePath) {
        this.@workspacePath = workspacePath
    }
    public void setJobName(String jobName) {
        this.@jobName= jobName
    }

}
