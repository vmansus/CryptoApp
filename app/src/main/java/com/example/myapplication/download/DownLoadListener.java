package com.example.myapplication.download;

import java.io.File;

public interface DownLoadListener {

    void OnDownloadStart();
    void OnDownloadComplete(File file);
    void OnDownloadfailed();
    void OnDownLoadProgress(int progress);

}