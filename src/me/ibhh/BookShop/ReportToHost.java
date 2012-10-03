package me.ibhh.BookShop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ReportToHost {

    private BookShop plugin;
    private FileSend filesend;
    private StackTraceUtil util;

    public ReportToHost(BookShop pl) {
        this.plugin = pl;
        util = new StackTraceUtil();
        if (PrepareLibrary.loaded()) {
            this.filesend = new FileSend(this.plugin);
        }
    }

    public String report(int line, String other, String message, String classfile, Exception stack) {
        if (this.plugin.getConfig().getBoolean("senderrorreport")) {
            if (other == null) {
                other = "none";
            }
            if (message == null) {
                message = "none";
            }
            String stacktrace;
            if (stack != null) {
                stacktrace = util.getStackTrace(stack);
            } else {
                stacktrace = "none";
            }

            return send(line + "", message, classfile, stacktrace, other);
        }
        return "internet not enabled in the config.yml";
    }

    public String report(int line, String other, String message, String classfile, String stacktrace) {
        if (this.plugin.getConfig().getBoolean("senderrorreport")) {
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
        }
        return "internet not enabled in the config.yml";
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
            this.plugin.Logger("Exception: IOException! Exception on reading message!", "Error");
            return "Exception on reading message!";
        } catch (Exception e) {
            e.printStackTrace();
            this.plugin.Logger("Exception: Exception! Exception on reading message!", "");
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
        String url = "http://ibhh.de/report/index.php?plugin=" + this.plugin.getName() + "&version=" + this.plugin.getDescription().getVersion() + "&line=" + line + "&gameversion=" + this.plugin.getServer().getBukkitVersion() + "&message=" + message + "&class=" + classfile + "&stacktrace=" + stacktrace + "&other=" + other;
        try {
            String temp = "[" + this.plugin.getName() + "] Sending issue report to ibhh.de!";
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] -------------------------";
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] Version: " + this.plugin.getDescription().getVersion();
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            System.out.print("[" + this.plugin.getName() + "] ErrorID: " + line);
            temp = "[" + this.plugin.getName() + "] Version: " + this.plugin.getDescription().getVersion();
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] Gameversion: " + this.plugin.getServer().getBukkitVersion();
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] Other: " + other;
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] Message: " + message;
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            if (this.plugin.getConfig().getBoolean("debug")) {
                temp = "[" + this.plugin.getName() + "] Stacktrace: " + stacktrace;
                System.out.print(temp);
                this.plugin.Loggerclass.log(temp);
            }
            temp = "[" + this.plugin.getName() + "] Class: " + classfile;
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] -------------------------";
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            ret = readAll(url);
            temp = "[" + this.plugin.getName() + "] Message of Server: " + ret;
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            temp = "[" + this.plugin.getName() + "] -------------------------";
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
        } catch (Exception ex) {
            String temp = "[" + this.plugin.getName() + "] Couldnt send error report to ibhh.de!";
            System.out.print(temp);
            this.plugin.Loggerclass.log(temp);
            if (this.plugin.getConfig().getBoolean("debug")) {
                ex.printStackTrace();
            }
        }
        if (PrepareLibrary.loaded()) {
            this.plugin.Logger("filesend loaded", "Debug");
            if (this.plugin.getConfig().getBoolean("senddebugfile")) {
                this.plugin.Logger("config == true", "Debug");
                if (ret != null) {
                    this.plugin.Logger("ret != null", "Debug");
                    try {
                        String[] id_text = ret.split(":");
                        String id = id_text[1];
                        this.plugin.Logger("ID: " + id, "Debug");
                        try {
                            if (id != null) {
                                this.filesend.sendDebugFile(id);
                            }
                        } catch (Exception e1) {
                            this.plugin.Logger("Could not send debugfile!", "Error");
                            if (this.plugin.config.debug) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return ret;
    }
}