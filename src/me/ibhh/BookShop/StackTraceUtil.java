package me.ibhh.BookShop;

import java.io.*;

/**
 * Simple utilities to return the stack trace of an exception as a String.
 */
public class StackTraceUtil {

    public String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    public static String getStackTrace(Exception stack) {
        StringWriter sw = new StringWriter();
        stack.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Defines a custom format for the stack trace as String.
     */
    public String getCustomStackTrace(Throwable aThrowable) {
        if (aThrowable != null) {
            //add the class name and any message passed to constructor
            final StringBuilder result = new StringBuilder("");
            result.append(aThrowable.toString());
            final String NEW_LINE = "<br>";
            result.append(NEW_LINE);

            //add each element of the stack trace
            for (StackTraceElement element : aThrowable.getStackTrace()) {
                result.append(element);
                result.append(NEW_LINE);
            }
            return result.toString();
        } else {
            return "no stack trace";
        }
    }
}
