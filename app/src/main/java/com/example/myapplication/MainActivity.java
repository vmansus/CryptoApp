package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity {

    private AndroidWebServer server;
    private long downloadID;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    EditText inputUrl;
    Button mode,rules,download,downloadButton,canceldownload;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            server = new AndroidWebServer();


        } catch (IOException e) {
            e.printStackTrace();
        }

        this.url=server.getUrl();
        System.out.println("ceshihhhhhhhh");
        System.out.println("url=="+url);

        if(this.url!=null){
            System.out.println(url);
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                    String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //Permission Not Granted

                }else
                    startDownloading();


            }else
                startDownloading();
        }


//        mode=findViewById(R.id.mode_btn);
//        rules=findViewById(R.id.rules_btn);
//        download=findViewById(R.id.download_btn);
//        inputUrl = (EditText) findViewById(R.id.inputUrl);
//        downloadButton = (Button) findViewById(R.id.buttonDownload);
//        canceldownload = (Button) findViewById(R.id.cancelDownload);

//        mode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),ModeManagement.class);
//                startActivity(i);
//            }
//        });
//
//        rules.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),RulesManagement.class);
//                startActivity(i);
//            }
//        });
//
//        download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ConfigDownloadActivity.class);
//                startActivity(i);
//            }
//        });
//
//        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
////                long boardcastDownload = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
////                if(boardcastDownload == downloadID){
//                    if(getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){
//                        Toast.makeText(MainActivity.this, "DOWNLOAD COMPELETE", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "DOWNLOAD NOT COMPELETE", Toast.LENGTH_SHORT).show();
//                    }
////                }
//            }
//        }, filter);
//
//
//        downloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    //申请WRITE_EXTERNAL_STORAGE权限
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            1);
//
//                }
//
//
//                startDownload(inputUrl.getText().toString());
//            }
//        });
//
//        canceldownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                cancelDownload();
//            }
//        });

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
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Image Download");
            request.setDescription("Download imagethis，");
            request.setDestinationInExternalFilesDir(this, "/storage/emulated/0/testfile", "aaa.txt");
//            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "aaa.txt");

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);

    }

    public void cancelDownload(){
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.remove(downloadID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {



        super.onResume();
//        try {
//            server = new AndroidWebServer();
//            this.url=server.configUrl;
//            System.out.println("url=="+url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(server != null) {
            server.stop();
        }
    }


    public void startDownloading(){
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(this.url.trim()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("Downloading File....");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"aaa.txt");
        DownloadManager manager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDownloading();
        } else {
            Toast.makeText(MainActivity.this,  "Permission Denied, You cannot access storage.", Toast.LENGTH_LONG).show();
        }
    }

}
