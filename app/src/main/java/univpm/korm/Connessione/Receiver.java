package univpm.korm.Connessione;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.text.DateFormat;
import java.util.Date;
import univpm.korm.HomeController;

public class Receiver extends BroadcastReceiver {
    private Sessione sessione;
    private Handler mHandler;
    private Context ReceiverContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        sessione=new Sessione(context);
        mHandler=new Handler();
        ReceiverContext=context;


            if(("univpm.korm.View.Connessione.Scaduto").equals(action)){
                int stopperiod = intent.getIntExtra("stopperiod",5000);
                if (sessione.user()!="logout") {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentservice=new Intent(ReceiverContext, BluetoothLeService.class);
                            ReceiverContext.startService(intentservice);
                        }
                    }, stopperiod);
                }
            }


            if(("univpm.korm.View.Connessione.Trovato").equals(action)){
                String device = intent.getStringExtra("device");

                if (!"".equals(sessione.ip())) {
                    Intent intentservice=new Intent(context, InvioDatiService.class);
                    intentservice.setAction("univpm.korm.View.Connessione.Trovato");
                    intentservice.putExtra("device",device);
                    intentservice.putExtra("user",sessione.user());
                    context.startService(intentservice);
                }
            }
        

            if (("univpm.korm.View.Connessione.Ricevuti").equals(action)) {

                    String device = intent.getStringExtra("device");
                    int humidity = (int) intent.getDoubleExtra("hum", 1000);
                    int temperature = (int) intent.getDoubleExtra("temp", 1000);
                    String humsend = String.valueOf(humidity);
                    String tempsend = String.valueOf(temperature);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    HomeController homeController = new HomeController();
                    homeController.updatesaveBeacon(device, currentDateTimeString, String.valueOf(temperature), String.valueOf(humidity));


                if (!"".equals(sessione.ip())) {

                    Intent intenteservice = new Intent(context, InvioDatiService.class);
                    intenteservice.setAction("univpm.korm.View.Connessione.Ricevuti.Invio");
                    intenteservice.putExtra("hum", humsend);
                    intenteservice.putExtra("temp", tempsend);
                    intenteservice.putExtra("data", currentDateTimeString);
                    intenteservice.putExtra("device", device);
                    context.startService(intenteservice);

                }

            }

    }

}
