package utils

import jenkins.model.Jenkins;
class Utils {


    Utils(script) {
        this.script = script
        this.jenkins = this.getJenkinsInstance()
        this.currentJobName = this.getCurrentJobName()
    }

    @NonCPS
    public def getJenkinsInstance() {
        return Jenkins.get()
    }

    @NonCPS
    public String getCurrentJobName() {
        return this.script.env.getProperty('JOB_NAME')
    }


}