package com.rpos.pos.domain.utils.File;

import android.content.Context;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.rpos.pos.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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

    public static void fileChooser(Context context, File directory, String title, FileSelectionListener listener){

        File folder = directory;//new File(filePath + File.separator + directory);
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

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
