package univpm.korm;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import univpm.korm.Connessione.InvioDatiService;
import univpm.korm.Connessione.Sessione;


public class Login extends AppCompatActivity {
    private String User;
    private String Pass;
    private Sessione sessione;
    private final static String TAG = Login.class.getSimpleName();
    private ProgressDialog progressDialogDB;
    private Button bLogin;
    private TextView registerLink;
    private EditText Usertext;
    private EditText Passtext;
    int contatore;
    Handler mHandler;

    private static String ip;
    private static String porta;
    private static String soglia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessione =new Sessione(this);
        bLogin=(Button) findViewById(R.id.button);
        final Button bLoginGuest=(Button) findViewById(R.id.bLoginGuest);
        registerLink = (TextView) findViewById(R.id.RegisterHere);
        contatore=0;
        Usertext = (EditText) findViewById(R.id.User);
        Passtext = (EditText) findViewById(R.id.Password);

        mHandler=new Handler();

        ip = getString(R.string.ip);
        porta = getString(R.string.porta);
        soglia = getString(R.string.soglia);

        // Caricamento nella ricerca del server
        progressDialogDB=new ProgressDialog(Login.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialogDB.setIndeterminate(true);
        progressDialogDB.setMessage("Connessione al Server...");
        progressDialogDB.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(contatore==0){
                    progressDialogDB.dismiss();
                    bLogin.setEnabled(false);
                    Usertext.setEnabled(false);
                    Passtext.setEnabled(false);
                    registerLink.setEnabled(false);
                    //Snackbar.make(findViewById(android.R.id.content), "Sei offline", Snackbar.LENGTH_LONG).setDuration(20000).show();
                    Toast.makeText(getApplicationContext(),"Non sei connesso al Server",Toast.LENGTH_LONG).show();
                    sessione.DatiServer("","");
                    contatore=contatore+1;

                }

            }},5000);

        //Salvo la temperatura di soglia
        sessione.DatiSoglia(Integer.parseInt(soglia));

        if(ip.equals("") || porta.equals("")){
            // displayToast("Errore di accesso al Server!");
            bLogin.setEnabled(false);
            Usertext.setEnabled(false);
            Passtext.setEnabled(false);
            registerLink.setEnabled(false);
            //Snackbar.make(findViewById(android.R.id.content), "Sei offline", Snackbar.LENGTH_LONG).setDuration(20000).show();
            Toast.makeText(getApplicationContext(),"Non sei connesso al Server",Toast.LENGTH_LONG).show();
            sessione.DatiServer("","");
            contatore=contatore+1;
        }else{
            sessione.DatiServer(ip,porta);
            //Leggo il file Dati.xls dove ci sono le coordinate dei punti dove sono posizionati i beacon sulle varie mappe
            LetturaMappa();
        }

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //associo un'azione all'oggetto bRegister
                Login();
            }
        });

        bLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginGuest();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent =new Intent(Login.this,Registrazione.class);
                startActivity(registerIntent);
            }
        });
    }

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



    private void LetturaMappa() {
        AssetManager am1 = getAssets();
        InputStream is1 = null;

        try {
            is1 = am1.open("Dati.xls");
        } catch (IOException e) {

            e.printStackTrace();
        }
        Workbook wb1 = null;
        try {
            wb1 = Workbook.getWorkbook(is1);
        } catch (IOException e) {


            e.printStackTrace();
        } catch (BiffException e) {


            e.printStackTrace();
        }
        Sheet s1 = wb1.getSheet(0);
        int row1 =s1.getRows();
        int col1= s1.getColumns();


        String xxx = "";
        for (int i = 1; i < row1; i++) {
            for (int c = 0; c < col1; c++) {
                Cell z = s1.getCell(c, i);
                xxx = xxx + z.getContents() + ",";

            }

        }
        System.out.println(xxx);


        String a1[] = xxx.split(",");


        Intent intent=new Intent(this, InvioDatiService.class);
        intent.setAction("univpm.korm.Login.Punti");
        intent.putExtra("arraypunti", a1);
        intent.putExtra("soglia", soglia);
        this.startService(intent);



    }

    private void Login(){


        User= Usertext.getText().toString();
        Pass=Passtext.getText().toString();
        Log.i(TAG, "username "+User);

        boolean controllo= true;

        if (User.isEmpty()) {
            Usertext.setError("Inserisci una mail valida");
            controllo=false;
        } else {
            Usertext.setError(null);

        }

        if (Pass.isEmpty()) {
            Passtext.setError("tra 4 e 10 caratteri");
            controllo= false;
        } else {
            Passtext.setError(null);

        }

        if(controllo==true){
            Intent intent=new Intent(this, InvioDatiService.class);
            intent.setAction("univpm.korm.Login.Utenti");
            intent.putExtra("user", User);
            intent.putExtra("pass", Pass);
            this.startService(intent);

        }

    }

    private void LoginGuest(){

        User="Guest";
        sessione.UtenteGuest(true,User); //avvio la sessione

        Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
        intent.putExtra("user", User);
        Login.this.startActivity(intent);
        finish();

    }

    public void displayToast(String message){
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            if (("univpm.korm.View.Offline").equals(action)) {
                if(contatore<1){
                    bLogin.setEnabled(false);
                    Usertext.setEnabled(false);
                    Passtext.setEnabled(false);
                    registerLink.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Non sei connesso al Server",Toast.LENGTH_LONG).show();
                    sessione.DatiServer("","");
                    contatore=contatore+1;
                }
            }
            if (("univpm.korm.Login.Utenti").equals(action)) {
                String risultato= intent.getStringExtra("risultato");
                if(risultato.equals("Utente autenticato!")){
                    sessione.UtenteLoggato(true,User);
                    Intent intent1 = new Intent(Login.this, Home.class); //reinderizzo a Home
                    Login.this.startActivity(intent1);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"User o password sbagliati", Toast.LENGTH_SHORT).show(); //in caso di esito negativo del login mostro u nmessaggio di errore
                }

            }
            if (("univpm.korm.Login.Punti").equals(action)) {
                progressDialogDB.dismiss();
                contatore=contatore+1;
                sessione.DatiServer(ip,porta);
                controlloPrimoAvvio();
            }
        }
    };


    public void controlloPrimoAvvio() {
        if(sessione.loggedin())
        {
            Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home
            Login.this.startActivity(intent);
            finish();
        }
    }



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("univpm.korm.View.Offline");
        intentFilter.addAction("univpm.korm.Login.Utenti");
        intentFilter.addAction("univpm.korm.Login.Punti");
        return intentFilter;
    }


}