package com.wdbyte.bing;

/**
 * @author niulang
 * @date 2023/02/11
 */
public class LogUtils {

    public static void log(String msg, Object... args) {
        if (args != null) {
            System.out.println(String.format(msg, args));
        } else {
            System.out.println(msg);
        }
    }
}
