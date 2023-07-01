package properties;

import base.Base;

class Env extends Base {
    /* Example values for a item named "sample"
     * workspacePath = '/var/lib/jenkins/workspace/practice3/sample'
     * jobName = 'sample'
    */
    protected String workspacePath;
    protected String jobName;

    Env(script){
        super(script);

        this.workspacePath = ""
        this.jobName = ""
    }
    //----------------------Getter & Setter methods------------------------------------
    public String getWorkspacePath() {
        if(!this.workspacePath){
            this.workspacePath = this.script.env.getProperty('WORKSPACE');
        }
        return this.@workspacePath
    }
    public String getJobName() {
        if(!this.jobName){
            this.jobName = this.script.env.getProperty('JOB_NAME')
        }
        return this.@jobName
    }
    public void setWorkspacePath(String workspacePath) {
        this.@workspacePath = workspacePath
    }
    public void setJobName(String jobName) {
        this.@jobName= jobName
    }

}
