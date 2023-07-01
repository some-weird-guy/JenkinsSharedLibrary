package utils

import jenkins.model.Jenkins; // it is a singleton class
import utils.GenUtils

class Utils {

    def script; // hudson.model.Hudson
    def jenkins;
    String currentJobName; // Name of the current job
    def currentJobObj; // org.jenkinsci.plugins.workflow.job.WorkflowJob

    Utils(script) {
        this.script = script
        this.jenkins = this.getJenkinsInstance()
        this.currentJobName = this.getCurrentJobName()
        this.currentJobObj = this.getCurrentJobObj()
    }

    @NonCPS
    public def getJenkinsInstance() {
        return Jenkins.get()
    }

    @NonCPS
    public String getCurrentJobName() {
        def currentJobName = this.script.env.getProperty('JOB_NAME')
        println("hi")
        GenUtils.jenkinsPrint("Current job name: ${currentJobName}", 4)
        println("hi2")
        return currentJobName
    }

    @NonCPS
    public def getItemByName(String name) {
        return this.jenkins.getItem(name)
    }

    @NonCPS
    public def getCurrentJobObj() {
        return this.getItemByName(this.currentJobName)
    }





}
