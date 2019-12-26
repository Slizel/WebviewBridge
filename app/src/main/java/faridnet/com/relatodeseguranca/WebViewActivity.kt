package faridnet.com.relatodeseguranca

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*

import java.net.URLEncoder


class WebViewActivity: AppCompatActivity() {

    private val URL = "http://gestao.faridnet.com.br/RelatoSeguranca/Relato#new"
    val urlLogin = "http://gestao.faridnet.com.br/Account/Login"
    private var isAlreadyCreated = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        startLoaderAnimate();

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(false)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                endLoaderAnimate()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?){
                super.onReceivedError(view, request, error)
                endLoaderAnimate()
                showErrorDialog("Error",
                                "Sem conexão com a Internet. Por favor verifique sua conexão",
                                this@WebViewActivity)
            }
        }

        var postData = "username=" + URLEncoder.encode("admin", "UTF-8") + "&password=" + URLEncoder.encode("farid@2004", "UTF-8");
        webView.postUrl(urlLogin,postData.toByteArray())
        webView.loadUrl(URL)
    }


    override fun onResume() {
        super.onResume()

        if(isAlreadyCreated && !isNetworkAvailable()){
            showErrorDialog("Error", "Sem conexão com a Internet. Por favor verifique sua conexão",
                            this@WebViewActivity)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectionManager =
            this@WebViewActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    // Navegar para trás
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showErrorDialog(title: String, message: String, context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton("Cancel", { _, _ ->
            this@WebViewActivity.finish()
        })
        dialog.setNeutralButton("Settings", {_, _ ->
            startActivity(Intent(Settings.ACTION_SETTINGS))
        })
        dialog.setPositiveButton("Retry", { _, _ ->
            this@WebViewActivity.recreate()
        })
        dialog.create().show()
    }

    private fun endLoaderAnimate() {
        loaderImage.clearAnimation()
        loaderImage.visibility = View.GONE
    }

    private fun startLoaderAnimate() {
        val objectAnimator = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val startHeight = 170
                val newHeight = (startHeight + (startHeight + 40) * interpolatedTime).toInt()
                loaderImage.layoutParams.height = newHeight
                loaderImage.requestLayout()
            }

            override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
                super.initialize(width, height, parentWidth, parentHeight)
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        objectAnimator.repeatCount = -1
        objectAnimator.repeatMode = ValueAnimator.REVERSE
        objectAnimator.duration = 1000
        loaderImage.startAnimation(objectAnimator)
    }

    //Cria o menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true

    }

    //Listener do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_preference) {
            preferenceActivityCall()
        }
        return true
    }

    //Navega para a activity de preferences
    private fun preferenceActivityCall() {
        val intent = Intent(this, PreferenceActivity::class.java)
        //val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)

    }
}


