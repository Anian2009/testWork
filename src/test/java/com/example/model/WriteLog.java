package com.example.model;

import java.io.FileWriter;
import java.io.IOException;

public class WriteLog {
    private static final String PATH_4_LOG = "src\\test\\resources\\log.txt";
    private StringBuilder stringBuilder = new StringBuilder(4000);

    protected void log(String str){
        stringBuilder.append(str);
    }

    protected void inFile(){
        try (FileWriter fileWriter = new FileWriter(PATH_4_LOG, false)) {
            fileWriter.write(String.valueOf(stringBuilder));
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
