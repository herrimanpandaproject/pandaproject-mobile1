package com.instructure.template.projectCodeHere

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.instructure.template.R
import com.instructure.template.loginTemplate.api.ApiPrefs
import com.instructure.template.loginTemplate.api.apiHelpers.CanvasRestAdapter
import com.instructure.template.loginTemplate.api.apiHelpers.RestBuilder
import com.instructure.template.loginTemplate.login.LoginActivity
import com.instructure.template.loginTemplate.utils.FileUtils
import kotlinx.android.synthetic.main.fragment_example.*

class ExampleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_example, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton.setOnClickListener {
            // Clear the local data (token, domain, etc)
            CanvasRestAdapter.okHttpClient.cache()?.evictAll()
            ApiPrefs.clearAllData()
            // Take us back to login starting point
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        navigationButton.setOnClickListener {
            findNavController().navigate(R.id.action_exampleFragment_to_exampleDestinationFragment)
        }
    }
}
