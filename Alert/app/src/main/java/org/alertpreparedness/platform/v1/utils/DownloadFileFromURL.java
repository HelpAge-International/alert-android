package org.alertpreparedness.platform.v1.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;

import org.alertpreparedness.platform.v1.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Tj on 21/02/2018.
 */

public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private Context context;
    private String fileName;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public DownloadFileFromURL(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNotifyManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("File Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.download);
        mBuilder.setProgress(100, 0, false);
        // Displays the progress bar on notification
        mNotifyManager.notify(0, mBuilder.build());

    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString()
                    + "/" + fileName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        System.out.println("progress = [" + Integer.parseInt(progress[0]) + "]");
        System.out.println("onProgressUpdateupdating");
        if(Integer.parseInt(progress[0]) < 100) {
            mBuilder.setProgress(100, Integer.parseInt(progress[0]), false);
            // Displays the progress bar on notification
            mNotifyManager.notify(0, mBuilder.build());

        }
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        mBuilder.setContentText("Download complete");
        mBuilder.setSmallIcon(R.drawable.ic_done);
        mBuilder.setProgress(100,100,false);

        File file = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/" + fileName);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);

        if (type == null)
            type = "*/*";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);

        intent.setDataAndType(data, type);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));

        mNotifyManager.cancel(0);
        mNotifyManager.notify(1, mBuilder.build());

    }

}
