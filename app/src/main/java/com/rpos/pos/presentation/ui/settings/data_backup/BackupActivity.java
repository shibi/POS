package com.rpos.pos.presentation.ui.settings.data_backup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.rpos.pos.R;
import com.rpos.pos.domain.utils.DatabaseHelp;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.io.File;

public class BackupActivity extends SharedActivity {

    private LinearLayout ll_back;
    private CardView cardLocalBackUp, cardLocalImport, cardExportToExcel, cardBackupToDrive;
    private DatabaseHelp databaseHelp;
    private final int PERMISSION_READ_STORAGE = 102;

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

        cardLocalBackUp = findViewById(R.id.card_local_backup);
        cardLocalImport = findViewById(R.id.card_local_db_import);
        cardExportToExcel = findViewById(R.id.card_export_to_excel);
        cardBackupToDrive = findViewById(R.id.card_backup_to_drive);


        checkReadStoragePermissionAvailable();


        cardLocalBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hasStoragePermissionGranted()) {
                    String outFileName = Environment.getExternalStorageDirectory() + File.separator + "POS/";
                    databaseHelp.exportDB(outFileName);
                }
            }
        });

        cardLocalImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseHelp.performRestore();
            }
        });

        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void initObservers() {

    }

    private boolean hasStoragePermissionGranted(){
        return ((ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
    }

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

    private void bacupUp(){

        /*final RoomBackup roomBackup = new RoomBackup(MainActivityJava.this);
        roomBackup.database(FruitDatabase.Companion.getInstance(getApplicationContext()));
        roomBackup.enableLogDebug(enableLog);
        roomBackup.backupIsEncrypted(encryptBackup);
        roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_INTERNAL);
        roomBackup.maxFileCount(5);
        roomBackup.onCompleteListener((success, message, exitCode) -> {
            Log.d("TAG", "success: " + success + ", message: " + message + ", exitCode: " + exitCode);
            if (success) roomBackup.restartApp(new Intent(getApplicationContext(), MainActivity.class));
        });
        roomBackup.backup();*/

    }
}