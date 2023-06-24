package utils

import jenkins.model.Jenkins;
class Utils {

    Utils() {
        this.jenkins = getJenkinsInstance()
    }

    def getJenkinsInstance() {
        return Jenkins.get()
    }
}
