package com.rpos.pos.domain.utils.File;

import android.content.Context;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.rpos.pos.R;
import com.rpos.pos.domain.utils.DateTimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FileHelper {

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    /**
     * to list files in a folder for selection
     * returns a selected file from the list
     * */
    public static void fileChooser(Context context, File directory, String title, FileSelectionListener listener){

        File folder = directory;//new File(filePath + File.separator + directory);
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            if(files !=null && files.length>1) {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        //return Long.compare(f1.lastModified(), f2.lastModified());
                        // For descending
                         return -Long.compare(f1.lastModified(), f2.lastModified());
                    }
                });
            }


            //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.view_backup_items_row, R.id.text1);
            SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss") ;
            String date;
            String nameWithDate;

            for (File file : files) {
                date = sm.format(new Date(file.lastModified()));
                nameWithDate = file.getName() + " \n"+ date;
                arrayAdapter.add(nameWithDate);
            }

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setTitle(title);
            builderSingle.setNegativeButton(
                    R.string.btn_cancel,
                    (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(
                    arrayAdapter,
                    (dialog, which) -> {
                        try {
                            //importDB(files[which].getPath());
                            listener.onFileReceive(files[which]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Unable to pick file", Toast.LENGTH_SHORT).show();
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(context, context.getResources().getString(R.string.backup_folder_not_found), Toast.LENGTH_SHORT).show();
    }

    public interface FileSelectionListener{
        void onFileReceive(File file);
    }
}
