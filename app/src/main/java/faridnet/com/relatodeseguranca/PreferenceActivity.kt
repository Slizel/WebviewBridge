package faridnet.com.relatodeseguranca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_preference.*

private lateinit var preferencesProvider: PreferencesProvider

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        preferencesProvider = PreferencesProvider(applicationContext)

        buttonSave.setOnClickListener{
            preferencesProvider.putString(Constants.KEY_CPF, inputCPF.text.toString())
            preferencesProvider.putString(Constants.KEY_CELULAR,inputTelefone.text.toString())
            //preferencesProvider.putInt(Constants.KEY_INT, inputInt.text.toString().toInt())
            preferencesProvider.putBoolean(
                Constants.KEY_BOOL,
                radioGroupMaterial.checkedRadioButtonId == R.id.radioButtonYes
            )
            Toast.makeText(applicationContext,"Data Saved!", Toast.LENGTH_SHORT).show()
        }

        buttonLoad.setOnClickListener{
            inputCPF.setText(preferencesProvider.getString(Constants.KEY_CPF))
            inputTelefone.setText(preferencesProvider.getString(Constants.KEY_CELULAR))
            //inputInt.setText(preferencesProvider.getInt(Constants.KEY_INT).toString())
            if (preferencesProvider.getBoolean(Constants.KEY_BOOL))
            {
                radioButtonYes.isChecked = true
            }else{
                radioButtonNo.isChecked = true
            }
        }
        Toast.makeText(applicationContext,"Data Retrieved!", Toast.LENGTH_SHORT).show()

        buttonClear.setOnClickListener {
            preferencesProvider.clear()
        }
        Toast.makeText(applicationContext,"Data Retrieved!", Toast.LENGTH_SHORT).show()


    }


}
