package com.rpos.pos.domain.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.rpos.pos.Config;
import com.rpos.pos.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Utility {


    private static Typeface fromAsset;
    private static SpannableString spannableString;
    private static Fonts currentTypeface;

    //email validate pattern
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]*\\.*[a-zA-Z0-9._]+@[a-z]*\\.*[a-z]+\\.+[a-z]{3}";

    /**
     * to show/hide password text in the field
     * */
    public static void setHideShowPassword(final EditText edtPassword) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                edtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_baseline_remove_red_eye_24,0);
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

            edtPassword.setTag("show");
            edtPassword.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int DRAWABLE_RIGHT = 2;


                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtPassword.getRight() - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (edtPassword.getTag().equals("show")) {

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_hide, 0);
                                edtPassword.setTag("hide");
                                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                            } else {

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24, 0);
                                edtPassword.setTag("show");
                                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                            }

                            edtPassword.setSelection(edtPassword.getText().length());

                            return true;
                        }
                    }
                    return false;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show/hide password text in the field
     * */
    public static void setHideShowPassword(final EditText edtPassword, int drawableLeft) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                edtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft,0, R.drawable.ic_baseline_remove_red_eye_24,0);
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

            edtPassword.setTag("show");
            edtPassword.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int DRAWABLE_RIGHT = 2;


                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtPassword.getRight() - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (edtPassword.getTag().equals("show")) {

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, R.drawable.ic_eye_hide, 0);
                                edtPassword.setTag("hide");
                                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                            } else {

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, R.drawable.ic_baseline_remove_red_eye_24, 0);
                                edtPassword.setTag("show");
                                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                            }

                            edtPassword.setSelection(edtPassword.getText().length());

                            return true;
                        }
                    }
                    return false;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * to change status bar color,
     * this will give the app a fullscreen look
     * */
    public static void changeStatusBarColor(Activity activity, int colorId){
        try {

            if (Build.VERSION.SDK_INT >= 21) {

                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(activity.getResources().getColor(colorId));

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To set small hint for common edittext
     * */
    public static void setSmallHintText(EditText editText, String hintText){
        try{

            editText.setHint(Html.fromHtml("<small>" + hintText + "</small>"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to set the screen go fullscreen
     * */
    public static void fullScreenCall(Activity activity) {

        try {

            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api

                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);

            } else if (Build.VERSION.SDK_INT >= 19) {

                //for new api versions.
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);

            }

        }catch (Exception e){
            throw e;
        }
    }


    public static void showDatePicker(final EditText et_dob_field, Context context){

        try {

            final Calendar newCalendar = Calendar.getInstance();
            final DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    try {

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String myFormat = "dd-MM-yyyy";
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);
                        String str_date = sdf.format(newDate.getTime());
                        et_dob_field.setText(str_date);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            //datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePicker.show();

        }catch (Exception e){
            throw e;
        }
    }

    //for filtering the username field for special characters
    private InputFilter SPECIAL_CHAR_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            try {

                for (int counter = start; counter < end; counter++) {

                    //check the entered char is a letter or digit
                    if (Character.isLetterOrDigit(source.charAt(counter))) {
                        return "";
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    };


    //for filtering the username start-
    public static InputFilter FILTER_USERNAME = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            //initialy set the falg true
            boolean keepOriginal = true;

            StringBuilder sb = new StringBuilder(end - start);

            for (int index = start; index < end; index++) {
                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
                char ch = source.charAt(index);

                if (isCharacterAllowed(ch))
                    //if entered the string char, appnd to the string builder
                    sb.append(ch);
                else

                    //else set the flag false
                    keepOriginal = false;
            }

            //if the flag set false, return the string
            if (keepOriginal)
                return null;

            else {
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(sb);
                    TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                    return sp;
                } else {
                    return sb;
                }
            }
        }
    };


    //check the char entered is a digit,letter or space bar
    private static boolean isCharacterAllowed(char c) {
        return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
    }


    //for filtering the first name and last name field for characters.
    public static InputFilter EMOJI_FILTER = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //initialy set the flag true
            boolean keepOriginal = true;
            //initialise the string builder
            StringBuilder sb = new StringBuilder(end - start);

            for (int index = start; index < end; index++) {
                int type = Character.getType(source.charAt(index));

                //check the entered char is a special symbol or not
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
                //get the char from entered
                char c = source.charAt(index);
                //check which is allowed to return
                if (isCharAllowed(c))
                    //if, yes append the string
                    sb.append(c);
                else
                    //else return the flag as false
                    keepOriginal = false;
            }

            //the flag set false, return the appended string
            if (keepOriginal)
                return null;

            else {
                if (source instanceof Spanned) {
                    //return the string entered
                    SpannableString sp = new SpannableString(sb);
                    TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                    return sp;
                } else {
                    return sb;
                }
            }
        }
    };


    /*public static Drawable createBadgeDrawable(int count, int backgroundImageId, Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.badge_layout_panel, null);
        view.setBackgroundResource(backgroundImageId);

        // System.out.println("=============count " + count);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setVisibility(View.VISIBLE);
            textView.setText("" + count);
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(activity.getResources(), bitmap);
    }
*/
    /**
     * retrun the flag as true, ehen entered char is a letter
     * @param c
     */
    private static boolean isCharAllowed(char c) {
        try {
            return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
        } catch (Exception e) {
            throw e;
        }
    }

    public static void logger(String msg){
        if(Config.LOG) {
            //Log.e("--------------", msg);
            Log.e("looger>", msg);
        }
    }

    public static String base64Encrypt(String keyString){
        return Base64.encodeToString(keyString.getBytes(), Base64.NO_WRAP);
    }

    public static String base64Decrypt(String keyString){
        byte[] bytes1 =  Base64.decode(keyString.getBytes(), Base64.NO_WRAP);
        return new String(bytes1);
    }


    public enum Fonts {
        ROBOTO,
        NOTO_SANS,
        ROBOTO_LIGHT,
        ROBOTO_MEDIUM,
        MM_FONT
    }

    public static void addToolbarScrollFlag(Toolbar toolbar) {
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    public static void removeToolbarScrollFlag(Toolbar toolbar) {
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
    }

    //item image upload
    public static byte[] compressImage(File file, Uri uri, ContentResolver contentResolver, float uploadImageHeight, float uploadImageWidth) {
//    private File compressImage(File file) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        String filePath = file.getPath();

//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        Bitmap bmp = null;
        try {
//            InputStream stream = contentResolver.openInputStream(Config.globalUri);
//            bmp = BitmapFactory.decodeStream(stream, null, options);

            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            parcelFileDescriptor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        if(actualHeight > uploadImageHeight) {

            int tmpWidth = actualWidth;
            float diff = uploadImageHeight/(float) actualHeight;
            actualWidth = (int)  (diff * tmpWidth);
            actualHeight = (int) uploadImageHeight;

        }
        if(actualWidth > uploadImageWidth){

            int tmpHeight = actualHeight;
            float diff = uploadImageWidth/ (float)actualWidth;
            actualHeight = (int) (diff * tmpHeight);
            actualWidth = (int) uploadImageWidth;

        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
//            bmp = BitmapFactory.decodeFile(filePath, options);
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            parcelFileDescriptor.close();
        } catch (Exception exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String tmpPath = file.getPath().toLowerCase();
            if(tmpPath.contains("png")) {
                return saveImage(contentResolver, scaledBitmap, "tmp_image.png", "png");
            }else if(tmpPath.contains("jpg") || tmpPath.contains("jpeg")) {
                return saveImage(contentResolver, scaledBitmap, "tmp_image.png", "jpg");
            }else if(tmpPath.contains("webp")) {
                return saveImage(contentResolver, scaledBitmap, "tmp_image.png", "webp");
            }else {
                return saveImage(contentResolver, scaledBitmap, "tmp_image.png", "jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static byte[] saveImage(ContentResolver contentResolver, Bitmap bitmap, @NonNull String name, String type) throws IOException {
//        private File saveImage(ContentResolver contentResolver, Bitmap bitmap, @NonNull String name) throws IOException {
        OutputStream fos;
        File image;
        Uri imageUri = null;
        byte[] inputData;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = contentResolver;
            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name );
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

            image = new File(imageUri.toString());

        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            image = new File(imagesDir, name + "." + type);
            fos = new FileOutputStream(image);
            image.toURI();
        }
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        switch (type) {
            case "png":
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
                break;
            case "webp":
                bitmap.compress(Bitmap.CompressFormat.WEBP, 80, fos);
                break;
            default:
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                break;
        }

        Objects.requireNonNull(fos).close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InputStream iStream = contentResolver.openInputStream(Objects.requireNonNull(imageUri));
            inputData = getBytes(iStream);
            contentResolver.delete(imageUri, "", null);
        }else {

            inputData = readBytesFromFile(image.getPath());

        }
        return inputData;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;
    }


}
