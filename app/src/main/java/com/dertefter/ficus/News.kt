package com.dertefter.ficus

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit


class News : Fragment(R.layout.fragment_news) {
    var newsId: Int = 1
    var scrollView: NestedScrollView? = null
    var allNews: LinearLayout? = null
    var topBar: ProgressBar? = null
    var appBarLayout: AppBarLayout? = null
    var fab: ExtendedFloatingActionButton? = null
    fun getNewsById(id: Int, isUp: Boolean){
        val mInflater = LayoutInflater.from(activity)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.nstu.ru/")
            .build()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getNews(newsId.toString())
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    topBar?.visibility = View.GONE
                    if (allNews?.size!! > 28 || isUp){
                        allNews?.removeAllViews()
                        scrollView?.scrollTo(20, 0)
                        appBarLayout?.scrollTo(-100, 0)

                        allNews?.removeAllViews()

                        val a = ObjectAnimator.ofFloat(allNews, "alpha", 0f, 1f)
                        a.duration = 750
                        a.start()
                        ObjectAnimator.ofFloat(fab, "translationY", 1000f).start()
                    }
                    val jsonObject = JSONObject(response.body()!!.string())
                    val pretty = jsonObject.getString("items")
                    val doc: Document = Jsoup.parse(pretty.replace("\n", "").replace("\t", ""))
                    val news_items = doc.body().select("a")
                    for (it in news_items){
                        val imageUrl = "https://www.nstu.ru/" + it.attr("style").toString().replace("background-image: url(", "").replace(");", "")
                        val colorString = it.attr("data-schema")
                        val item: View = mInflater.inflate(R.layout.item_news, null, false)
                        val title = it.select("div.main-events__item-title").text().toString()
                        val date = it.select("div.main-events__item-date").text().toString()
                        val tag = it.select("div.main-events__item-tags").text().toString()
                        when (colorString){
                            "yellow" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#EFAE2D"))
                            "blue" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#0596d6"))
                            "green" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#00cc73"))
                            "dark-red" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#781c33"))
                            "dark-green" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#004b37"))
                            "red" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#f25370"))
                            "grey" -> item.findViewById<FrameLayout>(R.id.news_color).setBackgroundColor(Color.parseColor("#000000"))
                            "false" -> item.findViewById<ImageView>(R.id.background_news).background = ContextCompat.getDrawable(Work.applicationContext(), R.drawable.news_basic_background)
                        }
                        item.findViewById<TextView>(R.id.title_news).text = title!!
                        item.findViewById<TextView>(R.id.tag_news).text = tag!!
                        item.findViewById<TextView>(R.id.date_news).text = date!!
                        if (imageUrl != "https://www.nstu.ru/"){
                            Picasso.with(Work.applicationContext()).load(imageUrl).memoryPolicy(
                                MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).into(item.findViewById<ImageView>(R.id.background_news))
                        }
                        val link = "https://www.nstu.ru/" + it.attr("href")
                        item.setOnClickListener{
                            val inta = Intent(
                                Auth.applicationContext(),
                                ReadNewsActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            inta.putExtra("link", link)
                            Work.applicationContext().startActivity(inta)
                        }

                        allNews?.addView(item)

                    }



                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allNews = view.findViewById(R.id.allnews_page)
        appBarLayout = view.findViewById(R.id.newsAppBarLayout)
        view.findViewById<Toolbar>(R.id.toolbar_news).addSystemWindowInsetToMargin(top = true)
        fab = view.findViewById(R.id.upword_news)
        fab?.setOnClickListener {
            newsId = 1
            topBar?.visibility = View.VISIBLE
            scrollView?.scrollTo(20, 0)
            appBarLayout?.scrollTo(-100, 0)
            ObjectAnimator.ofFloat(fab, "translationY", 1000f).start()
            getNewsById(newsId, true)
        }
        topBar = view.findViewById(R.id.top_news_bar)
        scrollView = view.findViewById(R.id.newsScrollView)
        getNewsById(newsId, false)
        scrollView?.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            run {
                if (scrollView?.canScrollVertically(1) == false) {
                    newsId++
                    getNewsById(newsId, false)
                }
                else if (scrollView?.canScrollVertically(-1) == false && newsId > 1 && allNews?.size != 0) {
                    newsId = 1
                    topBar?.visibility = View.VISIBLE
                    scrollView?.scrollTo(20, 0)
                    appBarLayout?.scrollTo(-100, 0)
                    ObjectAnimator.ofFloat(fab, "translationY", 1000f).start()
                    getNewsById(newsId, true)
                }
                if (i2 < i4 && newsId > 2) {
                    ObjectAnimator.ofFloat(fab, "translationY", 0f).start()
                } else {
                    ObjectAnimator.ofFloat(fab, "translationY", 500f).start()
                }
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