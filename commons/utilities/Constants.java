package utilities;

public class Constants {
	public static final String PROJECT_PATH = System.getProperty("user.dir");

	// System Variables
	public static final String URL = "https://fabet.live/";
	public static final String PATH_TEST_DATA = PROJECT_PATH + "\\data\\Excel-Automation.xlsx";
//	public static final String PATH_TEXT_FILE = PROJECT_PATH + "\\common\\utility\\OR.txt";
	public static final String EXCEL_FILE_NAME = "Excel-Automation.xlsx";
	public static final String KEYWORD_FAIL = "FAIL";
	public static final String KEYWORD_PASS = "PASS";
	public static final String KEYWORD_SKIP = "SKIP";

	// Test Cases Sheet
	public static final int RUN_MODE = 2;
	public static final int TC_RESULT = 3;
	
	// Test Steps Sheet
	public static final int TC_ID = 0;
	public static final int TS_ID = 1;
	public static final int ACTION = 4;
	public static final int DATA = 5;
	public static final int TS_RESULT = 7;

	// PageUIs Sheet
	public static final int ELEMENT = 6;
	
	// Excel sheet
	public static final String SHEET_TESTSTEPS = "Test Cases";
	public static final String SHEET_TESTCASES = "Test Steps";
	public static final String SHEET_PAGEUI = "PageUIs";

	// Test Data
	public static final String USERNAME = "";
	public static final String PASSWORD = "";


}
