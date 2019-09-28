package com.`in`.weareindian.quiknews.Adaptors

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.`in`.weareindian.quiknews.BuildConfig
import com.`in`.weareindian.quiknews.Main.ActivityHomePage
import com.`in`.weareindian.quiknews.ModelClasses.HomeActivity_API
import com.`in`.weareindian.quiknews.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_page_rec_cell.view.*
import java.io.File
import java.io.FileOutputStream


class AdapterHomePageRecyclerview(
    var activityHomePage: ActivityHomePage,
    var postList: ArrayList<HomeActivity_API>
) : RecyclerView.Adapter<AdapterHomePageRecyclerview.MyViewHolder>() {

    val folder_name = "QuiK news"
    private var imageFileToShare: File? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(activityHomePage).inflate(
                R.layout.home_page_rec_cell,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return postList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        deleteImage()

        val obj = postList.get(position)

        holder.text.text = postList.get(position).created
        Log.e("text", obj.created + obj.url)


        try {
            Picasso.get()
                .load(obj.url)
                //.fit()
                .into(holder.imageView)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("text", obj.created + obj.url)
        }


        fun downloadFile(uRl: String) {
            val direct = File(folder_name)

            if (!direct.exists()) {
                direct.mkdirs()
            }

            val mgr = activityHomePage.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadUri = Uri.parse(uRl)
            val request = DownloadManager.Request(
                downloadUri

            )

            request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            )
                .setAllowedOverRoaming(true).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir(folder_name, obj.id)

            mgr.enqueue(request)

            Toast.makeText(activityHomePage, "Image Downloaded", Toast.LENGTH_LONG).show()

        }

        holder.download.setOnClickListener {

            if (verifyAvailableNetwork(activityHomePage)) {
                downloadFile(obj.url)
            } else {
                Toast.makeText(
                    activityHomePage,
                    "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        holder.share.setOnClickListener {

            if (verifyAvailableNetwork(activityHomePage) && forceToAccessPath()) {
                val bitmap = Bitmap.createBitmap(
                    holder.imageView.width,
                    holder.imageView.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                var bgDrawable = holder.imageView.getBackground();
                if (bgDrawable != null) {
                    bgDrawable.draw(canvas)
                } else {
                    canvas.drawColor(Color.WHITE)
                }
                holder.imageView.draw(canvas)
//
//            val mBitmap = challange_view.drawingCache
                try {
                    val path = folder_name + "/${obj.id}.png"
                    val outputStream = FileOutputStream(File(path))
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                } catch (exception: Exception) {
                    Toast.makeText(
                        activityHomePage,
                        exception.message + "Could not share score",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                val imagePath: String = folder_name + "/${obj.id}.png"
                imageFileToShare = File(imagePath)
                val uri = Uri.fromFile(imageFileToShare)
                try {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "image/*"
                    share.putExtra(Intent.EXTRA_STREAM, uri)
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        "Checkout trending news on this amazing application \n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
                    )
                    activityHomePage.startActivity(Intent.createChooser(share, "QuiK news"))


                } catch (e: Exception) {
                    Toast.makeText(
                        activityHomePage,
                        e.message + "Could not share score",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            } else {
                Toast.makeText(
                    activityHomePage,
                    "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {

        val share = mView.home_page_rec_cell_share
        val download = mView.home_page_rec_cell_download
        val imageView = mView.home_page_rec_cell_image
        val text = mView.texttotest

    }


    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // forcefully allowes devices to give access to path to the sotred image
    fun forceToAccessPath(): Boolean {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        } else {
            Toast.makeText(activityHomePage, " Problem getting the image ", Toast.LENGTH_SHORT)
                .show()
            return false
        }
    }

    fun deleteImage(){
        // Because app crashes sometimes without the try->catch
        try {
            // if file exists in memory
            if (imageFileToShare!!.exists()) {
                imageFileToShare!!.delete()
                Log.e("delete", "image deleted :- $imageFileToShare")
            } else {
            }
        } catch (e: Exception) {
            Log.e("delete", e.toString())
        }

    }


}

