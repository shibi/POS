package com.rpos.pos;


import com.rpos.pos.domain.utils.Utility;

public interface Config {

    /**
     * App version .  ( NB )
     *  WHEN CHANGING VERSION, NEED TO PROVIDE
     *  SAME VERSION IN ADMIN PANEL
     * */
    String APP_VERSION = "1.0";


    /**
     * url prefix
     * */
    //String BASE_URL_PREFIX = "http://anncrm.dxgsofts.in/";
    //String BASE_URL_PREFIX = "http://185.185.126.217:6500/api/method/custom_integration";
    String BASE_URL_PREFIX = "http://185.185.126.217:6500/";


    /**
     * base url for api calls
     * */
    //String BASE_URL = BASE_URL_PREFIX+".api.";
    String BASE_URL = BASE_URL_PREFIX+"api/method/";


    String LICENCE_SERVER = "http://hsm.momscode.in/";

    String ZATCA_SERVER = "https://api-fatoora.herokuapp.com/";

    /**
     * API key normal, edit api key here.
     * */
    String API_KEY = "@Appname_ApiKey0123#";


    /**
     * DO NOT EDIT.....
     * Encrypted API key, used in network request for security
     * Do not edit this ..  edit only API_KEY variable above.
     * */
    String ENCRYPTED_API_KEY = Utility.base64Encrypt(Config.API_KEY);


    /**
     * To enable or disable log
     * */
    boolean IS_DEVELOPMENT = true;

    /**
     * Enable log for tracing request links
     * */
    boolean LOG = Config.IS_DEVELOPMENT;

    /**
     * Enable log for tracing request links
     * */
    String DB_NAME = "AppLocalDB#POSMachine";


    /**
     * shared preference local storage name
     * */
    String SHARED_PREF_NAME ="PosLocal#014";


    /**
     * http client connection read , write  and time out in seconds
     * */
    int HTTP_CONNECTION_TIMEOUT_IN_SEC = 60;
    int HTTP_CONNECTION_READ_TIMEOUT_IN_SEC = 60;
    int HTTP_CONNECTION_WRITE_TIMEOUT_IN_SEC = 60;


    //splash timeout
    int SPLASH_TIME_OUT = 1000;

    /**
     * app logo name
     * */
    String APP_LOGO_NAME = "appLogo.jpg";

    String LOGO_DIRECTORY = "POS/LOGO";

    String SALES_REPORT_FILE_PREFIX = "SalesReport";
    String PURCHASE_REPORT_FILE_PREFIX = "PurchaseReport";

    int MAXIMUM_DATABASE_FILE = 5;

    String BACKUP_DIRECTORY_NAME = "Pos_Reports/backup";

}


