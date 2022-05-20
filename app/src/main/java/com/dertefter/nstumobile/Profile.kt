package com.dertefter.nstumobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class Profile : Fragment(R.layout.profile_fragment) {
    var settinsButton: ImageButton? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settinsButton = view.findViewById(R.id.settings_button)
        settinsButton?.setOnClickListener {
            var inta = Intent(Work.applicationContext(), Settings::class.java)
            startActivity(inta)
        }

        fragmentManager?.beginTransaction()?.replace(R.id.profile_mwnu, profileMenu())?.commit()


    }
}