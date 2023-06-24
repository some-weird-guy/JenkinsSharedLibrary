package utils

import jenkins.model.Jenkins;
class Utils {

    protected def jenkins;

    Utils() {
        this.jenkins = getJenkinsInstance()
    }
    
    @NonCPS
    def getJenkinsInstance() {
        return Jenkins.get()
    }
}
