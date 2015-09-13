package com.tda.gairoutes.misc.util;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Alexey on 8/31/2015.
 */
public class CSVUtil {

    private static final String SEPARATOR = ",";

    private CSVUtil() {};

    public static List<GeoPoint> getPointsFromCsvFile(File csvFile) {
        try {
            List<String> listOfLines = FileUtil.getTextFileContentAsListOfLines(csvFile);
            List<GeoPoint> points = new ArrayList<>(listOfLines.size());
            boolean firstLine = true;
            for (String line : listOfLines) {
                if (!firstLine) {
                    String[] lineValues = line.split(SEPARATOR);
                    points.add(new GeoPoint(
                            Double.parseDouble(lineValues[0]),    // latitude
                            Double.parseDouble(lineValues[1]),    // longitude
                            Double.parseDouble(lineValues[2])));  // altitude
                } else {
                    firstLine = false;
                }
            }
            return points;
        } catch (FileNotFoundException e) {
            Timber.e("No such file [" + csvFile.getAbsolutePath() + "]");
            return new ArrayList<>();
        }
    }
}
