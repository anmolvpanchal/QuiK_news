package com.`in`.weareindian.quiknews.Main

import android.Manifest.permission.*
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.`in`.weareindian.quiknews.Adaptors.AdapterHomePageRecyclerview
import com.`in`.weareindian.quiknews.Api.ApiClient
import com.`in`.weareindian.quiknews.Api.ServiceGenerator
import com.`in`.weareindian.quiknews.ModelClasses.HomeActivity_API
import com.`in`.weareindian.quiknews.R
import kotlinx.android.synthetic.main.activity_home_page.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.R.attr.description
import android.R.id
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.util.jar.Manifest


class ActivityHomePage : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false // to close app
    val getPostList: ArrayList<HomeActivity_API> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // main function
        methodWithPermissions()
        getTopPosts()


        // this is to set the colors of refreshing
        swipeToRefresh.setColorSchemeResources(
            android.R.color.holo_orange_light,
            android.R.color.holo_green_light,
            android.R.color.holo_blue_bright,
            android.R.color.holo_red_light
        )

        swipeToRefresh.setOnRefreshListener {
            getTopPosts()
        }


    }

    fun methodWithPermissions() = runWithPermissions( WRITE_EXTERNAL_STORAGE, INTERNET, READ_EXTERNAL_STORAGE) {
        Log.e("permissions","permissions granted")
    }

    fun getTopPosts() {
        var progressDialog: ProgressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading Content");
        progressDialog.setMessage("Please Wait !");
        progressDialog.setCancelable(false);
        progressDialog.show();

        val apiClient = ServiceGenerator.createService(ApiClient::class.java)
        val call = apiClient.getTopPosts()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                swipeToRefresh.isRefreshing = false
                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show()
                Log.e("error on faliur", t.toString())
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                swipeToRefresh.isRefreshing = false

                if (!response.isSuccessful()) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Something went Wrong !!" + response.code(),
                        Toast.LENGTH_SHORT
                    ).show();
                    Log.e("!response.isSuccessful","body \n"
                            + response.errorBody().toString()
                            +" code ${response.code()}")
                    return;
                }

                try {

                    getPostList.clear()

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val records = rootObj.getJSONArray("records")
                    for (i in 0 until records.length()) {

                        val innerobject: JSONObject = records.getJSONObject(i)
                        val id = innerobject.getString("id")
                        val url = innerobject.getString("url")
                        val created = innerobject.getString("created")
                        getPostList.add(HomeActivity_API(id,url,created))


                    }

                    home_page_recyclerview.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@ActivityHomePage, 1)
                    home_page_recyclerview.adapter = AdapterHomePageRecyclerview(this@ActivityHomePage,getPostList)
                    (home_page_recyclerview.adapter as AdapterHomePageRecyclerview).notifyDataSetChanged()


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }

            }
        })

    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {    // checking the amount of times back pressed
            super.onBackPressed()
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true

        val toast: Toast = Toast.makeText(
            this,
            "Please click BACK again to exit",
            Toast.LENGTH_SHORT
        );  // to show toast in center
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show()

        Handler().postDelayed(
            Runnable { doubleBackToExitPressedOnce = false },
            2000
        ) // time in which second back should press and quit app


    }

}
