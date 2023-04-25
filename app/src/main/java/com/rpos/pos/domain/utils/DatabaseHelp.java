package com.rpos.pos.domain.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.rpos.pos.Config;
import com.rpos.pos.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DatabaseHelp{

    private Context mContext;

    public DatabaseHelp(Context context) {
        this.mContext = context;
    }

    public void backup(String outFileName) {

        File folder = new File(outFileName);

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();

        if(!success){
            Toast.makeText(mContext, "Backup file creation failed", Toast.LENGTH_SHORT).show();
            return;
        }

        outFileName = outFileName + "p_backup"+System.currentTimeMillis()+".db";

        //database path
        final String inFileName = mContext.getApplicationContext().getDatabasePath(Config.DB_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            Log.e("---------","0>>>"+dbFile.canWrite());
            Log.e("---------","1>>>"+dbFile.canRead());
            dbFile.setWritable(false);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);


            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Database export success", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Database export failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void exportDB(String outputFolder) {

        OutputStream output = null;
        FileInputStream fis = null;
        try {

            File folder = new File(outputFolder);

            boolean success = true;
            if (!folder.exists())
                success = folder.mkdirs();

            if(!success){
                Toast.makeText(mContext, "Backup file creation failed", Toast.LENGTH_SHORT).show();
                return;
            }

            File dbFile = new File(mContext.getDatabasePath(Config.DB_NAME).getAbsolutePath());
            fis = new FileInputStream(dbFile);

            String outFileName = outputFolder + File.separator +
                    Config.DB_NAME + ".db";

            // Open the empty db as the output stream
            output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            Toast.makeText(mContext, "sdfsdfsdf",Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("dbBackup:", e.getMessage());
        }finally {
            try {
                // Close the streams
                output.flush();
                output.close();
                fis.close();

            }catch (Exception e){
                e.getMessage();
            }
        }
    }



    //ask to the user what backup to restore
    public void performRestore() {

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "POS/");
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
            builderSingle.setTitle("Restore");
            builderSingle.setNegativeButton(
                    R.string.btn_cancel,
                    (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(
                    arrayAdapter,
                    (dialog, which) -> {
                        try {
                            importDB(files[which].getPath());
                        } catch (Exception e) {
                            Toast.makeText(mContext, "Unable to restore", Toast.LENGTH_SHORT).show();
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(mContext, "Backup folder not found", Toast.LENGTH_SHORT).show();
    }

    public void importDB(String inFileName) {

        final String outFileName = mContext.getDatabasePath(Config.DB_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Import completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to import database entry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
