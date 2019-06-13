package com.solar.huong.demookhttp

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_get_url.*
import okhttp3.*
import java.io.File
import java.util.concurrent.TimeUnit

class GetPostMethodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_url)
        btn_check_url.setOnClickListener {
            GetUrl().execute("http://" + edt_set_url.text.toString().trim())
        }
    }

    inner class GetUrl : AsyncTask<String, String, String>() {
        private var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        override fun doInBackground(vararg params: String?): String {
            val builder = Request.Builder()
            builder.url(params[0]!!)
            var request = builder.build()
            var response = okHttpClient.newCall(request).execute()
            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            if (!result.equals("")) {
                txt_url.append(result)
            } else {
                Toast.makeText(this@GetPostMethodActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
            super.onPostExecute(result)
        }

    }

    inner class GetImage : AsyncTask<String, Void, ByteArray>() {
        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()

        override fun doInBackground(vararg params: String?): ByteArray {
            val builder = Request.Builder()
            builder.url(params[0]!!)
            val request = builder.build()
            val response = okHttpClient.newCall(request).execute()
            return response.body()!!.bytes()
        }

        override fun onPostExecute(result: ByteArray?) {
            if (result?.size!! > 0) {
                var bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                // tra ve bitmap
                lateinit var x: ImageView
                x.setImageBitmap(bitmap)
            }
            super.onPostExecute(result)
        }
    }

    // du lieu truyen vao - du lieu xu ly - ket qua tra ve
    inner class PostToServer() : AsyncTask<String, Void, String>() {
        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()
        var user: String = ""
        var password: String = ""

        constructor(user: String, password: String) : this() {
            this.user = user
            this.password = password
        }

        override fun doInBackground(vararg params: String?): String {
            var requestBody = MultipartBody.Builder()
                .addFormDataPart("username", user)
                .addFormDataPart("password", password)
                .setType(MultipartBody.FORM)
                .build()

            var request = Request.Builder()
                .url(params[0]!!)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            // result
            super.onPostExecute(result)
        }
    }

    var path = ""

    inner class PostFile() : AsyncTask<String, Void, String>() {
        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()

        override fun doInBackground(vararg params: String?): String {
            var file = File(path)
            var content_type = getType(file.path)
            var file_path = file.absolutePath

            var fileBody = RequestBody.create(MediaType.parse(content_type), file)
            var requestBody = MultipartBody.Builder()
                .addFormDataPart("upload", file_path.substring(file_path.lastIndexOf("/") + 1), fileBody)
                .setType(MultipartBody.FORM)
                .build()

            var request = Request.Builder()
                .url(params[0]!!)
                .post(requestBody)
                .build()

            var response = okHttpClient.newCall(request).execute()
            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(this@GetPostMethodActivity, "$result", Toast.LENGTH_SHORT)
            super.onPostExecute(result)
        }
    }


    private fun getType(path: String): String {
        var extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}
