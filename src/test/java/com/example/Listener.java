package com.example;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class Listener extends TestListenerAdapter {

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(result.getTestClass().getName() + ":" + result.getMethod().getMethodName()+" - PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println(result.getTestClass().getName() + ":" + result.getMethod().getMethodName()+" - FAILED");
    }
}