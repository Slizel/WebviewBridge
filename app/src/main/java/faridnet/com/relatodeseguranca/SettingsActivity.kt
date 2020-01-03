package faridnet.com.relatodeseguranca

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.login_dialog.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val mDialogView = LayoutInflater.from(this).inflate(R.layout.login_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setCancelable(false)
            .setView(mDialogView)
            .setTitle("Digite a Senha")
        //show dialog
        val  mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {
            //dismiss dialog

            val current = LocalDateTime.now()
            var formatter = DateTimeFormatter.ofPattern("MM")

            val mes = current.format(formatter).toInt()
            formatter = DateTimeFormatter.ofPattern("dd")
            val dia = current.format(formatter).toInt()

            val senha =  (dia + 20).toString() + (mes + 11).toString()

            //get text from EditTexts of custom layout
            val password = mDialogView.dialogPasswEt.text.toString()
            //set the input text in TextView
            //mainInfoTv.setText("Password: "+ password)

            if (password == senha){
                mAlertDialog.dismiss()
            }else{
                Toast.makeText(this,"Senha Inválida!", Toast.LENGTH_SHORT).show()
            }
        }

        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container2, SettingsFragment())
            .commit()
    }
}