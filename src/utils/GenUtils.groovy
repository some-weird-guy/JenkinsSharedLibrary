package utils

import java.time.LocalDateTime

class GenUtils {

    GenUtils() {
    }

    @NonCPS
    public static void jenkinsPrint(String log, int loglevel) {
        log = "##[${LocalDateTime.now()}]: ${log}";
        println(log);
    }
}
