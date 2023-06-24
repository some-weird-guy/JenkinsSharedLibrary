package utils

import jenkins.model.Jenkins;
class Utils {

    Utils() {
        //
    }

    def getJenkinsInstance() {
        return Jenkins.get()
    }
}
