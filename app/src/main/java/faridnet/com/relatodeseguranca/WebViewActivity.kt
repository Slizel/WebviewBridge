package faridnet.com.relatodeseguranca

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class WebViewActivity : AppCompatActivity() {

    private val urlAPI = "http://gestao.faridnet.com.br/RelatoSeguranca/Relato"
    val urlLogin = "http://gestao.faridnet.com.br/Account/Login"
    val urlLogout = "http://gestao.faridnet.com.br/Account/Signout"

    private var sharedCPF = ""
    private var sharedCelular = ""
    private var sharedUNB = ""
    private var sharedIMEI = ""

    var currentPhotoPath: String = ""
    var mCameraPhotoPath = ""

    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mCapturedImageURI: Uri? = null

    companion object {
        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
        private val TAG = WebViewActivity::class.java.simpleName

        private const val loadUrl = "https://yandex.ru/aura/"
        private const val userAgent =
            "Mozilla/5.0 (Linux; Android 9; Mi A2 Build/PKQ1.180904.001; wv) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/72.0.3626.121 " //+ "Mobile Safari/537.36 YandexSearch/8.05 YandexSearchBrowser/8.05"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        startLoaderAnimate()
        validaSharedPreferences()

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.userAgentString = userAgent

        webView.webViewClient = object : WebViewClient() {
        }
        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                webView: WebView,
                filePath: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (mFilePathCallback != null) {
                    mFilePathCallback!!.onReceiveValue(null)
                }

                mFilePathCallback = filePath
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex)
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    } else {
                        takePictureIntent = null
                    }
                }

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray: Array<Intent?>
                intentArray = (if (takePictureIntent != null) {
                    arrayOf(takePictureIntent)
                } else arrayOfNulls(0))

                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)

                return true
            }

            // openFileChooser for Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String = "") {
                mUploadMessage = uploadMsg
                // Create Aura folder at sdcard
                val imageStorageDir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ), "Aura"
                )
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs()
                }
                // Create camera captured image file path and name
                val file = File(
                    imageStorageDir.toString() + File.separator + "IMG_"
                            + System.currentTimeMillis().toString()
                            + ".jpg"
                )
                mCapturedImageURI = Uri.fromFile(file)
                // Camera capture image intent
                val captureIntent = Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE
                )
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"
                // Create file chooser intent
                val chooserIntent = Intent.createChooser(i, "Image Chooser")
                // Set camera intent to file chooser
                chooserIntent.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    arrayOf<Parcelable>(captureIntent)
                )
                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
            }

            //openFileChooser for other Android versions
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                acceptType: String,
                capture: String
            ) {
                openFileChooser(uploadMsg, acceptType)
            }
        }

        val username = "admin"
        val password = "farid@2004"
        var postData =
            "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(
                password,
                "UTF-8"
            )

        webView.loadUrl(urlLogout)
        webView.postUrl(urlLogin, postData.toByteArray())
        webView.loadUrl(urlAPI + "?&Mobile=1&Linha=$sharedCelular&CPF=$sharedCPF&UNB=$sharedUNB&IMEI=$sharedIMEI#new")

        endLoaderAnimate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    if (resultCode != Activity.RESULT_OK) {
                        result = null
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = if (data == null) mCapturedImageURI else data.data
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext, "activity :$e",
                        Toast.LENGTH_LONG
                    ).show()
                }

                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return

    }

    private fun loadSharedPreferences() {
        val myPrefs =
            getSharedPreferences("faridnet.com.relatodeseguranca_preferences", Context.MODE_PRIVATE)

        sharedCPF = myPrefs.getString("sharedCPF", "").toString()
        sharedCelular = myPrefs.getString("sharedCelular", "").toString()
        sharedUNB = myPrefs.getString("sharedUNB", "").toString()
        sharedIMEI = myPrefs.getString("sharedIMEI", "").toString()
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
        if (item.itemId == R.id.action_preference) {
            preferenceActivityCall()
        }

        return true
    }

    //Navega para a activity de preferences
    private fun preferenceActivityCall() {
        //val intent = Intent(this, PreferenceActivity::class.java)
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun validaSharedPreferences() {
        loadSharedPreferences()
        if (sharedCPF == "" || sharedCelular == "" || sharedUNB == "" || sharedIMEI == "") {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    // criar um nome de arquivo resistente a colisões
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}

