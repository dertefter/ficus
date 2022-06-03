package com.dertefter.nstumobile

import AppPreferences
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class Work : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: Work? = null


        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    var bnav: BottomNavigationView? = null


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
        bnav = findViewById(R.id.bottomNavigationView)
        val messagesFragment = Messages()
        val profileFragment = Profile()
        var update = savedInstanceState?.getBoolean("update")
        if (update == null || update == true)
        {
            setCurrentFragment(timetableFragment)
            setCurrentFragment(scoreFragment)
            setCurrentFragment(messagesFragment)
            setCurrentFragment(profileFragment)
            showFragment(timetableFragment)

        }



        bnav?.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.timetable_nav -> {
                    showFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    hideFragment(messagesFragment)
                    hideFragment(profileFragment)
                }
                R.id.messages_nav -> {
                    hideFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    showFragment(messagesFragment)
                    hideFragment(profileFragment)
                }
                R.id.score_nav -> {
                    hideFragment(timetableFragment)
                    showFragment(scoreFragment)
                    hideFragment(messagesFragment)
                    hideFragment(profileFragment)
                }
                R.id.profile_nav -> {
                    hideFragment(timetableFragment)
                    hideFragment(scoreFragment)
                    hideFragment(messagesFragment)
                    showFragment(profileFragment)
                }
            }
            true
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

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
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
            when(it.itemId){
                R.id.timetable_nav -> {
                    showFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                }
                R.id.messages_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    showFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                }
                R.id.score_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    showFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    hideFragment(supportFragmentManager.fragments.get(3))
                }
                R.id.profile_nav -> {
                    hideFragment(supportFragmentManager.fragments.get(0))
                    hideFragment(supportFragmentManager.fragments.get(1))
                    hideFragment(supportFragmentManager.fragments.get(2))
                    showFragment(supportFragmentManager.fragments.get(3))
                }
            }
            true
        }




    }



}