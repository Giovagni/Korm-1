package univpm.korm.Connessione;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import univpm.korm.HomeController;
import univpm.korm.LoginController;
import univpm.korm.TabPunti;
import univpm.korm.R;
import univpm.korm.Home;

public class InvioDatiService extends Service {
    private String Nome;
    private String Cognome;
    private String User ;
    private String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;
    private String ConfPass;
    private String ip;
    private String porta;
    private String codice;
    private String x;
    private String y;
    private String quota;
    private String address;
    private String data;
    private String olduser;
    private String result;
    private String hum;
    private String temp;
    private String device;
    private String currentdate;
    private String azione;
    private Sessione sessione;

    JSONObject jsonObject = new JSONObject();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        azione=action;
        sessione=new Sessione(this);
        ip=sessione.ip();
        porta=sessione.porta();

        if(("univpm.korm.View.ModificaDati.Utente").equals(action)) {


            Nome = intent.getStringExtra("nome");
            Cognome = intent.getStringExtra("cognome");
            Pass = intent.getStringExtra("pass");
            User = intent.getStringExtra("user");
            Sesso = intent.getStringExtra("sesso");
            Problemi = intent.getStringExtra("problemi");
            DataN = intent.getStringExtra("datan");
            olduser=intent.getStringExtra("olduser");


            try {
                jsonObject.put("nome", Nome);
                jsonObject.put("cognome", Cognome);
                jsonObject.put("pass", Pass);
                jsonObject.put("user", User);
                jsonObject.put("sesso", Sesso);
                jsonObject.put("problemi", Problemi);
                jsonObject.put("datan", DataN);
                jsonObject.put("olduser", olduser);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati/modificautente");
        }

        if(("univpm.korm.View.ModificaDati").equals(action)) {

            User = intent.getStringExtra("user");


            try {
                jsonObject.put("user",User);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati/modifica");
        }

        if(("univpm.korm.Login.Utenti").equals(action)) {

            User = intent.getStringExtra("user");
            Pass= intent.getStringExtra("pass");

            try {
                jsonObject.put("user",User);
                jsonObject.put("pass",Pass);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati/db");
        }

        if(("univpm.korm.Login.Punti").equals(action)) {

            int z1=0;
            LoginController salvapunti=new LoginController();

            String a1[]=intent.getStringArrayExtra("arraypunti");
            ArrayList<String> cod_app= new ArrayList<String> ();
            ArrayList<String> x_app= new ArrayList<String> ();
            ArrayList<String> y_app= new ArrayList<String> ();
            ArrayList<String> quota_app= new ArrayList<String> ();
            ArrayList<String> address_app= new ArrayList<String> ();
            ArrayList<String> data_app= new ArrayList<String> ();

            int i=0;
            int numPunti=salvapunti.contaPuntiController();

            while (z1 < a1.length) {
                codice = a1[z1];
                codice.trim();
                cod_app.add(codice);
                x = a1[z1 + 1];
                x.trim();
                x_app.add(x);
                y = a1[z1 + 2];
                y.trim();
                y_app.add(y);
                quota = a1[z1 + 3];
                quota.trim();
                quota_app.add(quota);
                address = a1[z1 + 4];
                address.trim();
                address_app.add(address);
                data = a1[z1 + 5];
                data.trim();
                data_app.add(data);


                if(numPunti==0){
                    int h=salvapunti.SalvaPuntiController(a1[z1], a1[z1 + 1], a1[z1 + 2], a1[z1 + 3], a1[z1 + 4], a1[z1 + 5]);
                    switch (h) {
                        case 0:
                            displayToast("Punti x,y mancanti!");
                            break;
                        case 1:
                            displayToast("Quota mancante!");
                            break;
                        case 2:
                            Log.e("Codice:", a1[z1] + " inserito!");

                    }
                }else {
                    salvapunti.AggiornaoldDatabaseController(a1[z1], a1[z1 + 1], a1[z1 + 2], a1[z1 + 3], a1[z1 + 4], a1[z1 + 5]);
                }

                z1 = z1 + 6;
                i=i+1;

            }

            try {
                jsonObject.put("codice", new JSONArray(cod_app));
                jsonObject.put("x", new JSONArray(x_app));
                jsonObject.put("y", new JSONArray(y_app));
                jsonObject.put("quota", new JSONArray(quota_app));
                jsonObject.put("address", new JSONArray(address_app));
                jsonObject.put("data", new JSONArray(data_app));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati/punti");






        }

        if(("univpm.korm.Registrazione").equals(action)) {

            Nome = intent.getStringExtra("nome");
            Cognome = intent.getStringExtra("cognome");
            Pass = intent.getStringExtra("pass");
            User = intent.getStringExtra("user");
            Sesso = intent.getStringExtra("sesso");
            Problemi = intent.getStringExtra("problemi");
            DataN = intent.getStringExtra("datan");
            ConfPass = intent.getStringExtra("confpass");


            try {
                jsonObject.put("nome", Nome);
                jsonObject.put("cognome", Cognome);
                jsonObject.put("pass", Pass);
                jsonObject.put("user", User);
                jsonObject.put("sesso", Sesso);
                jsonObject.put("problemi", Problemi);
                jsonObject.put("datan", DataN);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (!ConfPass.equals(Pass)) {
                displayToast("Le password non corrispondono!");

            } else if (User.isEmpty() || Pass.isEmpty()) {
                displayToast("User e password non possono essere vuoti!");
            } else {
                new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati");
            }
        }

        if(("univpm.korm.View.Connessione.Ricevuti.Invio").equals(action)) {

            hum= intent.getStringExtra("hum");
            temp= intent.getStringExtra("temp");
            device=intent.getStringExtra("device");
            currentdate =intent.getStringExtra("data");
            int soglia=sessione.soglia();



            try {
                jsonObject.put("umd", hum);
                jsonObject.put("temp", temp);
                jsonObject.put("datatime", currentdate);
                jsonObject.put("address", device);
                jsonObject.put("soglia",soglia);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati/AggiornaDati");
        }


        if(("univpm.korm.View.Connessione.Trovato").equals(action)) {

            device=intent.getStringExtra("device");
            String user=intent.getStringExtra("user");
            String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            try {
                jsonObject.put("user", user);
                jsonObject.put("address", device);
                jsonObject.put("currentdate",currentDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new send().execute("http://" + ip + ":" + porta + "/KormServer/RicezioneDati/InvioNotifiche");

        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    public void displayToast(String message){
        Toast.makeText(InvioDatiService.this, message, Toast.LENGTH_SHORT).show();
    }


    private InputStream ApriConnessioneHttp(String urlString) throws IOException
    {
        InputStream in = null;
        int risposta = -1;

        URL url = new URL(urlString);


        try{

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();


            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

            BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String value = bf.readLine();
            result= value;
            risposta = httpURLConnection.getResponseCode();

            if (risposta == HttpURLConnection.HTTP_OK) {
                in = httpURLConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            Log.d("Connessione", ex.getLocalizedMessage());
            throw new IOException("Errore connessione");
        }
        return in;
    }

    private String avvia(String URL)
    {
        String bit = null;
        InputStream in = null;
        try {
            in = ApriConnessioneHttp(URL);

            bit = "Operazione eseguita";
            in.close();
        }
        catch (IOException e1) {
            Log.d("Servizio web", e1.getLocalizedMessage());
        }
        return bit;
    }

    private class send extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            return avvia(urls[0]);

        }

        protected void onPostExecute(String s) {

            if(("univpm.korm.Registrazione").equals(azione)) {

                displayToast(result);
                if(result.equals("Utente Registrato Server")){
                    Intent intent = new Intent("univpm.korm.Registrazione.Risposta");
                    sendBroadcast(intent);
                }


            }
            if(("univpm.korm.View.Connessione.Ricevuti.Invio").equals(azione)) {


            }

            if(("univpm.korm.Login.Punti").equals(azione)) {
                final String controllo=result;
                if (controllo!=null){
                    String dati[]=result.split(",");


                    LoginController aggiornapunti=new LoginController();

                    if(dati.length>1)
                    {
                        int i=0;
                        while (i<dati.length-1)
                        {
                            aggiornapunti.AggiornadatiController(dati[i],dati[i+1],dati[i+2],dati[i+3],dati[i+4],dati[i+5]);
                            i=i+6;
                        }
                    }

                    int c=0;
                    if(c==0){
                        if(result.contains("online")){
                            c=c+1;
                            Intent intent= new Intent("univpm.korm.Login.Punti");
                            sendBroadcast(intent);
                        }
                    }
                }



            }

            if(("univpm.korm.Login.Utenti").equals(azione)) {
                Intent intent= new Intent("univpm.korm.Login.Utenti");
                intent.putExtra("risultato", result);
                sendBroadcast(intent);


            }

            if(("univpm.korm.View.ModificaDati").equals(azione)) {
                Intent intent= new Intent("univpm.korm.View.ModificaDati");
                intent.putExtra("risultato", result);
                sendBroadcast(intent);

            }

            if(("univpm.korm.View.ModificaDati.Utente").equals(azione)) {
                if(result.equals("Utente Esistente sul Server")){
                    displayToast(result);
                }else{
                    displayToast(result);
                    Intent intent= new Intent("univpm.korm.View.ModificaDati.Utente");
                    sendBroadcast(intent);
                }
            }

            if(("univpm.korm.View.Connessione.Trovato").equals(azione)) {
                final String risultato=result;
                String username="";
                if (risultato!=null) {
                    String a1[] = result.split(",");
                    if (a1.length > 1) {
                        username=a1[0];
                        if (username.contains("Guest"))
                        {
                            sessione.UtenteGuest(true,username);
                        }
                        temp = a1[1];
                        hum = a1[2];
                        String data = a1[3];
                        String totaddress[] = new String[a1.length-4];
                        String addresssend[]=new String[1];
                        addresssend[0]="niente";
                        for (int i=4;i<a1.length;i++)
                        {
                            totaddress[i-4]=a1[i];
                        }
                        final Intent intent = new Intent();
                        if(totaddress.length!=0){
                            HomeController homeController=new HomeController();

                            String quota=homeController.TrovaCoordQuota(device).quota;
                            Log.w("tot", String.valueOf(totaddress[0]));
                            List<TabPunti> pericolo=homeController.TrovaCoordQuotaPericolo(totaddress);

                            int j=0;
                            String content="Incendio a quota:";

                            for (int i=0;i<pericolo.size();i++)
                            {
                                if(!content.contains(pericolo.get(i).quota))
                                {
                                    content=content+" "+pericolo.get(i).quota;
                                }

                                if(pericolo.get(i).quota.equals(quota))
                                {
                                    addresssend[j]=pericolo.get(i).address;
                                    j=j+1;
                                }
                            }
                            if (addresssend.length!=0)
                            {

                                intent.setAction("univpm.korm.View.Connessione.Ricevuti.Server");

                                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Intent i=new Intent(getBaseContext(),Home.class);
                                i.setAction("univpm.korm.View.Connessione.Notifica");
                                i.putExtra("devicenotifica",device);
                                PendingIntent pi= PendingIntent.getActivity(getBaseContext(), 0, i, 0);
                                NotificationCompat.Builder n  = new NotificationCompat.Builder(getBaseContext())
                                        .setContentTitle("Attenzione")
                                        .setContentText(content)
                                        .setColor(Color.BLUE)
                                        .setSound(sound)
                                        .setContentIntent(pi)
                                        .setAutoCancel(true)
                                        .setSmallIcon(R.drawable.charizard);
                                NotificationManager mNotificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(1, n.build());

                            }else{
                                intent.setAction("univpm.korm.View.Connessione.Ricevuti.");
                            }
                            intent.putExtra("hum", hum);
                            intent.putExtra("temp", temp);
                            intent.putExtra("data", data);
                            intent.putExtra("device",device);
                            intent.putExtra("address",addresssend);
                            sendBroadcast(intent);
                        }
                    }
                }
            }



        }
    }

}