package com.example.listapp.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.*
import com.example.listapp.BaseApplication
import com.example.listapp.R
import com.example.listapp.utils.Constants
import com.example.listapp.utils.Constants.SHARED_PREFERENCE
import com.example.listapp.utils.Status
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.data_item.*
import java.util.jar.Manifest

import java.lang.Error
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),ClickDownload {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var adapter: DataItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = DataItemAdapter(this@MainActivity)
        rvData.layoutManager = LinearLayoutManager(this)
        rvData.adapter = adapter

        mainViewModel.res.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.visibility = View.GONE
                    rvData.visibility = View.VISIBLE
                    it.data?.let { res ->
                            adapter.submitList(res)
                    }
                }
                Status.LOADING -> {
                    progress.visibility = View.VISIBLE
                    rvData.visibility = View.GONE
                }
                Status.ERROR -> {
                    progress.visibility = View.GONE
                    rvData.visibility = View.VISIBLE
                    Snackbar.make(rootView, "Something went wrong", Snackbar.LENGTH_SHORT).show()
                }
                Status.EMPTY->
                {
                    progress.visibility = View.GONE
                    rvData.visibility = View.GONE
                    tv_empty.visibility = View.VISIBLE
                }
            }
        })
    }



    override fun download(url:String) {

        //check Permission
        if(Build.VERSION.SDK_INT>23) {
            if (hasPermission()) {
                //download file
                startDownload(url)
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 25)
            }
        }
         else
            {
                //download file
                startDownload(url)

            }

    }


    fun hasPermission():Boolean
    {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
    }


    fun startDownload(url:String)
    {
        var downloadId = 0

        val dialogProgress = ProgressDialog(this)
        dialogProgress.setTitle("Download...")
        dialogProgress.setMessage("Preparing...")
        dialogProgress.setCancelable(false)
        dialogProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
      /*  dialogProgress.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",
            DialogInterface.OnClickListener{dialogProgress,which->

                dialogProgress.dismiss()
                PRDownloader.cancel(downloadId)
            })
        dialogProgress.show()*/

        // Enabling database for resume support even after the application is killed:
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(applicationContext, config)


       downloadId = PRDownloader.download(url, directory(), fileName(url))
            .build()
            .setOnStartOrResumeListener {
                dialogProgress.setTitle("Download Started...")
            }
            .setOnPauseListener { }
            .setOnCancelListener {
      //          Toast.makeText(this,"Download Canceled",Toast.LENGTH_SHORT).show()
            }
            .setOnProgressListener {

                progress->
                val progressPrecent :Long = progress.currentBytes * 100 / progress.totalBytes
                dialogProgress.progress = progressPrecent.toInt()
                dialogProgress.setMessage(toMB(progress.currentBytes) + "/" + toMB(progress.totalBytes))
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    dialogProgress.setTitle("Download Completed")
                    Toast.makeText(this@MainActivity,"Download Completed",Toast.LENGTH_SHORT).show()
                    dialogProgress.dismiss()
                }
                override fun onError(error: com.downloader.Error?) {
                    PRDownloader.cancel(downloadId)
                    Toast.makeText(this@MainActivity,"Download Error",Toast.LENGTH_SHORT).show()
                    dialogProgress.dismiss()

                }

            })

    }


    fun directory():String
    {
       return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    }


    fun fileName(url: String):String
    {
        return URLUtil.guessFileName(url,url,contentResolver.getType(Uri.parse(url)))
    }


    fun privateDirectory():String
    {
        return filesDir.absolutePath
    }


    private fun toMB(bytes: Long): String? {
        return java.lang.String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }

}

