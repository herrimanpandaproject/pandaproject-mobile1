/*
 * Copyright (C) 2019 - present Instructure, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.instructure.template.loginTemplate.login

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.instructure.template.R
import com.instructure.template.loginTemplate.api.ApiPrefs
import kotlinx.android.synthetic.main.activity_find_school.*

class FindSchoolActivity : AppCompatActivity() {

    private var mNextActionButton: TextView? = null

    private fun signInActivityIntent(url: String): Intent {
        return SignInActivity.createIntent(this, url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_school)
        setupViews()
    }

    private fun setupViews() {
        toolbar.apply {
            navigationIcon = ContextCompat.getDrawable(this@FindSchoolActivity, R.drawable.vd_arrow_back)
            navigationIcon?.isAutoMirrored = true
            setNavigationContentDescription(R.string.close)
            inflateMenu(R.menu.menu_next)
            setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
                if (item.itemId == R.id.next) {
                    validateDomain(domainInput.text.toString())
                    return@OnMenuItemClickListener true
                }
                false
            })
            setNavigationOnClickListener { finish() }
        }
        mNextActionButton = findViewById(R.id.next)
        mNextActionButton!!.isEnabled = true
        mNextActionButton!!.setTextColor(ContextCompat.getColor(
            this@FindSchoolActivity, R.color.loginFlowBlue))

        domainInput.setOnEditorActionListener { _, _, _ ->
            validateDomain(domainInput.text.toString())
            true
        }

        domainInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (mNextActionButton != null) {
                    if (TextUtils.isEmpty(s.toString())) {
                        mNextActionButton!!.isEnabled = false
                        mNextActionButton!!.setTextColor(ContextCompat.getColor(
                            this@FindSchoolActivity, R.color.grayCanvasLogo))
                    } else {
                        mNextActionButton!!.isEnabled = true
                        mNextActionButton!!.setTextColor(ContextCompat.getColor(
                            this@FindSchoolActivity, R.color.loginFlowBlue))
                    }
                }
            }
        })

        manualLogin.setOnClickListener {
            val editText = EditText(this@FindSchoolActivity)
            editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            AlertDialog.Builder(this@FindSchoolActivity)
                .setTitle("Set Token")
                .setPositiveButton("Ok") { _, _ ->
                    val token= editText.text.toString()
                    if(token.isEmpty()) {
                        ApiPrefs.token = ""
                    } else {
                        ApiPrefs.token = token
                        ApiPrefs.protocol = "https"
                        ApiPrefs.domain = "pandaproject.instructure.com"
                        this@FindSchoolActivity.startActivity(Intent(this, SignInActivity::class.java))
                    }
                }
                .setView(editText)
                .setNegativeButton("cancel", null)
                .create()
                .show()
        }
    }

    private fun validateDomain(urlInput: String?) {
        var url: String? = urlInput?.toLowerCase()?.replace(" ", "")

        //if the user enters nothing, try to connect to canvas.instructure.com
        if (url!!.trim { it <= ' ' }.isEmpty()) {
            url = "canvas.instructure.com"
        }

        //if there are no periods, append .instructure.com
        if (!url.contains(".") || url.endsWith(".beta")) {
            url += ".instructure.com"
        }

        //URIs need to to start with a scheme.
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        //Get just the host.
        val uri = Uri.parse(url)
        url = uri.host

        //Strip off www. if they typed it.
        if (url!!.startsWith("www.")) {
            url = url.substring(4)
        }

        val intent = signInActivityIntent(url)
        startActivity(intent)
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
