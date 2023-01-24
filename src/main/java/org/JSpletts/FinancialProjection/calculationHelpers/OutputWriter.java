package org.JSpletts.FinancialProjection.calculationHelpers;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class OutputWriter {

    public OutputWriter(){

    }
    public static void saveInputs(HashMap<String, String> inputs, String path) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(path));
        String out = "";
        String[] keySet = inputs.keySet().toArray(new String[0]);
        for(int j = 0; j< keySet.length; j++){
            String key = keySet[j];
            String value = inputs.get(key);
            out = "";
            out= out.concat(key + "," + value);
            br.write(out);
            br.newLine();
        }
        br.close();

    }
    public void writeJointData(double[][] data, String fileName, String[] fieldNames) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
        String out = "";
        for(int i= 0; i<fieldNames.length; i++){
            if (i == fieldNames.length-1)
                out= out.concat(fieldNames[i]+"");
            else
                out= out.concat(fieldNames[i] + ",");
        }
        br.write(out);
        br.newLine();

        for(int j = 0; j< data[0].length; j++){
            out = "";
            for(int i= 0; i<data.length; i++){
                if (i == data.length-1)
                    out= out.concat(data[i][j]+"");
                else
                    out= out.concat(data[i][j] + ",");
            }
            br.write(out);
            br.newLine();
        }
        br.close();

    }

    public void writePersonAVs(double[][][][] data, int PersonIndex, String fileName, String[] fieldNames) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
        String out = "";
        for(int i= 0; i<fieldNames.length; i++){
            if (i == fieldNames.length-1)
                out= out.concat(fieldNames[i]+"");
            else
                out= out.concat(fieldNames[i] + ",");
        }
        br.write(out);
        br.newLine();

        for(int yr = 0; yr< data[PersonIndex][0][0].length; yr++){
            out = "";
            out = out.concat(yr + ",");
            for(int acctNum = 0; acctNum < data[PersonIndex].length; acctNum++){
                for(int j = 0; j<data[PersonIndex][0].length; j++){
                    if (j == data[PersonIndex][0][0].length-1 && acctNum == data[PersonIndex].length - 1)
                        out= out.concat(data[PersonIndex][acctNum][j][yr]+"");
                    else
                        out= out.concat(data[PersonIndex][acctNum][j][yr] + ",");
                }
            }
            br.write(out);
            br.newLine();
        }
        br.close();

    }

    public void writeAVs(double[][][][] data_indiv,double[][][] data_joint, String fileName, String[] fieldNames) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
        String out = "";
        for(int i= 0; i<fieldNames.length; i++){
            if (i == fieldNames.length-1)
                out= out.concat(fieldNames[i]+"");
            else
                out= out.concat(fieldNames[i] + ",");
        }
        br.write(out);
        br.newLine();

        for(int yr = 0; yr< data_indiv[0][0][0].length; yr++){
            out = "";
            out = out.concat(yr + ",");
            for(int acctNum = 0; acctNum < data_indiv[0].length; acctNum++){
                double sumAV = 0;
                for(int j = 0; j<data_indiv.length; j++)
                    sumAV += data_indiv[j][acctNum][5][yr];
                sumAV += data_joint[acctNum][5][yr];
                if (acctNum == data_indiv[0].length - 1)
                    out= out.concat(sumAV + "");
                else
                    out= out.concat(sumAV + ",");
            }
            br.write(out);
            br.newLine();
        }
        br.close();

    }
}
