package hu.unideb.inf.kovacs.laszlo.myapplicationbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button button, button2;
    public static final String SERVICE_ADDRESS = "";
    private BluetoothDevice telescope = null;
    private BluetoothSocket btSocket = null;
    public BluetoothAdapter btAdapter;
    public BluetoothDevice btDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        System.out.println(btAdapter.getBondedDevices());
        for (BluetoothDevice item : btAdapter.getBondedDevices()) {
            System.out.println(item.getName() + " " + item.getAddress());
            if ("Telescope".equals(item.getName())) {
                System.out.println("Megvan a telescope");
                telescope = btAdapter.getRemoteDevice(item.getAddress());
            }
        }
        try {
            if (telescope != null) {
                System.out.println("Nem nulla a telescope");
                btSocket = telescope.createRfcommSocketToServiceRecord(mUUID);
                do {
                    btSocket.connect();
                    System.out.println(btSocket.isConnected());
                } while (!btSocket.isConnected());
            } else {
                System.out.println("Telescope null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void kuld_fg(View view) throws IOException {
        EditText editText = (EditText) findViewById(R.id.uzenet);
        OutputStream outputStream = btSocket.getOutputStream();
        outputStream.write((editText.getText().toString() + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    public void bont_fg(View view) {
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void kapcs_fg(View view) throws IOException {
        if(!btSocket.isConnected()){
            btSocket = telescope.createRfcommSocketToServiceRecord(mUUID);
            do {
                btSocket.connect();
                System.out.println(btSocket.isConnected());
            } while (!btSocket.isConnected());
        }
    }
}