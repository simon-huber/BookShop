/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * @author Simon
 */
public class ReportToHost {

    private BookShop plugin;

    public ReportToHost(BookShop pl) {
        plugin = pl;
    }

    public String report(int line, String other, String message, String classfile, Exception stack) {
        if (plugin.getConfig().getBoolean("senderrorreport")) {
//            if (other == null) {
//                other = "none";
//            }
//            other = other.replace(" ", "%20");
//            if (message == null) {
//                message = "none";
//            }
//            message = message.replace(" ", "%20");
            if (other == null) {
                other = "none";
            }
            if (message == null) {
                message = "none";
            }
            String stacktrace;
            if (stack != null) {
                stacktrace = StackTraceUtil.getStackTrace(stack);
            } else {
                stacktrace = "none";
            }
//            if (stacktrace == null) {
//                stacktrace = "none";
//            }
//            stacktrace = stacktrace.replace(" ", "%20");
            return send(line + "", message, classfile, stacktrace, other);
        } else {
            return "internet not enabled in the config.yml";
        }
    }

    public String report(int line, String other, String message, String classfile, String stacktrace) {
        if (plugin.getConfig().getBoolean("senderrorreport")) {
            //            if (other == null) {
//                other = "none";
//            }
//            other = other.replace(" ", "%20");
//            if (message == null) {
//                message = "none";
//            }
//            message = message.replace(" ", "%20");
//            if (stacktrace == null) {
//                stacktrace = "none";
//            }
//            stacktrace = stacktrace.replace(" ", "%20");
            if (other == null) {
                other = "none";
            }
            if (message == null) {
                message = "none";
            }
            if (stacktrace == null) {
                stacktrace = "none";
            }
            return send(line + "", message, classfile, stacktrace, other);
        } else {
            return "internet not enabled in the config.yml";
        }
    }

    public String readAll(String url) {
        String zeile;
        try {
            URL myConnection = new URL(url);
            URLConnection connectMe = myConnection.openConnection();
            InputStreamReader lineReader = new InputStreamReader(connectMe.getInputStream());
            BufferedReader br = new BufferedReader(new BufferedReader(lineReader));
            zeile = br.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            plugin.Logger("Exception: IOException! Exception on reading message!", "Error");
            return "Exception on reading message!";
        } catch (Exception e) {
            e.printStackTrace();
            plugin.Logger("Exception: Exception! Exception on reading message!", "");
            return "Exception on reading message!";
        }
        return zeile;
    }

    public String send(String line, String message, String classfile, String stacktrace, String other) {
        String ret = "Error";
        try {
            stacktrace = URLEncoder.encode(stacktrace, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            stacktrace = "exceptiononencoding";
        }
        try {
            message = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            message = "exceptiononencoding";
        }
        try {
            classfile = URLEncoder.encode(classfile, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            classfile = "exceptiononencoding";
        }
        try {
            other = URLEncoder.encode(other, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            other = "exceptiononencoding";
        }
        String url = "http://ibhh.de/report/index.php?"
                + "plugin=" + plugin.getName()
                + "&version=" + plugin.getDescription().getVersion()
                + "&line=" + line
                + "&gameversion=" + plugin.getServer().getBukkitVersion()
                + "&message=" + message
                + "&class=" + classfile
                + "&stacktrace=" + stacktrace
                + "&other=" + other;
        try {
            String temp = "[" + plugin.getName() + "] Sending issue report to ibhh.de!";
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] -------------------------";
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] Version: " + plugin.getDescription().getVersion();
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            System.out.print("[" + plugin.getName() + "] ErrorID: " + line);
            temp = "[" + plugin.getName() + "] Version: " + plugin.getDescription().getVersion();
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] Gameversion: " + plugin.getServer().getBukkitVersion();
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] Other: " + other;
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] Message: " + message;
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            if (plugin.getConfig().getBoolean("debug")) {
                temp = "[" + plugin.getName() + "] Stacktrace: " + stacktrace;
                System.out.print(temp);
                plugin.Loggerclass.log(temp);
            }
            temp = "[" + plugin.getName() + "] Class: " + classfile;
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] -------------------------";
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            ret = readAll(url);
            temp = "[" + plugin.getName() + "] Message of Server: " + ret;
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            temp = "[" + plugin.getName() + "] -------------------------";
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
        } catch (Exception ex) {
            String temp = "[" + plugin.getName() + "] Couldnt send error report to ibhh.de!";
            System.out.print(temp);
            plugin.Loggerclass.log(temp);
            if (plugin.getConfig().getBoolean("debug")) {
                ex.printStackTrace();
            }
        }
        return ret;
    }
}
