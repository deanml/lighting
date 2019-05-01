package com.control4.integration.utils;

import org.testng.ITestResult;
import org.testng.reporters.JUnitReportReporter;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.utils
 */

//Simple class to add the test description to the junit report.
public class CustomReporter extends JUnitReportReporter {
    @Override
    protected String getTestName(ITestResult tr) {
        return tr.getMethod()
                .getMethodName() + ": " + tr.getMethod()
                .getDescription();
    }
}
