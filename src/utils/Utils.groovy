package utils

import jenkins.model.Jenkins;
class Utils {

    protected def jenkins;

    Utils() {
        this.jenkins = getJenkinsInstance()
    }

    def getJenkinsInstance() {
        return Jenkins.get()
    }
}
