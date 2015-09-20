package com.tda.gairoutes.misc.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

/**
 * Created by Alexey on 8/31/2015.
 */
public class FileUtil {

    public static String ENCODING_UTF8 = "UTF-8";

    private FileUtil() {}

    public static List<String> getTextFileContentAsListOfLines(File file) throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        InputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, Charset.forName(ENCODING_UTF8));
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fis);
            close(isr);
            close(br);
        }
        return lines;
    }

    public static String getTextFileContentAsString(File file) throws FileNotFoundException {
        StringBuffer buffer = new StringBuffer();
        InputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, Charset.forName(ENCODING_UTF8));
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fis);
            close(isr);
            close(br);
        }
        return buffer.toString();
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean unzip(File fileToZip, File targetDirectory) {
        Timber.d("Start unzipping " + fileToZip.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
        try {
            ZipFile zipFile = new ZipFile(fileToZip);
            int count;
            byte[] buffer = new byte[8192];
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry zipEntry;
            while (entries.hasMoreElements()) {
                zipEntry = entries.nextElement();
                File file = new File(targetDirectory, zipEntry.getName());
                Timber.d("Processing zip entry " + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    FileOutputStream outputStream = new FileOutputStream(file);
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    try {
                        while ((count = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }
                    } finally {
                        outputStream.close();
                        inputStream.close();
                    }
                }
            }
            Timber.d("Unzipping done");
            fileToZip.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unzip(InputStream is, File targetDirectory) {
        ZipInputStream zipInputStream = null;
        try {
            Timber.d("Start unzipping stream to " + targetDirectory.getAbsolutePath());
            zipInputStream = new ZipInputStream(is);
            ZipEntry zipEntry;
            int count;
            byte[] buffer = new byte[8192];
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File file = new File(targetDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        while ((count = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }
                    } finally {
                        outputStream.close();
                    }
                }
                Timber.d("Unzipping done");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
