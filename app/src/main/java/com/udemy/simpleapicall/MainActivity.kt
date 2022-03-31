package com.udemy.simpleapicall

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CallApiLoginAsyncTask("Adan", "123453213").startApiCall()
    }

    private inner class CallApiLoginAsyncTask(val userName: String, val psw: String) {

        private lateinit var customProgressDialog: Dialog

        fun startApiCall() {
            showProgressDialog()
            lifecycleScope.launch(Dispatchers.IO) {

                val stringResult = makeApiCall()
                runOnUiThread() {
                    cancelProgressDialog(stringResult)
                }
            }
        }

        private fun makeApiCall(): String {
            var result: String
            var connection: HttpURLConnection? = null

            try {
                val url = URL("https://run.mocky.io/v3/d9db49ea-54ef-4be1-926b-b2a7b3f8da79")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")

                connection.useCaches = false
                val writeDataOutputStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("username", userName)
                jsonRequest.put("password", psw)

                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()

                val httpResult: Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                } else {
                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error: ${e.printStackTrace()}"
            } finally {
                connection?.disconnect()
            }
            return result
        }

        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.setCancelable(false)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(result: String) {
            customProgressDialog.dismiss()

            //with GSON library
            val responseData = Gson().fromJson(result, ResponseData::class.java)
            Log.i("NAME", responseData.name)
            Log.i("DUMMY NO", "${responseData.dummy_no}")
            Log.i("PROFILE", "${responseData.profile_details.is_profile_completed}")
            Log.i("RATING", "${responseData.profile_details.rating}")

            responseData.data_list.forEach { userDetail ->
                Log.i("USER ID", "${userDetail.id}")
                Log.i("USER NAME", userDetail.name)
            }

            /*
            Log.i("JSON RESPONSE RESULT", result)
            val jsonObj = JSONObject(result)
            val name = jsonObj.optString("Name")
            Log.i("NAME", name)
            val profile = jsonObj.optString("Profile_details")
            Log.i("PROFILE", profile)

            val dataList = jsonObj.optJSONArray("data_list")

            for (item in 0 until dataList!!.length()) {
                Log.i("value $item", "$dataList[item]")
                val dataItemObj: JSONObject = dataList[item] as JSONObject
                val userId = dataItemObj.optInt("id")
                val userName = dataItemObj.optString("name")
                Log.i("USER ID", "$userId")
                Log.i("USER NAME", userName)
            }

             */
        }
    }
}