package com.tda.gairoutes.misc.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
}
