package com.dertefter.ficus

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class Profile : Fragment(R.layout.profile_fragment) {
    var toolbar: Toolbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar_pr)
        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings_button -> {
                    var inta = Intent(Work.applicationContext(), Settings::class.java)
                    startActivity(inta)
                    true
                }
                else -> false
            }
        }
        fragmentManager?.beginTransaction()?.replace(R.id.profile_mwnu, profileMenu())?.commit()


    }
}