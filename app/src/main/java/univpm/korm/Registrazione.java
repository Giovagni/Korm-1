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
import java.util.Date;

import univpm.korm.Connessione.InvioDatiService;
import univpm.korm.Connessione.Sessione;

public class Registrazione extends AppCompatActivity {

    private  String Nome;
    private String Cognome;
    private String User ;
    private  String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;
    private String ConfPass;
    String ip;
    String porta;
    private Sessione sessione;
    protected TextView mDateDisplay;
    protected ImageButton mPickDate;
    protected int mYear;
    protected int mMonth;
    protected int mDay;


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
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sessione=new Sessione(this);
        ip=sessione.ip();
        porta=sessione.porta();
        final Button bRegister=(Button) findViewById(R.id.buttonRegistra);

        spinner();
        datepicker();
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistraServer();
            }
        });


    }

    public void spinner(){

        Spinner spinnersesso = (Spinner)findViewById(R.id.Sesso);

        final ArrayAdapter<String> adaptersesso = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Maschio","Femmina"}
        );

        spinnersesso.setAdapter(adaptersesso);
        spinnersesso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptersesso, View view,int pos, long id) {
                Sesso = (String)adaptersesso.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        Spinner spinnerproblemi = (Spinner)findViewById(R.id.Problemi);
        final ArrayAdapter<String> adapterproblemi = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Nessuno","Problemi motori"}
        );
        spinnerproblemi.setAdapter(adapterproblemi);

        spinnerproblemi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterproblemi, View view,int pos, long id) {

                Problemi = (String) adapterproblemi.getItemAtPosition(pos);


            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void datepicker(){



        mDateDisplay = (TextView) findViewById(R.id.Data);
        mPickDate = (ImageButton) findViewById(R.id.buttonData);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    //cambia la data mostrata a video
    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append("  ")
                        .append(mDay).append("/")
                        .append(mMonth+1).append("/")
                        .append(mYear).append(" "));
    }

    private void RegistraServer(){

        final EditText name=(EditText) findViewById(R.id.Name);
        final EditText cognome=(EditText) findViewById(R.id.Cognome);
        final EditText username=(EditText) findViewById(R.id.User);
        final EditText password=(EditText) findViewById(R.id.Password);
        final EditText confPassword=(EditText) findViewById(R.id.ConfermaPassword);

        Nome = name.getText().toString();
        Cognome = cognome.getText().toString();
        User = username.getText().toString();
        Pass = password.getText().toString();
        ConfPass = confPassword.getText().toString();
        DataN =  String.valueOf(new StringBuilder()
                .append("  ")
                .append(mDay).append("/")
                .append(mMonth+1).append("/")
                .append(mYear).append(" "));
        DataN=DataN.replace("/","-");

        boolean controllo= true;

        if (User.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(User).matches()) {
            username.setError("Inserisci una mail valida");
            controllo=false;
        } else {
            username.setError(null);

        }

        if (Pass.isEmpty() || Pass.length() < 4 || Pass.length() > 10) {
            password.setError("tra 4 e 10 caratteri");
            controllo= false;
        } else if (!password.getText().toString().equals(confPassword.getText().toString())){
            password.setError("Le password non corrispondono!");
            confPassword.setError("Le password non corrispondono!");
            controllo= false;
        }else {
            confPassword.setError(null);

        }


        if(controllo==true) {
            Intent intent = new Intent(this, InvioDatiService.class);
            intent.setAction("univpm.korm.Registrazione");
            intent.putExtra("nome", Nome);
            intent.putExtra("cognome", Cognome);
            intent.putExtra("pass", Pass);
            intent.putExtra("user", User);
            intent.putExtra("sesso", Sesso);
            intent.putExtra("problemi", Problemi);
            intent.putExtra("datan", DataN);
            intent.putExtra("confpass", ConfPass);
            startService(intent);
        }


    }

    //mostra a video dei messaggi
    public void displayToast(String message){
        Toast.makeText(Registrazione.this, message, Toast.LENGTH_SHORT).show();
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action=intent.getAction();
            if(("univpm.korm.Registrazione.Risposta").equals(action)){
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
        intentFilter.addAction("univpm.korm.Registrazione.Risposta");
        return intentFilter;
    }

}