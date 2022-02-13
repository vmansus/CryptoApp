package com.example.myapplication;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ConfigDownloadActivity extends AppCompatActivity {
    EditText inputUrl;
    Button downloadButton;
    Button canceldownload;
    private long downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configdownload);

        inputUrl = (EditText) findViewById(R.id.inputUrl);
        downloadButton = (Button) findViewById(R.id.buttonDownload);
        canceldownload = (Button) findViewById(R.id.cancelDownload);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long boardcastDownload = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                System.out.println(boardcastDownload);
                System.out.println(downloadID);
                if(boardcastDownload == downloadID){
                    if(getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){
                        Toast.makeText(ConfigDownloadActivity.this, "DOWNLOAD COMPELETE", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConfigDownloadActivity.this, "DOWNLOAD NOT COMPELETE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, filter);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload(inputUrl.getText().toString());
            }
        });

        canceldownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDownload();
            }
        });

    }

    private int getDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            return status;
        }

        return DownloadManager.ERROR_UNKNOWN;
    }

    public void startDownload(String url){
        Uri uri = Uri.parse(url);
        System.out.println(uri);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("appConfig Download");
        request.setDescription("Download appConfig");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "aaa.txt");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
        System.out.println(downloadManager.getUriForDownloadedFile(downloadID));
        System.out.println(downloadID);
    }

    public void cancelDownload(){
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.remove(downloadID);
    }

    public File downloadFile(String filename)
    {
        OkHttpClient okhttp = new OkHttpClient();
        if(filename == null || filename.isEmpty())
            return null;

        FutureTask<File> task = new FutureTask<>(()->
        {
            ResponseBody responseBody = okhttp.newCall(
                    new Request.Builder().url("http://10.133.64.235:8787/download?filename=aaa.txt").build()
            ).execute().body();
            if(responseBody != null)
            {
                if(getExternalFilesDir(null) != null)
                {
                    File file = new File(getExternalFilesDir(null).toString() + "/" + filename);
                    try (
                            InputStream inputStream = responseBody.byteStream();
                            FileOutputStream outputStream = new FileOutputStream(file)
                    )
                    {
                        byte[] b = new byte[1024];
                        int n;
                        if((n = inputStream.read(b)) != -1)
                        {
                            outputStream.write(b,0,n);
                            while ((n = inputStream.read(b)) != -1)
                                outputStream.write(b, 0, n);
                            return file;
                        }
                        else
                        {
                            file.delete();
                            return null;
                        }
                    }
                }
            }
            return null;
        });
        try
        {
            new Thread(task).start();
            return task.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
