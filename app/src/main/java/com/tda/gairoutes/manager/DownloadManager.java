package com.tda.gairoutes.manager;

import com.tda.gairoutes.misc.util.DateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import timber.log.Timber;

/**
 * Created by Alexey on 9/16/2015.
 */
public class DownloadManager {

    private static int TIMEOUT_TIME = 10 * DateUtil.MS_IN_SEC;

    private static String CONTENT_LENGTH = "Content-Length";

    public interface DownloadListener {
        public void onDownloadComplete(String url, File file);
        public void onDownloadError(String url, Exception ex);
        public void onDownloadProgress(String url, int percentReady);
    }

    public static void downloadFile(String url, String path, String fileName, DownloadListener downloadListener) {
        try {
            Timber.d("Start downloading from " + url + " to " + path);
            HttpURLConnection connection = (HttpURLConnection)new java.net.URL(url).openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(TIMEOUT_TIME);
            connection.connect();

            String lengthStringValue = connection.getHeaderField(CONTENT_LENGTH);
            long length = lengthStringValue == null ? 0 : Long.parseLong(lengthStringValue);
            Timber.d("File size = " + (length == 0 ? "unknown" : length));
            File filePath = new File(path);
            if (!filePath.exists()) filePath.mkdirs();
            File file = new File(filePath, fileName);

            InputStream is = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            long bytesTotalRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                bytesTotalRead += bytesRead;
                if (downloadListener != null && length > 0) {
                    downloadListener.onDownloadProgress(url, (int) (bytesTotalRead / length) * 100);
                }
            }
            fos.flush();
            fos.close();
            is.close();
            if (downloadListener != null) {
                Timber.d("Download complete");
                downloadListener.onDownloadComplete(url, file);
            }
        } catch (Exception e) {
            Timber.e(e, "Couldn't download file from " + url);
            if (downloadListener != null) {
                downloadListener.onDownloadError(url, e);
            }
        }
    }
}
