package faridnet.com.relatodeseguranca

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_dialog.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.login_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setCancelable(false)
            .setView(mDialogView)
            .setTitle("Digite a Senha")
        //show dialog
        val mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {
            //dismiss dialog

            val calander: Calendar = Calendar.getInstance()
            var dia = calander.get(Calendar.DAY_OF_MONTH)
            var mes = calander.get(Calendar.MONTH) + 1

              // For API 26 or higher
//            val current = LocalDateTime.now()
//            var formatter = DateTimeFormatter.ofPattern("MM")
//
//            val mes = current.format(formatter).toInt()
//            formatter = DateTimeFormatter.ofPattern("dd")
//            val dia = current.format(formatter).toInt()

            val senha = (dia + 20).toString() + (mes + 11).toString()

            //get text from EditTexts of custom layout
            val password = mDialogView.dialogPasswEt.text.toString()
            //set the input text in TextView
            //mainInfoTv.setText("Password: "+ password)

            if (password == senha) {
                mAlertDialog.dismiss()
            } else {
                Toast.makeText(this, "Senha Inválida!", Toast.LENGTH_SHORT).show()
            }
        }

        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

    }
}