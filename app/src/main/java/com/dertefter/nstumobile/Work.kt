package com.dertefter.nstumobile

import AppPreferences
import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.IOException
import java.util.*


class Work : AppCompatActivity(), RecognitionListener {
    init {
        instance = this
    }

    companion object {
        private var instance: Work? = null


        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    var soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)

    var state: Bundle? = null
    var closeYavaButton: ImageButton? = null
    var yavaButton: FloatingActionButton? = null
    var yavaBox: FrameLayout? = null
    var bnav: BottomNavigationView? = null
    var yavaListenButton: LottieAnimationView? = null
    /* Used to handle permission request */
    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1

    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var ResultSpeech: TextView? = null

    var listenedString: String = ""
    var resultString: String = ""

    var cancelSound: Int? = null
    var listenSound: Int? = null
    var acceptSound: Int? = null
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)
        AppPreferences.setup(applicationContext())
        yavaListenButton = findViewById(R.id.yava_listen_button)
        yavaListenButton?.setOnClickListener{
            inboxListen()
        }
        yavaButton?.visibility = View.INVISIBLE
        listenSound = soundPool.load(applicationContext(), R.raw.listen, 1)
        cancelSound = soundPool.load(applicationContext(), R.raw.cancel, 1)
        acceptSound = soundPool.load(applicationContext(), R.raw.accept, 1)
        LibVosk.setLogLevel(LogLevel.INFO)
        // Check if user has given permission to record audio, init the model after permission is granted

        // Check if user has given permission to record audio, init the model after permission is granted
        val permissionCheck =
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        } else {
            initModel()
        }


        val timetableFragment = timeTable()
        val scoreFragment = Score()
        closeYavaButton = findViewById(R.id.closeYava)
        bnav = findViewById(R.id.bottomNavigationView)
        yavaButton = findViewById(R.id.Yava)
        yavaBox = findViewById(R.id.yava_box)
        ResultSpeech = findViewById(R.id.yava_listened_inbox)

        yavaButton?.setOnClickListener{
            show()
        }
        closeYavaButton?.setOnClickListener {
            hide()

        }


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

    override fun onPartialResult(hypothesis: String?) {
        if (hypothesis != null) {
            var decoded = hypothesis.replace("\n", "").replace("{", "").replace("}", "").replace("\"", "").replace("partial :", "")
            if (decoded.length != 3){
                listenedString = decoded.replace("   ", "")
                ResultSpeech?.text = listenedString
            }
        }
    }

    override fun onResult(hypothesis: String?) {
        if (hypothesis != null) {
            var decoded =
                hypothesis.replace("\n", "").replace("{", "").replace("}", "").replace("\"", "")
                    .replace("text :", "")
            resultString = decoded.replace("   ", "")
            ResultSpeech?.text = resultString
            YavaAI(resultString)
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        if (hypothesis != null) {
            cancel()
        }
    }

    override fun onError(exception: Exception?) {
        cancel()
    }

    override fun onTimeout() {
        YavaAI("args")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                initModel()
            } else {
                finish()
            }
        }
    }

    private fun initModel() {
        StorageService.unpack(
            Companion.applicationContext(), "model-ru", "model",
            { model: Model? ->
                this.model = model
                ObjectAnimator.ofFloat(yavaButton, "translationX", 0f).apply {
                    duration = 200
                    start()
                }
                yavaButton?.visibility = View.VISIBLE
            }
        ) { exception: IOException ->
            Log.e(
                "Failed to unpack the model", exception.message.toString()
            )
        }
    }

    private fun recognizeMicrophone() {
        if (speechService != null) {
            speechService?.stop()
            speechService = null
        } else {
            try {
                val rec = Recognizer(model, 16000.0f)
                speechService = SpeechService(rec, 16000.0f)
                speechService?.startListening(this)
            } catch (e: IOException) {
            } catch (e: java.lang.Exception){

            }
        }
    }

    fun listen(){
        yavaListenButton?.frame = 13
        yavaListenButton?.speed = 2f
        ResultSpeech?.text = "Слушаю..."
        soundPool.play(listenSound!!, .5f, .5f, 0, 0, 1f)
        recognizeMicrophone()
    }
    fun inboxListen(){
        if (speechService != null){
            cancel()
            soundPool.play(cancelSound!!, .5f, .5f, 0, 0, 1f)
        } else {
            listen()
        }
    }
    fun cancel(){
        yavaListenButton?.frame = 13
        yavaListenButton?.speed = 0f
        ResultSpeech?.text = "Нажмите, чтобы говорить"
        speechService?.cancel()
        speechService = null
    }
    fun show(){
        bnav?.visibility = View.INVISIBLE
        yavaButton?.visibility = View.INVISIBLE
        yavaBox?.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(yavaBox, "translationY", 0f).apply {
            duration = 200
            start()
        }
        listen()
    }
    fun hide(){
        ObjectAnimator.ofFloat(yavaBox, "translationY", 350f).apply {
            duration = 30
            start()
        }
        bnav?.visibility = View.VISIBLE
        yavaButton?.visibility = View.VISIBLE
        yavaBox?.visibility = View.INVISIBLE
        cancel()
        soundPool.play(cancelSound!!, .5f, .5f, 0, 0, 1f)
    }
    fun accept(){
        hide()
        soundPool.play(acceptSound!!, .5f, .5f, 0, 0, 1f)
    }


    fun YavaAI(inputData: String){
        if (inputData.contains("зачёт") or inputData.contains("успеваемость")){
            bnav?.selectedItemId = R.id.score_nav
            accept()
        }
        if (inputData.contains("почт") or inputData.contains("сообщ")){
            bnav?.selectedItemId = R.id.messages_nav
            accept()
        }
        if (inputData.contains("профиль") or inputData.contains("акк")){
            bnav?.selectedItemId = R.id.profile_nav
            accept()
        }
        if (inputData.contains("редакт") or inputData.contains("личные")){
            if (bnav?.selectedItemId == R.id.profile_nav){
                val profiledataIntent = Intent(Auth.applicationContext(), ProfileData::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK)
                Auth.applicationContext().startActivity(profiledataIntent)
            }
            accept()
        }

        if (inputData.contains("вай фай") or inputData.contains("вай-фай")){
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://wifi.nstu.ru/"))
            startActivity(browserIntent)
            accept()
        }


        if (inputData.contains("писани") or inputData.contains("когда")){
            bnav?.selectedItemId = R.id.timetable_nav
            if(inputData.contains("сес") or (inputData.contains("экз"))){
                bnav?.selectedItemId = R.id.timetable_nav
                (supportFragmentManager.fragments.get(0) as timeTable).sessia()
            }
            if(inputData.contains("заняти") or (inputData.contains("пар"))){
                bnav?.selectedItemId = R.id.timetable_nav
                (supportFragmentManager.fragments.get(0) as timeTable).lessons()
            }
            accept()
        }
    }


}