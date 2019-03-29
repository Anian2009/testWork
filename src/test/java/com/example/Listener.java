package com.example;

import com.example.model.Path4log;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.FileWriter;
import java.io.IOException;

public class Listener extends TestListenerAdapter {

    @Override
    public void onTestSuccess(ITestResult result) {
        try (FileWriter fileWriter = new FileWriter(Path4log.PATH_4_LOG,true)){
            fileWriter.write(result.getTestClass().getName() + ":" + result.getMethod().getMethodName()+" - PASSED\n");
            System.out.println(result.getTestClass().getName() + ":" + result.getMethod().getMethodName()+" - PASSED");
        }catch (IOException e){
            System.out.println(e);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try (FileWriter fileWriter = new FileWriter(Path4log.PATH_4_LOG,true)){
            fileWriter.write(result.getTestClass().getName() + ":" + result.getMethod().getMethodName()+" - FAILED\n");
            System.out.println(result.getTestClass().getName() + ":" + result.getMethod().getMethodName()+" - FAILED");
        }catch (IOException e){
            System.out.println(e);
        }
    }
}