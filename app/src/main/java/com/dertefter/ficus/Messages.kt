package com.dertefter.ficus

import AppPreferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.view.ActionMode
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Text
import retrofit2.Retrofit




class Messages : Fragment(R.layout.messages_fragment) {
    var messagesView: LinearLayout? = null
    var mInflater: LayoutInflater? = null
    var toolbar: Toolbar? = null
    var toolbarLayout: AppBarLayout? = null
    var selectableToolbar: Toolbar? = null
    var selectableToolbarLayout: AppBarLayout? = null
    var spinner: ProgressBar? = null
    var animation: FrameLayout? = null
    var current_value = 0
    var no_mesTextView: TextView? = null
    var isSelectMode: Boolean = false
    var mes_ids = mutableListOf<String>()
    var how_many_mes: Int = 0
    override fun onStart() {
        super.onStart()
        mes(current_value)

    }
    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun mes(value: Int) {
        current_value = value
        messagesView?.removeAllViews()
        animation?.visibility = View.INVISIBLE
        spinner?.visibility = View.VISIBLE
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=" + AppPreferences.token)
                    .build()
                chain.proceed(authorized)
            })
            .build()

        val url1 = "https://ciu.nstu.ru/student_study/"
        var retrofit = Retrofit.Builder()
            .baseUrl(url1)
            .client(client)
            .build()
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.messages("-1")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.sysContentWithMenu")
                    val tbody = s.select("tbody")[value]
                    val messages = tbody?.select("tr")
                    spinner?.visibility = View.INVISIBLE
                    if (messages != null) {
                        if (messages.size != 0) {
                            animation?.visibility = View.INVISIBLE
                        } else {
                            animation?.visibility = View.VISIBLE
                        }
                        for (i in messages) {
                            var message_item: View =
                                mInflater!!.inflate(R.layout.message, null, false)
                            message_item.findViewById<TextView>(R.id.send_by).text =
                                i.select("span")[0].text().replace("(преподаватель)", "").replace("(деканат)", "").replace("(УИП)", "")
                            message_item.findViewById<TextView>(R.id.message_text).text =
                                i.select("div").text().toString().replace(" -- ", ": ")
                            message_item.findViewById<ImageView>(R.id.send_by_image)
                                .setImageResource(R.drawable.ic_round_account_circle_24)
                            message_item.findViewById<TextView>(R.id.mes_id).text =  i.select("td")[0].select("input")[0].attr("id").toString()
                                .replace("id_chk_", "")
                            message_item.isClickable = true
                            message_item.setOnClickListener {
                                if (!isSelectMode){
                                    val inta = Intent(
                                        Auth.applicationContext(),
                                        ReadMessageActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    inta.putExtra(
                                        "send_by",
                                        i.select("span")[0].text().toString()
                                            .replace("(преподаватель)", "").replace("(деканат)", "").replace("(УИП)", "")
                                    )
                                    inta.putExtra(
                                        "theme",
                                        i.select("span")[1].text().toString().replace("--", "\n")
                                    )
                                    var text = i.select("span")[2].text().toString().replace("--", "\n")
                                    text = text.replaceRange(0, 2, "")
                                    inta.putExtra(
                                        "text",
                                        text
                                    )
                                    inta.putExtra(
                                        "mesid",
                                        i.select("td")[0].select("input")[0].attr("id").toString()
                                            .replace("id_chk_", "")
                                    )
                                    Work.applicationContext().startActivity(inta)
                                }
                                else{
                                    if (mes_ids.contains(message_item.findViewById<TextView>(R.id.mes_id).text.toString())){
                                        message_item.findViewById<MaterialCardView>(R.id.CardItem)?.setCardBackgroundColor(context?.getColorFromAttr(
                                            com.google.android.material.R.attr.colorSurface)!!)
                                        mes_ids.remove(message_item.findViewById<TextView>(R.id.mes_id).text.toString())
                                        selectableToolbar?.title = mes_ids.size.toString() + " выбрано"
                                        if (mes_ids.size == 0){
                                            selectModeDisable()
                                            mes_ids = mutableListOf<String>()
                                        }
                                    }else{
                                        message_item.findViewById<MaterialCardView>(R.id.CardItem)?.setCardBackgroundColor(context?.getColorFromAttr(
                                            com.google.android.material.R.attr.colorSurfaceVariant)!!)
                                        how_many_mes++

                                        mes_ids.add(message_item.findViewById<TextView>(R.id.mes_id).text.toString())
                                        selectableToolbar?.title = mes_ids.size.toString() + " выбрано"

                                    }

                                }
                            }
                            message_item.setOnLongClickListener {
                                if (!isSelectMode){
                                    isSelectMode = true
                                    selectModeEnable()
                                    how_many_mes++
                                    mes_ids.add(message_item.findViewById<TextView>(R.id.mes_id).text.toString())
                                    selectableToolbar?.title = (mes_ids.size).toString() + " выбрано"
                                    message_item.findViewById<MaterialCardView>(R.id.CardItem)?.setCardBackgroundColor(context?.getColorFromAttr(
                                        com.google.android.material.R.attr.colorSurfaceVariant)!!)
                                }
                                true
                            }

                            messagesView?.addView(message_item)

                        }
                    }


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    fun selectModeEnable(){
        toolbarLayout?.visibility = View.INVISIBLE
        selectableToolbarLayout?.visibility = View.VISIBLE
        isSelectMode = true
    }
    fun selectModeDisable(){
        toolbarLayout?.visibility = View.VISIBLE
        selectableToolbarLayout?.visibility = View.INVISIBLE
        isSelectMode = false
    }

    private fun deleteThis(MessageID: String) {
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
            try {
                val response = service.postForm(params)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                    }
                }
            } catch (e: Throwable) {

            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarLayout = view.findViewById(R.id.AppBarLayout)
        messagesView = view.findViewById(R.id.messages_view)
        animation = view.findViewById(R.id.animationMessages)
        mInflater = LayoutInflater.from(activity)
        spinner = view.findViewById(R.id.spinner_mes)
        toolbar = view.findViewById(R.id.toolbar_messages)
        toolbar?.addSystemWindowInsetToMargin(top = true)
        no_mesTextView = view.findViewById(R.id.no_mes_text)
        selectableToolbar = view.findViewById(R.id.Stoolbar_messages)
        selectableToolbar?.addSystemWindowInsetToMargin(top = true)
        selectableToolbarLayout = view.findViewById(R.id.SAppBarLayout)
        selectableToolbar?.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.delete_many -> {

                    for (i in 0..mes_ids.size - 1){
                        deleteThis(mes_ids[i])

                    }
                    selectModeDisable()
                    mes(current_value)
                    true
                }
                else -> false
            }

        }
        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mes1 -> {
                    toolbar?.title = "От преподавателей"
                    mes(0)
                    true
                }
                R.id.mes2 -> {
                    toolbar?.title = "От деканата"
                    mes(1)
                    true
                }
                R.id.mes3 -> {
                    toolbar?.title = "От бухгалтерии"
                    mes(2)
                    true
                }
                R.id.mes4 -> {
                    toolbar?.title = "От международной службы"
                    mes(3)
                    true
                }
                R.id.mes5 -> {
                    toolbar?.title = "Прочее"
                    mes(4)
                    true
                }
                else -> false
            }
        }
    }

    fun View.addSystemWindowInsetToPadding(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
    ) {
        val (initialLeft, initialTop, initialRight, initialBottom) =
            listOf(paddingLeft, paddingTop, paddingRight, paddingBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            view.updatePadding(
                left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
            )

            insets
        }
    }

    fun View.addSystemWindowInsetToMargin(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
    ) {
        val (initialLeft, initialTop, initialRight, initialBottom) =
            listOf(marginLeft, marginTop, marginRight, marginBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            view.updateLayoutParams {
                (this as? ViewGroup.MarginLayoutParams)?.let {
                    updateMargins(
                        left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                        top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                        right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                        bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
                    )
                }
            }

            insets
        }
    }
}