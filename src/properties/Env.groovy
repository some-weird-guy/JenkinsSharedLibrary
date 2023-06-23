package kapil.properties;

import kapil.base.Base;

class Env extends Base {
    protected String workspacePath;
    protected String jobName;

    Env(script, jenkins){
        super(script, jenkins);

        this.workspacePath = this.script.env.getProperty('WORKSPACE');
        this.jobName = this.script.env.getProperty('JOB_NAME')
    }

}
