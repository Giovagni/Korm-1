package univpm.korm.Connessione;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import static android.content.ContentValues.TAG;


/*Service che si occupa di connettere e leggere i dati dal Beacon.*/
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

public class BluetoothLeService extends Service {
    private static String LOG_TAG = "BluetoothLeService";
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    // Stops scanning after 10 seconds.
    private static int SCAN_PERIOD = 10000;
    private static int PAUSE_PERIOD = 5000;

    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private String finaladdress;
    private static BluetoothGatt finalgatt;
    private Sessione sessione;



    private static final byte[] ENABLE_SENSOR = {0x01};




    @Override
    public void onCreate() {
        super.onCreate();
        getBluetoothAdapterAndLeScanner();
        mHandler = new Handler();
        mBluetoothDeviceAddress="";
        sessione=new Sessione(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getBluetoothAdapterAndLeScanner() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Intent intent1 = new Intent("univpm.korm.View.Connessione.Scansione");
        sendBroadcast(intent1);
        if (sessione.user()!="logout") {
          scanLeDevice(true);
        }else{
            close();
            disconnect();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Lo scan si ferma dopo un tempo predefinito.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(scanCallback);
                    final Intent intent1 = new Intent("univpm.korm.View.Connessione.Scaduto");
                    intent1.putExtra("stopperiod",PAUSE_PERIOD);
                    sendBroadcast(intent1);
                }
            }, SCAN_PERIOD);
            mBluetoothLeScanner.startScan(scanCallback);
        } else {
            mBluetoothLeScanner.stopScan(scanCallback);
        }
    }



    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            final String deviceName = device.getName();
            mBluetoothDeviceAddress=device.getAddress();
            if(sessione.user()!="logout") {
                if (deviceName != null && deviceName.length() > 0) {
                    if (deviceName.contains("SensorTag")) {
                        connect();
                        scanLeDevice(false);
                        final Intent intent = new Intent("univpm.korm.View.Connessione.Trovato");
                        Sessione sessione = new Sessione(getBaseContext());
                        intent.putExtra("user", sessione.user());
                        intent.putExtra("device", mBluetoothDeviceAddress);
                        sendBroadcast(intent);

                    }
                } else {
                    Log.e("Errore", String.valueOf(callbackType));
                }
            }else{
                close();
                disconnect();
            }
        }
    };





    /* Connessione a dispositivo*/
    public boolean connect() {
        if (mBluetoothAdapter == null || mBluetoothDeviceAddress == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        finalgatt=mBluetoothGatt;
        Log.d(TAG, "Trying to create a new connection.");
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    /*Messaggio di risposta ottenuto dopo le interazioni col dispositivo*/
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /*Metodo che viene richiamato quando cambia lo stato della connessione (da non connesso passo a connesso e viceversa) */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(sessione.user()!="logout"){
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                mBluetoothDeviceAddress=gatt.getDevice().getAddress();
                finaladdress=gatt.getDevice().getAddress();
                broadcasUpdate("univpm.korm.View.Connessione.Connesso",mBluetoothDeviceAddress);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
            }
            gatt.discoverServices();
            }else{
                close();
                disconnect();
            }
        }


        /*Metodo che viene richiamato una volta che sono stati scoperti i servizi offerti dal dispositivo */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(sessione.user()!="logout") {
            /* Mi associo al service che mi interessa*/
                BluetoothGattService humidityService = gatt.getService(SensorTagGatt.UUID_HUM_SERV);

            /* Mi associo ala caratteristica che mi interessa*/
                BluetoothGattCharacteristic enableHum = humidityService.getCharacteristic(SensorTagGatt.UUID_HUM_CONF);

            /*Assegno alla caratteristica desiderata il valore che accende il sensore*/
                enableHum.setValue(ENABLE_SENSOR);

            /*Invio il dato al sensore*/
                gatt.writeCharacteristic(enableHum);
            }else{
                close();
                disconnect();
            }

        }

        /*Metodo che viene richiamato una volta che sono stati iviati dati al dispositivo */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if(sessione.user()!="logout") {
                BluetoothGattService humidityService = gatt.getService(SensorTagGatt.UUID_HUM_SERV);

                BluetoothGattCharacteristic humidityCharacteristic = humidityService.getCharacteristic(SensorTagGatt.UUID_HUM_DATA);

            /*richiedo di leggere il valore della caratteristica*/
                gatt.readCharacteristic(humidityCharacteristic);
            }else{
                close();
                disconnect();
            }

        }
        /*Metodo che viene richiamato una volta che sono stati richiesti i dati al dispositivo*/
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(sessione.user()!="logout"){
            /*Controllo se il valore è uguale a 0, se è così vuol dire che abbiamo preso il valore prima che il sensore si accendesse quindi provo a leggere di nuovo */
            byte []value=characteristic.getValue();
            int t = shortUnsignedAtOffset(value, 0);
            if (t==0)
            {
                gatt.readCharacteristic(characteristic);
            }else {
                /*se il valore ricevuto è diverso da 0 abbiamo letto i dati e li inviamo alla home */
                broadcastUpdate("univpm.korm.View.Connessione.Ricevuti",characteristic);
            }

            }else{
                close();
                disconnect();
            }
        }

    };

    private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }


    /*Tutte le funzioni broadcast inviano un intent caratterizzato da un'azione e da dati dati, il receiver si comporta in modo diverso in base all'azione dell'intent */
    private void broadcastUpdate(final String action,BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        double humidity = SensorTagData.extractHumidity(characteristic);
        double temperature = SensorTagData.extractHumAmbientTemperature(characteristic);
        intent.putExtra("device",finaladdress);
        intent.putExtra("hum",humidity);
        intent.putExtra("temp",temperature);
        sendBroadcast(intent);
        if(!disconnect()){
            disconnect();
        }
        close();
        stopSelf();

    }

    private void broadcasUpdate(final String action,String device){
        final Intent intent =new Intent(action);
        intent.putExtra("device",device);
        sendBroadcast(intent);
    }

    public void broadcastUpdate(final String action){
        Intent intent=new Intent(action);
        sendBroadcast(intent);
    }

    public boolean disconnect() {
        if (mBluetoothAdapter == null || finalgatt == null) {
            mBluetoothGatt=finalgatt;
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        finalgatt.disconnect();
        return true;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}
