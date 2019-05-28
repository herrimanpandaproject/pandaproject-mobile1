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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.instructure.template.R
import com.instructure.template.loginTemplate.api.ApiPrefs
import com.instructure.template.loginTemplate.api.apiHelpers.*
import com.instructure.template.loginTemplate.api.apiHelpers.APIHelper.generateUserAgent
import com.instructure.template.loginTemplate.api.models.OAuthToken
import com.instructure.template.loginTemplate.api.models.SignedInUser
import com.instructure.template.loginTemplate.api.models.User
import com.instructure.template.loginTemplate.utils.Const
import com.instructure.template.projectCodeHere.api.UpdateGrades
import com.instructure.template.projectCodeHere.layouts.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class SignInActivity : AppCompatActivity() {

    val SUCCESS_URL = "/login/oauth2/auth?code="
    val ERROR_URL = "/login/oauth2/auth?error=access_denied"
    private var mClientId: String? = null
    private var mClientSecret: String? = null
    private var mAuthenticationURL: String? = null
    private var accountUrl: String? = null

    fun getAccountUrl(): String {
        if (accountUrl == null) {
            accountUrl = intent.getStringExtra(Const.ACCOUNT_DOMAIN)
        }
        return accountUrl!!
    }

    private fun launchApplicationMainActivityIntent(): Intent {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush()
        }

        val intent = Intent(this, MainActivity::class.java)
        if (getIntent() != null && getIntent().extras != null) {
            intent.putExtras(getIntent().extras)
        }
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        if(ApiPrefs.token.isNotEmpty()) {
            // We now need to get the cache user
            UserManager.getSelf(object : StatusCallback<User>() {

                override fun onResponse(response: Response<User>, linkHeaders: LinkHeaders, type: ApiType) {
                    if (type.isAPI) {
                        ApiPrefs.user = response.body()
                        val userResponse = response.body()
                        val domain = ApiPrefs.domain
                        val protocol = ApiPrefs.protocol

                        val user = SignedInUser(userResponse!!, domain, protocol, ApiPrefs.token, null, null)

                        handleLaunchApplicationMainActivityIntent()
                    }
                }
            })
        } else {
            setupViews()
            signIn()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupViews() {
        toolbar.apply {
            title = getAccountUrl()
            setNavigationIcon(R.drawable.vd_arrow_back)
            if (navigationIcon != null) {
                navigationIcon!!.isAutoMirrored = true
            }
            setNavigationContentDescription(R.string.close)
            setNavigationOnClickListener { finish() }
        }

        clearCookies()
        CookieManager.getInstance().setAcceptCookie(true)
        webView.apply {
            settings.loadWithOverviewMode = true
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.setAppCacheEnabled(false)
            settings.domStorageEnabled = true
            settings.userAgentString = generateUserAgent(this@SignInActivity, ApiPrefs.userAgent)
            webViewClient = mWebViewClient
        }

        if (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    private fun overrideUrlLoading(view: WebView, url: String): Boolean {
        return false
    }

    private fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
    }

    fun getHeaders(): Map<String, String> {
        val extraHeaders = HashMap<String, String>()
        extraHeaders["user-agent"] = generateUserAgent(this, ApiPrefs.userAgent)
        extraHeaders["session_locale"] = Locale.getDefault().language
        return extraHeaders
    }

    private val mWebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                handleShouldOverrideUrlLoading(view, request.url.toString())
            } else super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return handleShouldOverrideUrlLoading(view, url)
        }

        private fun handleShouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (overrideUrlLoading(view, url)) return true

            if (url.contains(SUCCESS_URL)) {
                ApiPrefs.domain = getAccountUrl()
                val oAuthRequest = url.substring(url.indexOf(SUCCESS_URL) + SUCCESS_URL.length)
                OAuthManager.getToken(mClientId!!, mClientSecret!!, oAuthRequest, mGetTokenCallback)
            } else if (url.contains(ERROR_URL)) {
                clearCookies()
                view.loadUrl(mAuthenticationURL, getHeaders())
            } else {
                view.loadUrl(url, getHeaders())
            }

            return true // then it is not handled by default action
        }
    }

    private fun signIn() {
        mClientId = "146390000000000001"
        mClientSecret = "ICL1guLu6jedR23lQ1sIT7rWOKzlKm4kOgSHylRypxSR3y3h8yCqPLBDURkKLpmW"
        ApiPrefs.protocol = "https"

        buildAuthenticationUrl("https", getAccountUrl(), mClientId!!)
        loadAuthenticationUrl("https", getAccountUrl())
        UpdateGrades()
    }

    protected fun loadAuthenticationUrl(apiProtocol: String, domain: String) {
        webView.loadUrl(mAuthenticationURL, getHeaders())
    }

    private fun buildAuthenticationUrl(
        protocol: String,
        url: String,
        clientId: String
    ) {
        //Get device name for the login request.
        var deviceName: String? = Build.MODEL
        if (deviceName == null || deviceName == "") {
            deviceName = getString(R.string.unknownDevice)
        }
        //Remove spaces
        deviceName = deviceName!!.replace(" ", "_")
        //changed for the online update to have an actual formatted login page

        var domain = url

        if (domain != null && domain!!.endsWith("/")) {
            domain = domain!!.substring(0, domain!!.length - 1)
        }

        val builder = Uri.Builder()
            .scheme(protocol)
            .authority(domain)
            .appendPath("login")
            .appendPath("oauth2")
            .appendPath("auth")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("mobile", "1")
            .appendQueryParameter("purpose", deviceName)

        builder.appendQueryParameter("redirect_uri", "https://canvas.instructure.com/login/oauth2/auth")

        val authUri = builder.build()

        mAuthenticationURL = authUri.toString()
    }

    private val mGetTokenCallback = object : StatusCallback<OAuthToken>() {

        override fun onResponse(response: Response<OAuthToken>, linkHeaders: LinkHeaders, type: ApiType) {
            if (type.isCache) return

            val token = response.body()
            ApiPrefs.token = token!!.accessToken!!
            val accessToken = token!!.accessToken!!

            // We now need to get the cache user
            UserManager.getSelf(object : StatusCallback<User>() {

                override fun onResponse(response: Response<User>, linkHeaders: LinkHeaders, type: ApiType) {
                    if (type.isAPI) {
                        ApiPrefs.user = response.body()
                        val userResponse = response.body()
                        val domain = ApiPrefs.domain
                        val protocol = ApiPrefs.protocol

                        val user = SignedInUser(userResponse!!, domain, protocol, accessToken, null, null)

                        handleLaunchApplicationMainActivityIntent()
                    }
                }
            })
        }

        override fun onFail(call: Call<OAuthToken>?, error: Throwable, response: Response<*>?) {
            Toast.makeText(this@SignInActivity, R.string.errorOccurred, Toast.LENGTH_SHORT).show()

            webView.loadUrl(mAuthenticationURL, getHeaders())
        }
    }

    /**
     * Override and do not call super if you need additional logic before launching the main activity intent.
     * It is expected that the class overriding will launch an intent.
     */
    protected fun handleLaunchApplicationMainActivityIntent() {
        val intent = launchApplicationMainActivityIntent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        fun createIntent(context: Context, url: String): Intent {
            val intent = Intent(context, SignInActivity::class.java)
            val extras = Bundle()
            extras.putString(Const.ACCOUNT_DOMAIN, url)
            intent.putExtras(extras)
            return intent
        }
    }
}
