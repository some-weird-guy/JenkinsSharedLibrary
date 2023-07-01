package utils

import java.time.LocalDateTime

class GenUtils {

    GenUtils() {
    }

    @NonCPS
    public static void jenkinsPrint(def script, String log, int loglevel) {
        String jenkinsLog = "##[${LocalDateTime.now()}] ${log}";
        script.echo(jenkinsLog);
    }
}
