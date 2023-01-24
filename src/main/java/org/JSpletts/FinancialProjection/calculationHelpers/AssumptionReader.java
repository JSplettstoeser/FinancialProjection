package org.JSpletts.FinancialProjection.calculationHelpers;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class AssumptionReader {

    public AssumptionReader() {
        int x = 0;
    }

    public void load(HashMap<String,String> map, String fileName) throws IOException {
        String cvsSplitBy = ",";

        InputStream is;
        if(fileName.substring(0,1).equals("/"))
            is = new FileInputStream(fileName);
        else
            is = getClass().getClassLoader().getResourceAsStream(fileName);
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);

        for (String line2; (line2 = reader.readLine()) != null;) {
            String[] keyValue = line2.split(cvsSplitBy);
            if(keyValue.length == 1)
                map.put(keyValue[0], "");
            else
              map.put(keyValue[0], keyValue[1]);
        }
        int x = 0;
    }
    public void loadDoubleArray(ArrayList<double[]> doubleArray, String fileName) throws IOException {
        String line = "";
        String cvsSplitBy = ",";

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);

        int lines = 0;
        int columns = 0;
        while ((line =reader.readLine()) != null) {
            lines++;
            if(lines ==1) {
                columns = line.split(cvsSplitBy).length;
            } else{
                String[] dataLine = line.split(cvsSplitBy);
                double[] v2 = new double[columns];
                for(int i =0; i<dataLine.length; i++) {
                    v2[i] = Double.valueOf(dataLine[i]);
                }
                doubleArray.add(v2);
            }
        }
        reader.close();
    }
    public void loadStringArray(ArrayList<String[]> doubleArray, String fileName) throws IOException {
        String line = "";
        String cvsSplitBy = ",";

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);

        int lines = 0;
        int columns = 0;
        while ((line =reader.readLine()) != null) {
            lines++;
            if(lines ==1) {
                columns = line.split(cvsSplitBy).length;
            } else{
                String[] dataLine = line.split(cvsSplitBy);
                String[] v2 = new String[columns];
                for(int i =0; i<dataLine.length; i++) {
                    v2[i] = dataLine[i];
                }
                doubleArray.add(v2);
            }
        }
        reader.close();
    }

}
