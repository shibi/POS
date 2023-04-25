package com.rpos.pos;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CurrencyItem;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.ShiftRegEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.models.country.CountryItem;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.opencsv.CSVWriter;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CoreApp extends Application {

    public static String DEFAULT_LANG = Constants.LANG_EN;

    public static boolean isRefreshRequired = false;

    private String defaultCurrency;

    private List<UomItem> uomList;

    private AppDatabase appDatabase;

    private List<ItemEntity> allItemsList;

    private ShiftRegEntity runningShift;


    @Override
    public void onCreate() {
        super.onCreate();

        //Printooth.INSTANCE.init(this);
        init();
    }

    /**
     * Connect print service through interface library
     */
    private void init(){
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);
    }

    public void setApiTokens(String apiKey, String apiSecret){
        Constants.API_KEY = apiKey;
        Constants.API_SECRET = apiSecret;
        SharedPrefHelper.getInstance(this).saveApiToken(apiKey,apiSecret);
    }

    public AppDatabase getLocalDb(){
        if(appDatabase==null){
            appDatabase = Room.databaseBuilder(this, AppDatabase.class, Config.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }

    public ApiService getWebService(){
        ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
        return apiService;
    }

    public ApiService getNoTokenWebService(){
        ApiService apiService = ApiGenerator.createNoTokenApiService(ApiService.class);
        return apiService;
    }

    public AppExecutors getAppExecutors(){
        AppExecutors appExecutors = new AppExecutors();
        return appExecutors;
    }

    public void setDefaultLanguage(Context context,String language){
        SharedPrefHelper.getInstance(context).setSelectedLang(language);
        DEFAULT_LANG = language;
    }

    public void setDefaultCurrency(String currency){
        defaultCurrency = currency;
    }

    public String getDefaultCurrency(){
        return defaultCurrency;
    }

    private String loadJSONFromAsset(String jsonFileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<CurrencyItem> getCurrencyList(){

        try {

            JSONArray m_jArry = new JSONArray(loadJSONFromAsset("currency.json"));
            ArrayList<CurrencyItem> currencyList_temp = new ArrayList<>();

            CurrencyItem currencyItem_temp;
            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);
                int id = jo_inside.getInt("id");
                String currencyName = jo_inside.getString("currencyName");
                String symbol = jo_inside.getString("symbol");
                String fraction = jo_inside.getString("fraction");
                String country = jo_inside.getString("country");
                String numberFormat = jo_inside.getString("numberFormat");
                double fractionUnits = jo_inside.getDouble("fractionUnit");
                double smallestFraction = jo_inside.getDouble("SmallestFraction");

                currencyItem_temp = new CurrencyItem(id,currencyName,country,symbol,fraction,(float) fractionUnits,(float) smallestFraction,numberFormat);
                currencyList_temp.add(currencyItem_temp);
            }

           return currencyList_temp;

        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public ArrayList<CountryItem> getCountryList(){
        try {
            JSONArray m_jArray = new JSONArray(loadJSONFromAsset("country.json"));
            ArrayList<CountryItem> countryList = new ArrayList<>();

            CountryItem country;
            for (int i = 0; i < m_jArray.length(); i++) {

                JSONObject jo_inside = m_jArray.getJSONObject(i);
                int id = jo_inside.getInt("id");
                String name = jo_inside.getString("country");
                country = new CountryItem(id,name);
                countryList.add(country);
            }

            return countryList;

        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<UomItem> getUomList(){
        return uomList;
    }

    public void setUomList(List<UomItem> list){
        uomList = list;
    }

    public List<ItemEntity> getAllItemsList(){
        return allItemsList;
    }

    public void setAllItemsList(List<ItemEntity> list){
        allItemsList = list;
    }

    public Bitmap loadImageFromStorage(String path) {
        try {
            File filefolder;
            filefolder = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), Config.LOGO_DIRECTORY);

            //if the folder does not exist, create a new folder to save the file
            if (!filefolder.exists()){
                return null;
            }

            String imageName = Config.APP_LOGO_NAME;
            Log.e("------------------","image Name "+imageName);

            //file path to for saving the user key
            String filePath =  filefolder+"/"+imageName; // External is the text file et_name

            //initialise the file
            File logoImage = new File(filePath);
            Bitmap imageBitmap = BitmapFactory.decodeStream(new FileInputStream(logoImage));
            return imageBitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCurrentShift(ShiftRegEntity shift){
        runningShift = shift;
    }

    public ShiftRegEntity getRunningShift(){
        return runningShift;
    }


    /*public void exportDbToCSV(SupportSQLiteDatabase db, String fileName){
        try {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, fileName + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = db.query("SELECT * FROM " + TableName, null);

                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    //Which column you want to exprort
                    String arrStr[] = new String[curCSV.getColumnCount()];
                    for (int i = 0; i < curCSV.getColumnCount() - 1; i++)
                        arrStr[i] = curCSV.getString(i);
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
                Toast.makeText(this, "Exported", Toast.LENGTH_SHORT).show();

            } catch (Exception sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            }

        }catch (Exception e){
            e.getMessage();
        }
    }*/


    /**
     * to clear all default values saved in shared preference
     */
    public void logoutSession(){
        try {

            String EMPTY = "";
            SharedPrefHelper prefHelper = SharedPrefHelper.getInstance(this);
            prefHelper.saveApiToken(EMPTY,EMPTY);
            prefHelper.saveUserId(EMPTY);
            prefHelper.setDefaultCustomer(EMPTY,EMPTY,EMPTY);
            prefHelper.setSellingPriceList(Constants.EMPTY_INT);
            prefHelper.setBuyingPriceList(Constants.EMPTY_INT);
            prefHelper.setSelectedLang(EMPTY);
            prefHelper.setBillingCountry(Constants.EMPTY_INT);
            prefHelper.setDefaultHomeScreenLayout(Constants.HOME_SCREEN_LIST);
            prefHelper.setDefaultCurrency(Constants.EMPTY_INT, EMPTY);
            prefHelper.setSelectedLang(EMPTY);
            prefHelper.saveDefaultTax(Constants.EMPTY_INT, EMPTY, 0.0f);

            Constants.API_KEY = EMPTY;
            Constants.API_SECRET = EMPTY;

        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
