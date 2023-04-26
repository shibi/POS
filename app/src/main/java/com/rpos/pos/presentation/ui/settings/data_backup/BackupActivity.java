package com.rpos.pos.presentation.ui.settings.data_backup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.rpos.pos.BuildConfig;
import com.rpos.pos.Config;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DatabaseHelp;
import com.rpos.pos.domain.utils.File.FileHelper;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.rpos.pos.Config.BACKUP_DIRECTORY_NAME;


public class BackupActivity extends SharedActivity {

    private LinearLayout ll_back;
    private CardView cardLocalBackUp, cardLocalImport, cardBackupToDrive;
    private DatabaseHelp databaseHelp;
    private final int PERMISSION_READ_STORAGE = 102;

    private String BACKUP_RESTORE_ROLLBACK_FILE_NAME = "db_restore_temp";

    private AppDialogs progressDialog;

    @Override
    public int setUpLayout() {
        return R.layout.activity_backup;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        databaseHelp = new DatabaseHelp(this);
        progressDialog = new AppDialogs(this);

        cardLocalBackUp = findViewById(R.id.card_local_backup);
        cardLocalImport = findViewById(R.id.card_local_db_import);
        //cardExportToExcel = findViewById(R.id.card_export_to_excel);
        cardBackupToDrive = findViewById(R.id.card_backup_to_drive);

        //check permission read and write
        checkReadStoragePermissionAvailable();

        //listener for backup
        cardLocalBackUp.setOnClickListener(v -> {
            //check storage permission granted
            if(hasStoragePermissionGranted()) {
                //backup database
                backupDatabase(BackupActivity.this);
            }
        });

        //listener for restore db click
        cardLocalImport.setOnClickListener(this::onRestoreClicked);

        cardBackupToDrive.setOnClickListener(this::showDBFilePicker);

        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    /**
     * to check storage permission granted
     * */
    private boolean hasStoragePermissionGranted(){
        return ((ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
    }

    /**
     * to check permission and request permission
     * */
    private boolean checkReadStoragePermissionAvailable() {

        if (hasStoragePermissionGranted()) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSION_READ_STORAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    /**
     * to backup database
     * @param context
     * */
    public void backupDatabase(Context context) {

        progressDialog.showProgressBar();

        String FILE_NAME = "db";
        AppDatabase appDatabase = getCoreApp().getLocalDb();
        appDatabase.close();
        File dbfile = context.getDatabasePath(Config.DB_NAME);
        File sdir = new File(getBackupDirectory(), BACKUP_DIRECTORY_NAME);
        String fileName = FILE_NAME + System.currentTimeMillis();
        String sfpath = sdir.getPath() + File.separator + fileName;
        if (!sdir.exists()) {
            sdir.mkdirs();
        } else {
            //Directory Exists. Delete a file if count is 5 already. Because we will be creating a new.
            //This will create a conflict if the last backup file was also on the same date. In that case,
            //we will reduce it to 4 with the function call but the below code will again delete one more file.
            checkAndDeleteBackupFile(sdir, sfpath);
        }
        File savefile = new File(sfpath);
        if (savefile.exists()) {
            Log.d("LOGGER", "File exists. Deleting it and then creating new file.");
            savefile.delete();
        }
        try {
            if (savefile.createNewFile()) {
                int buffersize = 8 * 1024;
                byte[] buffer = new byte[buffersize];
                int bytes_read = buffersize;
                OutputStream savedb = new FileOutputStream(sfpath);
                InputStream indb = new FileInputStream(dbfile);
                while ((bytes_read = indb.read(buffer, 0, buffersize)) > 0) {
                    savedb.write(buffer, 0, bytes_read);
                }
                savedb.flush();
                indb.close();
                savedb.close();
                SharedPrefHelper sharedPreferences = SharedPrefHelper.getInstance(BackupActivity.this);
                //sharedPreferences.putString("backupFileName", fileName).apply();
                sharedPreferences.setLastDBSavedTime(System.currentTimeMillis());

                try {

                    new CountDownTimer(2000,1000){

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            progressDialog.hideProgressbar();
                            showToast(getString(R.string.db_export_success));
                        }
                    }.start();

                }catch (Exception e){
                    e.printStackTrace();
                }


            }else {
                progressDialog.hideProgressbar();
                showToast(getString(R.string.db_file_creation_failed));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.db_unable_to_export));
            Log.d("LOGGER", "ex: " + e);
            progressDialog.hideProgressbar();
        }
    }

    /**
     * to manage backup count by deleting , if the number of backup file exceeded the Max limit
     * */
    public static void checkAndDeleteBackupFile(File directory, String path) {
        //This is to prevent deleting extra file being deleted which is mentioned in previous comment lines.
        File currentDateFile = new File(path);
        int fileIndex = -1;
        long lastModifiedTime = System.currentTimeMillis();

        if (!currentDateFile.exists()) {
            File[] files = directory.listFiles();
            if (files != null && files.length >= Config.MAXIMUM_DATABASE_FILE) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    long fileLastModifiedTime = file.lastModified();
                    if (fileLastModifiedTime < lastModifiedTime) {
                        lastModifiedTime = fileLastModifiedTime;
                        fileIndex = i;
                    }
                }

                if (fileIndex != -1) {
                    File deletingFile = files[fileIndex];
                    if (deletingFile.exists()) {
                        deletingFile.delete();
                    }
                }
            }
        }
    }

    /**
     * to backup data before the restore
     * */
    public void backupDatabaseForRestore(Activity activity, Context context) {

        File dbfile = activity.getDatabasePath(Config.DB_NAME);
        File sdir = new File(getBackupDirectory(), BACKUP_DIRECTORY_NAME);
        String sfpath = sdir.getPath() + File.separator + BACKUP_RESTORE_ROLLBACK_FILE_NAME;
        if (!sdir.exists()) {
            sdir.mkdirs();
        }
        File savefile = new File(sfpath);
        if (savefile.exists()) {
            Log.d("LOGGER", "Backup Restore - File exists. Deleting it and then creating new file.");
            savefile.delete();
        }
        try {
            if (savefile.createNewFile()) {
                int buffersize = 8 * 1024;
                byte[] buffer = new byte[buffersize];
                int bytes_read = buffersize;
                OutputStream savedb = new FileOutputStream(sfpath);
                InputStream indb = new FileInputStream(dbfile);
                while ((bytes_read = indb.read(buffer, 0, buffersize)) > 0) {
                    savedb.write(buffer, 0, bytes_read);
                }
                savedb.flush();
                indb.close();
                savedb.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LOGGER", "ex for restore file: " + e);
        }
    }

    /**
     * to restore database
     * step 1 , saves a backup of current database before restore.
     * step 2 , restores backup
     * */
    private void restoreDatabase(InputStream inputStreamNewDB) {
        //progress
        progressDialog.showProgressBar();
        //room db instance
        AppDatabase appDatabase = getCoreApp().getLocalDb();
        appDatabase.close();

        //Delete the existing restoreFile and create a new one.
        backupDatabaseForRestore(this, getApplicationContext());

        //current db
        File oldDB = this.getDatabasePath(Config.DB_NAME);
        if (inputStreamNewDB != null) {
            try {

                FileHelper.copyFile((FileInputStream) inputStreamNewDB, new FileOutputStream(oldDB));
                //Take the user to home screen and there we will validate if the database file was actually restored correctly.
            } catch (IOException e) {
                Log.d("LOGGER", "ex for is of restore: " + e);
                e.printStackTrace();
            }
        } else {
            Log.d("LOGGER", "Restore - file does not exists");
        }

        progressDialog.hideProgressbar();
    }

    /**
     * manage restore on click
     * */
    private void onRestoreClicked(View view){
        try {

            File backupSourceDirectory = new File(getBackupDirectory(), BACKUP_DIRECTORY_NAME);

            FileHelper.fileChooser(BackupActivity.this, backupSourceDirectory,getString(R.string.restore), new FileHelper.FileSelectionListener() {
                @Override
                public void onFileReceive(File file) {
                    try {

                        FileInputStream inputStream = new FileInputStream(file);
                        restoreDatabase(inputStream);

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to get the public directory to save and retrieve db backup
     * saving data in document folder (directory type : 1)
     * */
    private File getBackupDirectory(){
        return getCoreApp().getPublicDirectory(1); // 1 for document folder
    }

    private void showDBFilePicker(View view){
        try {

            File sourceDirectory = new File(getBackupDirectory(), BACKUP_DIRECTORY_NAME);

            FileHelper.fileChooser(BackupActivity.this, sourceDirectory,getString(R.string.choose_file), new FileHelper.FileSelectionListener() {
                @Override
                public void onFileReceive(File file) {
                    shareFile(file.getPath());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void shareFile(String filePath) {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            File fileWithinMyDir = new File(filePath);

            if (fileWithinMyDir.exists()) {

                Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", fileWithinMyDir);

                intentShareFile.setType("application/*");
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intentShareFile, getString(R.string.share_file)));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(String msg){
        showToast(msg, BackupActivity.this);
    }
}