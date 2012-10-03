package me.ibhh.BookShop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrepareLibrary {

    private static BookShop plugin;
    private static String path;
    private static boolean debug = false;
    private static String[] resources = {"httpclient-4.2.1.jar", "httpcore-4.2.1.jar", "httpmime-4.2.1.jar", "httpclient-cache-4.2.1.jar", "fluent-hc-4.2.1.jar", "commons-logging-1.1.1.jar", "commons-codec-1.6.jar"};

    public static void setPlugin(BookShop plugin) {
        if (plugin != null) {
            plugin = plugin;
            path = "lib";
        }
    }

    public void loadAndInstantiate() {
        ClassNotFoundException e = null;
        for (String a : resources) {
            MyURLClassLoader cl = null;
            try {
                File file = new File(path + File.separator + a);

                URL url = file.toURL();
                JarFile jarFile = new JarFile(file);
                Enumeration entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) entries.nextElement();
                    String entryName = entry.getName();
                    try {
                        cl = new MyURLClassLoader(new URL[]{url}, getClass().getClassLoader());
                        if (entryName.endsWith(".class")) {
                            Class loadedClass = cl.loadClass(entryName);
                            if (debug) {
                                System.out.println("Loaded " + loadedClass.getName());
                            }
                        }
                    } catch (ClassNotFoundException ex) {
                        System.out.println("Failed to load class " + entryName);
                        e = ex;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (cl != null) {
                    cl.close();
                }
            }
        }
        Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, e);
    }

    public static boolean loaded() {
        return packageExists(new String[]{"org.apache.http.client.methods.HttpPost"});
    }

    private static boolean packageExists(String[] packages) {
        try {
            String[] arrayOfString = packages;
            int j = packages.length;
            for (int i = 0; i < j; i++) {
                String pkg = arrayOfString[i];
                Class.forName(pkg);
            }
            return true;
        } catch (Exception localException) {
        }
        return false;
    }

    public static void copy() {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (String a : resources) {
            File last = new File(directory + File.separator + a);
            if (last.exists()) {
                last.delete();
            }
            String pathofresource = "/" + a;
            InputStream in = BookShop.class.getResourceAsStream(pathofresource);
            OutputStream os = null;
            if (in != null) {
                try {
                    os = new FileOutputStream(directory + File.separator + a);
                    try {
                        byte[] buffer = new byte[4096];
                        int n;
                        while ((n = in.read(buffer)) != -1) {
                            os.write(buffer, 0, n);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            os.close();
                        } catch (IOException ex) {
                            Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        in.close();
                        os.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PrepareLibrary.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                System.out.println("No Resource found!: " + pathofresource);
            }
        }
    }
}