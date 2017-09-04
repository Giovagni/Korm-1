package univpm.korm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import univpm.korm.Connessione.InvioDatiService;
import univpm.korm.Connessione.Sessione;
import univpm.korm.R;


public class Modifica extends AppCompatActivity {


    private String User ;
    private String Sesso;
    private String Problemi;

    private int contatore=0;

    private Sessione sessione;
    protected TextView mDateDisplay;
    protected ImageButton mPickDate;
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected String sesso;
    protected String problemi;

    EditText name;
    EditText cognome;
    EditText username;
    EditText password;
    EditText confPassword;
    Button bconfmodifica;


    protected DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }

            };



    protected Dialog onCreateDialog(int id) {
        DatePickerDialog dialog = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        return dialog;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_dati);
        sessione=new Sessione(this);
        User=sessione.user();


        name=(EditText) findViewById(R.id.UNome);
        cognome=(EditText) findViewById(R.id.UCognome);
        username=(EditText) findViewById(R.id.UUser);
        password=(EditText) findViewById(R.id.UPassword);
        confPassword=(EditText) findViewById(R.id.ConfermaUPassword);
        bconfmodifica=(Button) findViewById(R.id.buttonConfModifica);

        username.setText(User);


        Intent intent=new Intent(this, InvioDatiService.class);
        intent.setAction("univpm.korm.View.ModificaDati");
        intent.putExtra("user", User);
        startService(intent);


        bconfmodifica.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 modificadatiserver();

                                             }
                                         }
        );

    }

    public void modificadatiserver(){
        String data= String.valueOf(new StringBuilder()
                .append("  ")
                .append(mDay).append("-")
                .append(mMonth+1).append("-")
                .append(mYear).append(" "));

        User=username.getText().toString();

        boolean controllo= true;

        if (User.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(User).matches()) {
            username.setError("Inserisci una mail valida");
            controllo=false;
        } else {
            username.setError(null);

        }

        if (password.getText().toString().isEmpty() || password.getText().toString().length() < 4 || password.getText().toString().length() > 10) {
            password.setError("tra 4 e 10 caratteri");
            controllo= false;
        } else if (!password.getText().toString().equals(confPassword.getText().toString())){
            password.setError("Le password non corrispondono!");
            confPassword.setError("Le password non corrispondono!");
            controllo= false;
        }else {
            confPassword.setError(null);

        }

        if(controllo==true){


            Intent intent=new Intent(this, InvioDatiService.class);
            intent.setAction("univpm.korm.View.ModificaDati.Utente");
            intent.putExtra("nome", name.getText().toString());
            intent.putExtra("cognome", cognome.getText().toString());
            intent.putExtra("pass", password.getText().toString());
            intent.putExtra("user", User);
            intent.putExtra("sesso", Sesso);
            intent.putExtra("problemi", Problemi);
            intent.putExtra("datan", data);
            intent.putExtra("olduser", sessione.user());
            startService(intent);

        }

        sessione.UtenteLoggato(true,String.valueOf(username.getText()));
    }


    //mostra a video dei messaggi
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void spinner(){
        String compareValuesesso=sesso;
        Spinner spinnersesso = (Spinner)findViewById(R.id.USesso);
        final ArrayAdapter<String> adaptersesso = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Maschio","Femmina"}
        );
        spinnersesso.setAdapter(adaptersesso);
        if (!compareValuesesso.equals(null)) {
            int spinnerPosition = adaptersesso.getPosition(compareValuesesso);
            spinnersesso.setSelection(spinnerPosition);
        }
        spinnersesso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptersesso, View view,int pos, long id) {

                Sesso = (String)adaptersesso.getItemAtPosition(pos);



            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        String compareValueprob=problemi;
        Spinner spinnerproblemi = (Spinner)findViewById(R.id.UProblemi);
        final ArrayAdapter<String> adapterproblemi = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Nessuno","Problemi motori"}
        );
        spinnerproblemi.setAdapter(adapterproblemi);
        if (!compareValueprob.equals(null)) {
            int spinnerPosition = adapterproblemi.getPosition(compareValueprob);
            spinnerproblemi.setSelection(spinnerPosition);
        }
        spinnerproblemi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterproblemi, View view,int pos, long id) {

                Problemi = (String) adapterproblemi.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
    public void datepicker(){


        mDateDisplay = (TextView) findViewById(R.id.UData);
        mPickDate = (ImageButton) findViewById(R.id.buttonUData);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });
        if(contatore!=0) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            updateDisplay();
        }
        else
        {updateDisplay();}
        contatore++;
    }


    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append("  ")
                        .append(mDay).append("/")
                        .append(mMonth+1).append("/")
                        .append(mYear).append(" "));
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action=intent.getAction();
            if(("univpm.korm.View.ModificaDati").equals(action)){

                String risultato=intent.getStringExtra("risultato");
                String ris[] = risultato.split(",");
                name.setText(ris[1]);
                cognome.setText(ris[2]);
                password.setText(ris[0]);
                confPassword.setText(ris[0]);
                problemi=(ris[4]);
                sesso=(ris[5]);
                String[] datasezionata=ris[3].split("-");
                mDay= Integer.parseInt(datasezionata[0].trim());
                mMonth= Integer.parseInt(datasezionata[1])-1;
                mYear= Integer.parseInt(datasezionata[2].trim());
                datepicker();
                spinner();
            }
            if(("univpm.korm.View.ModificaDati.Utente").equals(action)){

                finish();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("univpm.korm.View.ModificaDati");
        intentFilter.addAction("univpm.korm.View.ModificaDati.Utente");
        return intentFilter;
    }


}