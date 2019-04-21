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
 */    package com.instructure.template.loginTemplate.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.instructure.template.R
import com.instructure.template.loginTemplate.api.apiHelpers.APIHelper
import com.instructure.template.loginTemplate.utils.Const
import kotlinx.android.synthetic.main.activity_login_landing_page.*

class LoginLandingPageActivity : AppCompatActivity() {

    private fun beginFindSchoolFlow(): Intent {
        return Intent(this, FindSchoolActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_landing_page)

        findMySchool.setOnClickListener{
            if (APIHelper.hasNetworkConnection()) {
                val intent = beginFindSchoolFlow()
                startActivity(intent)
            }
        }

    }
}