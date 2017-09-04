package univpm.korm;


import univpm.korm.Connessione.BluetoothLeService;
import univpm.korm.Connessione.Mappa;
import univpm.korm.Connessione.Sessione;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static android.R.attr.uiOptions;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class Home extends AppCompatActivity {

    private Sessione sessione;
    private String user;
    private Toolbar toolbar;
    private Mappa imageView=null;
    private boolean started=false;
    private TextView scansione;
    private ProgressBar progressBar;
    private int contatore=0;


    private FloatingActionButton mFabItem1;
    private FloatingActionButton mFabItem2;


    private static final int MY_PERMISSIONS_REQUEST =1 ;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int RQS_ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Nascondo la toolbar
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        sessione = new Sessione(this);
        imageView = (Mappa) findViewById(R.id.mappa);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        scansione=(TextView) findViewById(R.id.scansione);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);



        mFabItem1 = (FloatingActionButton) findViewById(R.id.menu_item1);
        mFabItem2 = (FloatingActionButton) findViewById(R.id.menu_item2);

        mFabItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.contains("Guest"))
                {
                    Toast.makeText(getApplicationContext(),"Registrati per proseguire",Toast.LENGTH_LONG).show(); //in caso di utente guest non permetto la modifica
                }
                else if("".equals(sessione.ip()))
                {
                    Toast.makeText(getApplicationContext(),"Non sei connesso al Server",Toast.LENGTH_LONG).show(); //in caso di utente guest non permetto la modifica

                }
                else{
                    Intent intent = new Intent(Home.this, Modifica.class); //reinderizzo a Modificadati
                    Home.this.startActivity(intent);
                }
            }
        });

        mFabItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguot();
            }
        });


        if (!sessione.loggedin()) {
            loguot();
        }
        user=sessione.user();

        // Controllo se il BLE è supportato
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,
                    "BLE non supportato!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();




        // Controlla se il Bluetooth è supportato.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth non supportato",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        requestPermission();
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );


        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            displayPromptForEnablingGPS();        }


        Intent i= new Intent(this, BluetoothLeService.class);

        if(mBluetoothAdapter.isEnabled() &&  manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            if(started==false) {
                this.startService(i);
                started=true;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Nascondo l'icona sandwich
        toggle.setDrawerIndicatorEnabled(false);

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
        if(!mBluetoothAdapter.isEnabled()) {
            Intent i= new Intent(this, BluetoothLeService.class);
            this.stopService(i);
            started=false;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        if (requestCode== RQS_ENABLE_BLUETOOTH &&resultCode == RESULT_OK) {

            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                displayPromptForEnablingGPS();        }else {
            Intent i= new Intent(this, BluetoothLeService.class);
            if(started==false) {
              this.startService(i);
                started=true;
            }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void displayPromptForEnablingGPS()
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(Home.this,R.style.Theme_AppCompat_Light_Dialog_Alert);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Per proseguire, attiva il gps";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                                finish();
                            }
                        })
                .setCancelable(false);
        builder.create().show();
    }


    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(findViewById(android.R.id.content), "I permessi per la posizione servono per effettuare lo scan",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dai permessi", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(Home.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    MY_PERMISSIONS_REQUEST);

                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
            }

        } else {
            // Permission request was denied.
            Snackbar.make(findViewById(android.R.id.content), "I permessi per la posizione sono stati negati",
                    Snackbar.LENGTH_SHORT)
                    .show();
            finish();

        }
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loguot(){
        sessione.UtenteLoggato(false,sessione.user());
        sessione.UtenteGuest(false,sessione.user());
        sessione.eliminaUser();
        finish();
        startActivity(new Intent(Home.this,Login.class));
    }

    public void MostraDatiAmbientali(String temperature,String humidity,String currentDateTimeString){

        Toast.makeText(getApplicationContext(),"Temperatura "+String.valueOf(temperature)+"° Umidità "+ String.valueOf(humidity)+"%",Toast.LENGTH_LONG).show();

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            HomeController homeController=new HomeController();

            // Se ho ricevuto i dati dal server (posizione)
            if (("univpm.korm.View.Connessione.Ricevuti.Server").equals(action)) {
                //toolbar.setBackgroundColor(Color.RED);
                CaricaMappa(homeController,intent);

            }
            // se non ho ricevuto i dati dal server (posizione)
            if (("univpm.korm.View.Connessione.Ricevuti").equals(action)) {

                //toolbar.setBackgroundColor(Color.parseColor("#009933"));
                int humidity=(int)intent.getDoubleExtra("hum",1000);
                int temperature=(int)intent.getDoubleExtra("temp",1000);
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                MostraDatiAmbientali(String.valueOf(temperature),String.valueOf(humidity),currentDateTimeString);

            }
            if(("univpm.korm.View.Connessione.Connesso").equals(action)) {
                String device=intent.getStringExtra("device");
                Snackbar.make(findViewById(android.R.id.content), "Connesso a "+device,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

            if(("univpm.korm.View.Connessione.Trovato").equals(action)) {
                if(contatore==0){
                    scansione.setVisibility(View.INVISIBLE);
                    contatore++;
                }
                String device=intent.getStringExtra("device");
                CaricaMappa(homeController,device);

            }
            if(("univpm.korm.View.Connessione.Scaduto").equals(action)) {
                if(contatore==0){
                    scansione.setText("Nessun Beacon trovato");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            if(("univpm.korm.View.Connessione.Scansione").equals(action)) {
                if(contatore==0){
                    scansione.setText("Ricerca Beacon...");
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    private  void CaricaMappa(HomeController homeController,String device)
    {
        TabPunti coord=homeController.TrovaCoordQuota(device);
        imageView.init(toolbar,Integer.parseInt(coord.x),Integer.parseInt(coord.y),Integer.parseInt(coord.quota));
    }

    private  void CaricaMappa(HomeController homeController,Intent intent)
    {
        String device=intent.getStringExtra("device");
        String humidity=intent.getStringExtra("hum");
        String temperature=intent.getStringExtra("temp");
        String address[]= intent.getStringArrayExtra("address");
        String currentDateTimeString=intent.getStringExtra("data");
        MostraDatiAmbientali(temperature,humidity,currentDateTimeString);
        TabPunti coord=homeController.TrovaCoordQuota(device);
        List<TabPunti> pericolo=homeController.TrovaCoordQuotaPericolo(address);
        imageView.init(toolbar,Integer.parseInt(coord.x),Integer.parseInt(coord.y),Integer.parseInt(coord.quota),pericolo);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("univpm.korm.View.Connessione.Ricevuti");
        intentFilter.addAction("univpm.korm.View.Connessione.Ricevuti.Server");
        intentFilter.addAction("univpm.korm.View.Connessione.Connesso");
        intentFilter.addAction("univpm.korm.View.Connessione.Trovato");
        intentFilter.addAction("univpm.korm.View.Connessione.Scaduto");
        intentFilter.addAction("univpm.korm.View.Connessione.Scansione");
        return intentFilter;
    }


    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled = isImmersiveModeEnabled();
        if (isImmersiveModeEnabled) {
            Log.i("TEST", "Turning immersive mode mode off. ");
        } else {
            Log.i("TEST", "Turning immersive mode mode on.");

        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    private boolean isImmersiveModeEnabled() {
        return ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
    }


}
