package com.rpos.pos.domain.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.rpos.pos.Constants;

import java.util.Locale;

public class LocalHelper {

    public static Context updateBaseContextLocale(Context context) {

        String language = SharedPrefHelper.getInstance(context).getSelectedLang();//it return "en", "ar" like this
        if (language == null || language.isEmpty()) {
            //when first time enter into app (get the device language and set it
            language = Locale.getDefault().getLanguage();
            if (language.equals("ar")) {
                SharedPrefHelper.getInstance(context).setSelectedLang(Constants.LANG_AR);
            }
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale);
            return  updateResourcesLocaleLegacy(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
