package TrajDB.Util;

/**
 * Created by zeyuec on 11/9/14.
 */
public class Log {

    public static boolean DEBUG = true;

    public static void plain(String msg) {
        System.out.println(msg);
    }

    public static void exception(String msg) {
        System.out.println("[Exception] " + msg);
    }

    public static void error(String msg) {
        System.out.println("[Error] " + msg);
    }

    public static void debug(String msg) {
        if (DEBUG) {
            System.out.println("[Debug] " + msg);
        }
    }

    public static void output(String msg) {
        System.out.println("[Return] " + msg);
    }
}
