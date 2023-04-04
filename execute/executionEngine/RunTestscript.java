package executionEngine;

import java.lang.reflect.Method;

import org.apache.log4j.xml.DOMConfigurator;

import keywords.actionKeywords;
import utilities.Constants;
import utilities.ExcelUtils;
import utilities.Log;

public class RunTestscript {
    public static actionKeywords ActionKeywords;
    public static String sActionKeyword;
    public static String sPageObject;
    public static Method[] method;

    public static int iTestStep;
    public static int iTestLastStep;
    public static String sTestCaseID;
    public static String sRunMode;
    public static String sData;
    public static String sKeyword;
    public static String sObject;
    public static String sTestStepID;
    public static boolean bResult;

    public RunTestscript() throws NoSuchMethodException, SecurityException {
        ActionKeywords = new actionKeywords();
        method = ActionKeywords.getClass().getMethods();
    }

    public static void main(String[] args) throws Exception {
        ExcelUtils.setExcelFile(Constants.PATH_TEST_DATA);
        DOMConfigurator.configure("log4j.xml");

        // Initialize action keywords object
        ActionKeywords = new actionKeywords();
        // Open the Excel file
        ExcelUtils.setExcelFile(Constants.PATH_TEST_DATA);

        // Loop through the Test Steps sheet
        for (iTestStep = 1; iTestStep <= ExcelUtils.getRowCount(Constants.SHEET_TESTSTEPS); iTestStep++) {
            // Get the current test case ID and test step ID
            sTestCaseID = ExcelUtils.getCellData(iTestStep, Constants.TC_ID, Constants.SHEET_TESTSTEPS);
            ExcelUtils.getCellData(iTestStep, Constants.TS_ID, Constants.SHEET_TESTSTEPS);

            // If the test case ID is blank, skip the test step
            if (sTestCaseID.equals("")) {
                continue;
            }

            // Get the Action, UI, and Data associated with the test step
            sActionKeyword = ExcelUtils.getCellData(iTestStep, Constants.ACTION, Constants.SHEET_TESTSTEPS);
            sPageObject = ExcelUtils.getCellData(iTestStep, Constants.ELEMENT, Constants.SHEET_TESTSTEPS);
            sData = ExcelUtils.getCellData(iTestStep, Constants.DATA, Constants.SHEET_TESTSTEPS);

            // Merge the page object from the Test Steps sheet with the corresponding element from the PageUIs sheet
            if (!sPageObject.equals("")) {
                String sElement = ExcelUtils.getCellData(iTestStep, Constants.ELEMENT, Constants.SHEET_PAGEUI);
                sPageObject = sElement + "." + sPageObject;
            }

            // Invoke the method corresponding to the Action keyword
            Method method = ActionKeywords.getClass().getMethod(sActionKeyword, String.class, String.class);
            method.invoke(ActionKeywords, sPageObject, sData);

            // If the method returns false, set the result to false
            if (bResult == false) {
                ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestStep, Constants.TS_RESULT, Constants.SHEET_TESTSTEPS);
            }
        }

        // Call execute_TestCase method outside the loop
        RunTestscript startEngine = new RunTestscript();
        startEngine.execute_TestCase();
    }
    
    private void execute_TestCase() throws Exception {
        // Count all rows in sheet Testcase
        int iTotalTestCases = ExcelUtils.getRowCount(Constants.SHEET_TESTCASES);
        Log.info("Total testcase = " + iTotalTestCases);
        // Loop for every row
        for (int iTestcase = 1; iTestcase <= iTotalTestCases; iTestcase++) {
            // Get the test case ID and run mode
            sTestCaseID = ExcelUtils.getCellData(iTestcase, Constants.TC_ID, Constants.SHEET_TESTCASES);
            sRunMode = ExcelUtils.getCellData(iTestcase, Constants.RUN_MODE, Constants.SHEET_TESTCASES);

            // If the run mode is "No", skip the test case
            if (sRunMode.equals("No")) {
                // Set result to "Skipped" for all test steps associated with this test case
                int iLastStep = ExcelUtils.getTestStepsCount(sTestCaseID, iTestcase);
                ExcelUtils.setTestStepsResult(sTestCaseID, iTestcase, 1, iLastStep, Constants.KEYWORD_SKIP);
                // Log the skipped test case
                Log.info("Test case " + sTestCaseID + " skipped");
                continue;
            }

            // Log the start of the test case
            Log.startTestCase(sTestCaseID);

            // Loop through the Test Steps sheet to execute the test steps
            iTestStep = ExcelUtils.getFirstTestStepRow(sTestCaseID, iTestcase);
            iTestLastStep = ExcelUtils.getTestStepsCount(sTestCaseID, iTestcase);
            bResult = true;
            while (iTestStep <= iTestLastStep) {
                sActionKeyword = ExcelUtils.getCellData(iTestStep, Constants.ACTION, Constants.SHEET_TESTSTEPS);
                sPageObject = ExcelUtils.getCellData(iTestStep, Constants.ELEMENT, Constants.SHEET_TESTSTEPS);
                sData = ExcelUtils.getCellData(iTestStep, Constants.DATA, Constants.SHEET_TESTSTEPS);

                // Merge the page object from the Test Steps sheet with the corresponding element from the PageUIs sheet
                if (!sPageObject.equals("")) {
                    String sElement = ExcelUtils.getCellData(iTestStep, Constants.ELEMENT, Constants.SHEET_PAGEUI);
                    sPageObject = sElement + "." + sPageObject;
                }

                // Invoke the method corresponding to the Action keyword
                Method method = ActionKeywords.getClass().getMethod(sActionKeyword, String.class, String.class);
                bResult = (Boolean) method.invoke(ActionKeywords, sPageObject, sData);

                // Set the test step result based on the result of the action keyword
                if (bResult == true) {
                    ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestStep, Constants.TS_RESULT, Constants.SHEET_TESTSTEPS);
                } else {
                    ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestStep, Constants.TS_RESULT, Constants.SHEET_TESTSTEPS);
                // Set the test step result based on the result of the action keyword
                if (bResult == true) {
                    // If the keyword passes, set the test step result as Pass
                    ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestStep, Constants.TS_RESULT, Constants.SHEET_TESTSTEPS);
                    Log.info(sTestCaseID + " - " + sTestStepID + " - " + sActionKeyword + " - Passed");
                } else {
                    // If the keyword fails, set the test step result as Fail
                    ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestStep, Constants.TS_RESULT, Constants.SHEET_TESTSTEPS);
                    Log.info(sTestCaseID + " - " + sTestStepID + " - " + sActionKeyword + " - Failed");
                    // If a test step fails, set the overall test case result as Fail
                    ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestcase, Constants.TC_RESULT, Constants.SHEET_TESTCASES);
                    break;
                    }        
                // If the test case has reached its last test step, set the overall test case result as Pass
                if (iTestStep == iTestLastStep) {
                    ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestcase, Constants.TC_RESULT, Constants.SHEET_TESTCASES);
                    Log.info(sTestCaseID + " - Passed");
                }
            }
        }
        }
        }
}