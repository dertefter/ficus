package com.dertefter.nstumobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

class ReadMessageActivity : AppCompatActivity() {
    var toolbar: MaterialToolbar? = null
    var text: TextView? = null
    var fab: FloatingActionButton? = null

    private fun deleteThis(MessageID: String){
        var tokenId = AppPreferences.token
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=$tokenId")
                    .build()
                chain.proceed(authorized)
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ciu.nstu.ru/student_study/mess_teacher/ajax_del_mes/")
            .client(client)
            .build()
        val service = retrofit.create(APIService::class.java)
        val params = HashMap<String?, String?>()
        params["idmes"] = MessageID
        params["what"] = "1"
        params["type"] = "1"
        params["vid_sort"] = "1"
        params["year"] = "-1"

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = service.postForm(params)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        finish()
                    }
                }
            }catch (e: Throwable){
                Snackbar.make(findViewById(R.id.read_message_layout), "Ошибка! Попробуйте позже...", Snackbar.LENGTH_SHORT).setTextColor(getColor(R.color.md_theme_dark_inverseSurface))
                    .show()
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_message_layout)
        var messageID: String = intent.getStringExtra("mesid")!!
        toolbar = findViewById(R.id.toolbar_read_message)
        text = findViewById(R.id.read_message_text)
        fab = findViewById(R.id.delete_this)
        val get_theme = intent.getStringExtra("theme")
        val get_text=intent.getStringExtra("text")
        val get_send_by=intent.getStringExtra("send_by")
        toolbar?.title = get_send_by
        toolbar?.subtitle = get_theme
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        text?.text = get_text
        fab?.setOnClickListener{
            MaterialAlertDialogBuilder(this)
                .setTitle("Вы уверены?")
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Удалить") { dialog, which ->
                    deleteThis(messageID)
                }
                .show()
        }


    }
}