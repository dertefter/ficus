package com.dertefter.ficus

import AppPreferences
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.WindowCompat
import androidx.core.view.forEach
import androidx.core.view.iterator
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_work.*


class Work : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: Work? = null


        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    var bnav: BottomNavigationView? = null

    val transition_duration: Long = 270
    var backPressedTime: Long = 0
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_work)

        AppPreferences.setup(applicationContext())

        val timetableFragment = timeTable()
        val scoreFragment = Score()
        val messagesFragment = Messages()
        val profileFragment = Profile()
        val newsFragment = News()
        bnav = findViewById(R.id.bottomNavigationView)
        bnav?.menu?.forEach {
            val view = bnav?.findViewById<View>(it.itemId)
            view?.setOnLongClickListener {
                // your logic here
                true
            }

        }



        var update = savedInstanceState?.getBoolean("update")
        if (update == null || update == true) {
            setCurrentFragment(timetableFragment)
            setCurrentFragment(scoreFragment)
            setCurrentFragment(newsFragment)
            setCurrentFragment(messagesFragment)
            setCurrentFragment(profileFragment)
            showFragment(timetableFragment)

        }

        bnav?.setOnNavigationItemSelectedListener {
            if (bnav?.selectedItemId != it.itemId){
                blink()
            }
            when (it.itemId) {

                R.id.timetable_nav -> {
                    showFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    hideFragment(newsFragment)
                    hideFragment(messagesFragment)
                    hideFragment(profileFragment)

                    true
                }
                R.id.messages_nav -> {
                    hideFragment(newsFragment)
                    hideFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    showFragment(messagesFragment)
                    hideFragment(profileFragment)

                    true
                }
                R.id.score_nav -> {
                    hideFragment(newsFragment)
                    hideFragment(timetableFragment)
                    showFragment(scoreFragment)
                    hideFragment(messagesFragment)
                    hideFragment(profileFragment)

                    true
                }
                R.id.profile_nav -> {
                    hideFragment(newsFragment)
                    hideFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    hideFragment(messagesFragment)
                    showFragment(profileFragment)

                    true
                }
                R.id.news_nav -> {
                    showFragment(newsFragment)
                    hideFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    hideFragment(messagesFragment)
                    hideFragment(profileFragment)

                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        show(fragment)
        commit()
    }

    private fun hideFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        hide(fragment)
        commit()
    }

    private fun blink(){
        val a = ObjectAnimator.ofFloat(flFragment, "alpha", 0f, 1f)
        a.duration = transition_duration
        a.start()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            add(R.id.flFragment, fragment)
            hide(fragment)
            commit()
        }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("update", false)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val bnav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val timetableFragment = timeTable()
        val scoreFragment = Score()
        val messagesFragment = Messages()
        val profileFragment = Profile()

        bnav.setOnNavigationItemSelectedListener {
            if (bnav.selectedItemId != it.itemId){
                blink()
            }
            when (it.itemId) {
                R.id.timetable_nav -> {
                    showFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                    hideFragment(supportFragmentManager.fragments.get(4))
                }
                R.id.score_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    showFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                    hideFragment(supportFragmentManager.fragments.get(4))
                }
                R.id.news_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    showFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                    hideFragment(supportFragmentManager.fragments.get(4))
                }
                R.id.messages_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    showFragment(supportFragmentManager.fragments.get(3))
                    hideFragment(supportFragmentManager.fragments.get(4))
                }

                R.id.profile_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                    showFragment(supportFragmentManager.fragments.get(4))
                }
            }
            true
        }


    }


}