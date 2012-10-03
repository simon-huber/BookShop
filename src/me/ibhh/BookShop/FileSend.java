package me.ibhh.BookShop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class FileSend {

    private BookShop plugin;

    public FileSend(BookShop plugin) {
        this.plugin = plugin;
    }

    public void sendDebugFile(final String errorid) throws IOException {
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);

                    HttpPost httppost = new HttpPost("http://ibhh.de/report/logs/send.php?plugin=" + FileSend.this.plugin.getName() + "&ID=" + errorid);
                    Date now = new Date();
                    String path = FileSend.this.plugin.getDataFolder().toString() + File.separator + "debugfiles" + File.separator;
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd 'at' HH");
                    File file = new File(path + "debug-" + ft.format(now) + ".txt");
                    if (file.exists()) {
                        try {
                            MultipartEntity mpEntity = new MultipartEntity();
                            ContentBody cbFile = new FileBody(file, "text");
                            mpEntity.addPart("userfile", cbFile);
                            httppost.setEntity(mpEntity);
                            System.out.println("executing request " + httppost.getRequestLine());
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity resEntity = response.getEntity();
                            System.out.println(response.getStatusLine());
                            if (resEntity != null) {
                                System.out.println(EntityUtils.toString(resEntity));
                            }
                            if (resEntity != null) {
                                resEntity.consumeContent();
                            }
                            httpclient.getConnectionManager().shutdown();
                        } catch (IOException ex) {
                            Logger.getLogger(FileSend.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception e) {
                    plugin.Logger("cannot send debugfile because the linking of some resources failed.", "Error");
                    plugin.Logger("May you used /reload and therefore it doesnt work.", "Error");
                }
            }
        }, 0L);
    }
}