package executionEngine;

import java.lang.reflect.Method;

import org.apache.log4j.xml.DOMConfigurator;

import keywords.actionKeywords;
import utilities.Constants;
import utilities.ExcelUtils;
import utilities.Log;

public class RunTestscript {

    public RunTestscript() throws NoSuchMethodException, SecurityException {
        actionKeywords = new actionKeywords();
        method = actionKeywords.getClass().getMethods();
    }

    public static void main(String[] args) throws Exception {
        ExcelUtils.setExcelFile(Constants.PATH_TEST_DATA);
        DOMConfigurator.configure("log4j.xml");

        RunTestscript startEngine = new RunTestscript();
        startEngine.execute_TestCase();
    }

    private void execute_TestCase() throws Exception {
        // Count all rows in sheet Testcase
        int iTotalTestCases = ExcelUtils.getRowCount(Constants.SHEET_TESTCASES);
        Log.info("Total testcase = " + iTotalTestCases);
        // Loop for every row
        for (int iTestcase = 1; iTestcase < iTotalTestCases; iTestcase++) {
            // Set result = true
            bResult = true;
            // Get Testcase ID
            sTestCaseID = ExcelUtils.getCellData(iTestcase, Constants.TC_ID, Constants.SHEET_TESTCASES);
            Log.info("Total testcase = " + sTestCaseID);
            // Get Run Mode (If RunMode = Yes then Run Test)
            sRunMode = ExcelUtils.getCellData(iTestcase, Constants.RUN_MODE, Constants.SHEET_TESTCASES);
            Log.info("Run mode = " + sRunMode);
            if (sRunMode.equals("Yes")) {
                // Start testcase
                Log.startTestCase(sTestCaseID);
                // Map Teststep ID from TestcaseID
                iTestStep = ExcelUtils.getRowContains(sTestCaseID, Constants.TC_ID, Constants.SHEET_TESTSTEPS);
                Log.info("Teststep = " + iTestStep);
                iTestLastStep = ExcelUtils.getTestStepsCount(Constants.SHEET_TESTSTEPS, sTestCaseID, iTestStep);
                Log.info("Last step = " + iTestLastStep);
                bResult = true;
                for (; iTestStep < iTestLastStep; iTestStep++) {
                    // Get data of action keyword/ page object/ data set
                    sActionKeyword = ExcelUtils.getCellData(iTestStep, Constants.ACTION, Constants.SHEET_TESTSTEPS);
                    Log.info("Action keyword = " + sActionKeyword);
                    sPageObject = ExcelUtils.getCellData(iTestStep, Constants.ELEMENT, Constants.SHEET_TESTSTEPS);
                    Log.info("Page Object = " + sPageObject);
                    sData = ExcelUtils.getCellData(iTestStep, Constants.DATA, Constants.SHEET_TESTSTEPS);
                    Log.info("Data set = " + sData);
                    // Run test
                    execute_Actions();
                    if (bResult == false) {
                        ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestcase, Constants.TS_RESULT, Constants.SHEET_TESTCASES);
                        Log.endTestCase(sTestCaseID);
                        break;
                    }
                }
                if (bResult == true) {
                    ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestcase, Constants.TS_RESULT, Constants.SHEET_TESTCASES);
                    Log.endTestCase(sTestCaseID);
                }
            }
        }
    }
    
    private static void execute_Actions() throws Exception {
        String[] sheets = {Constants.SHEET_TESTCASES, Constants.SHEET_TESTSTEPS, Constants.SHEET_PAGEUI};
        for (String sheet : sheets) {
            int totalKeywords = ExcelUtils.getRowCount(sheet);
            for (int i = 1; i < totalKeywords; i++) {
                String tcId = ExcelUtils.getCellData(i, Constants.TC_ID, sheet);
                String tsId = ExcelUtils.getCellData(i, Constants.TS_ID, sheet);
                String ui = ExcelUtils.getCellData(i, Constants.UI, sheet);
                String action = ExcelUtils.getCellData(i, Constants.ACTION, sheet);
                String data = ExcelUtils.getCellData(i, Constants.DATA, sheet);
                
                // check if the current row is marked as runnable
                String runMode = ExcelUtils.getCellData(i, Constants.RUN_MODE, Constants.SHEET_TESTCASES);
                if (runMode.equalsIgnoreCase("yes")) {
                    // Invoke the method corresponding to the Action keyword
                    Method method = actionKeywords.class.getMethod(action, String.class, String.class);
                    method.invoke(actionKeywords.class.newInstance(), ui, data);
                    
                    // Check the result of the action
                    boolean tsResult = bResult;
                    String tcResult;
                    if (!tsResult) {
                        tcResult = Constants.KEYWORD_FAIL;
                        ExcelUtils.setCellData(tcResult, i, Constants.TS_RESULT, sheet);
                        ExcelUtils.setCellData(tcResult, i, Constants.TC_RESULT, Constants.SHEET_TESTCASES);
                        break;
                    } else {
                        tcResult = Constants.KEYWORD_PASS;
                        ExcelUtils.setCellData(tcResult, i, Constants.TS_RESULT, sheet);
                    }
                    
                    // If this is the last test step for the current test case, mark the test case as completed
                    int nextTsId = i + 1;
                    if (nextTsId >= totalKeywords || !ExcelUtils.getCellData(nextTsId, Constants.TC_ID, sheet).equals(tcId)) {
                        ExcelUtils.setCellData(tcResult, i, Constants.TC_RESULT, Constants.SHEET_TESTCASES);
                    }
                }
            }
        }
    }
	public static actionKeywords actionKeywords;
	public static String sActionKeyword;
	public static String sPageObject;
	public static Method method[];
	
	public static int iTestStep;
	public static int iTestLastStep;
	public static String sTestCaseID;
	public static String sRunMode;
	public static String sData;
	public static String sKeyword;
	public static String sObject;
	public static boolean bResult;
    }
           
