package com.rpos.pos.domain.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rpos.pos.Config;
import com.rpos.pos.Constants;


public class SharedPrefHelper {

    private static SharedPrefHelper instance;
    private static final String SP_LOGIN_USERNAME = "username";
    private static final String SP_LOGIN_PASSWORD = "password";

    private static final String SP_USERID = "userid";

    private static final String SP_API_KEY = "ApiKEY";
    private static final String SP_API_SECRET = "ApiSecret";

    //default tax
    private static final String SP_DEFAULT_TAX_ID = "DefaultTaxId";
    private static final String SP_DEFAULT_TAX_NAME = "DefaultTaxName";
    private static final String SP_DEFAULT_TAX_RATE = "DefaultTaxRate";

    //home screen layout
    private static final String SP_DEFAULT_HOME_LAYOUT = "default_layout";

    //language
    private static final String SP_SELECTED_LANG = "selected_lang";

    private static final String SP_DEFAULT_CURRENCY = "defaultCurrency";

    private static final String SP_DEFAULT_CURRENCY_SYMBOL = "defaultCurrencySymbol";

    private static final String SP_BILLING_COUNTRY = "billingCountry";
    private static final String SP_DEFAULT_CUST_ID = "DefaultCustId";
    private static final String SP_DEFAULT_CUST_NAME = "DefaultCustName";
    private static final String SP_DEFAULT_CUST_MOBILE = "DefaultCustMobile";

    private static final String SP_DEFAULT_BUYING_PRICE_LIST_ID = "DefaultBuyingPriceListId";
    private static final String SP_DEFAULT_SELLING_PRICE_LIST_ID = "DefaultSellingPriceListId";


    private static final String SP_PRINTER_CONNECTION_METHOD = "printerConnectionMethod";


    private static final String SP_APP_LOGO_PATH = "AppLogoPath";

    private static Context mContext;



    private SharedPrefHelper(Context context) {
        this.mContext = context;
    }

    public static SharedPrefHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefHelper(context);
        }
        return instance;
    }

    public void saveUserId(String userid) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_USERID, userid);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String apiKey = sharedPreferences.getString(SP_USERID, "");
            return apiKey;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void saveApiToken(String apiKey, String apiSecret) {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_API_KEY, apiKey);
            editor.putString(SP_API_SECRET, apiSecret);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TAX
    public void saveDefaultTax(int taxId,String taxName,float taxRate) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_DEFAULT_TAX_ID, taxId);
            editor.putString(SP_DEFAULT_TAX_NAME, taxName);
            editor.putFloat(SP_DEFAULT_TAX_NAME, taxRate);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getDefaultTaxId() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int taxid = sharedPreferences.getInt(SP_DEFAULT_TAX_ID, -1);
            return taxid;
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }


    //HOME LAYOUT MENU STYLE
    public void setDefaultHomeScreenLayout(int homeScreenLayout) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_DEFAULT_HOME_LAYOUT, homeScreenLayout);

            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getHomeScreenDefaultLayout() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int homeScreenLayout = sharedPreferences.getInt(SP_DEFAULT_HOME_LAYOUT, Constants.EMPTY_INT);
            return homeScreenLayout;
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    //LANGUAGE
    public void setSelectedLang(String language) {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_SELECTED_LANG, language);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getSelectedLang() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String selectedLang = sharedPreferences.getString(SP_SELECTED_LANG, Constants.LANG_EN);
            return selectedLang;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.LANG_EN;
        }
    }


    //DEFAULT CURRENCY
    public void setDefaultCurrency(int currencyId, String symbol) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_DEFAULT_CURRENCY, currencyId);
            editor.putString(SP_DEFAULT_CURRENCY_SYMBOL, symbol);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getDefaultCurrency() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(SP_DEFAULT_CURRENCY, Constants.EMPTY_INT);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY_INT;
        }
    }
    public String getDefaultCurrencySymbol(){
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(SP_DEFAULT_CURRENCY_SYMBOL, Constants.NONE);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.NONE;
        }
    }

    //Buying price list id
    public void setBuyingPriceList(int buyingPriceListId) {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_DEFAULT_BUYING_PRICE_LIST_ID, buyingPriceListId);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getBuyingPriceListId() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int defaultBuyingPriceListId = sharedPreferences.getInt(SP_DEFAULT_BUYING_PRICE_LIST_ID, Constants.EMPTY_INT);
            return defaultBuyingPriceListId;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY_INT;
        }
    }

    //Selling price list id
    public void setSellingPriceList(int sellingPriceListId) {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_DEFAULT_SELLING_PRICE_LIST_ID, sellingPriceListId);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getSellingPriceListId() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int defaultSellingPriceListId = sharedPreferences.getInt(SP_DEFAULT_SELLING_PRICE_LIST_ID, Constants.EMPTY_INT);
            return defaultSellingPriceListId;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY_INT;
        }
    }



    //default Country
    public void setBillingCountry(int countryId) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_BILLING_COUNTRY, countryId);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBillingCountry() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(SP_BILLING_COUNTRY, Constants.EMPTY_INT);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY_INT;
        }
    }

    //default Country
    public void setAppLogoPath(String path) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_APP_LOGO_PATH, path);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAppLogoPath() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(SP_APP_LOGO_PATH, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    //default Customer
    public void setDefaultCustomer(String custId, String custName,String mobile) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_DEFAULT_CUST_ID, custId);
            editor.putString(SP_DEFAULT_CUST_NAME, custName);
            editor.putString(SP_DEFAULT_CUST_MOBILE, mobile);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getDefaultCustomer() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String custId = sharedPreferences.getString(SP_DEFAULT_CUST_ID, "");
            String custName = sharedPreferences.getString(SP_DEFAULT_CUST_NAME, "");
            String custMobile = sharedPreferences.getString(SP_DEFAULT_CUST_MOBILE, "");
            String[] custDetails = new String[3];

            custDetails[0] = custId;
            custDetails[1] = custName;
            custDetails[2] = custMobile;

            return custDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getApiKey() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String apiKey = sharedPreferences.getString(SP_API_KEY, "");
            return apiKey;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getApiSecret() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String apiSecret = sharedPreferences.getString(SP_API_SECRET, "");
            return apiSecret;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    //printer connection method
    public void setPrinterConnectionMethod(int method) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_PRINTER_CONNECTION_METHOD, method);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getPrinterConnectionMethod() {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int printerConnectionMethod = sharedPreferences.getInt(SP_PRINTER_CONNECTION_METHOD, Constants.EMPTY_INT);
            return printerConnectionMethod;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY_INT;
        }
    }

}
