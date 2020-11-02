package com.example.bconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    Button ButtonON;
    Button ButtonOFF;
    Button listPaired;
    BluetoothAdapter BAdapter=BluetoothAdapter.getDefaultAdapter();;
    ListView listDevice;
    TextView textView;
    ArrayList<String> stringArr=new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    int index = 0;

    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private static final UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice[] btArray;

    Intent BEnableIntent;
  // IntentFilter intentFilter;
    int enableRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonON =(Button) findViewById(R.id.bOn);
        ButtonOFF =(Button) findViewById(R.id.bOff);
        listPaired=(Button) findViewById(R.id.listPaired);
        listDevice=(ListView) findViewById(R.id.listDevices);
        textView=(TextView) findViewById(R.id.textView);

        BEnableIntent =new Intent(BAdapter.ACTION_REQUEST_ENABLE);
        enableRequestCode =1;
        BluetoothOnMethod();
        BlueToothOffMethod();


        findPairedDevices();

    }

    private void findPairedDevices() {

        //List Paired Devices
        listPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bAdapter==null){
                    Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
                }
                else{
                    Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
                    ArrayList list = new ArrayList();
                    btArray =new BluetoothDevice[pairedDevices.size()];
                    if(pairedDevices.size()>0) {

                        for (BluetoothDevice device : pairedDevices) {
                                btArray[index] = device;
                                index++;
                            String deviceName = device.getName();
                            String macAddress = device.getAddress();

                            list.add("Name: " + deviceName + "MAC Address: " + macAddress);
                        }
                        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                        listDevice.setAdapter(arrayAdapter);


                    }
                }



                /*
                //List Available Devices
                if(adapter.isDiscovering()){
                    adapter.cancelDiscovery();
                    //check BT permissions in manifest
                    // checkBTPermissions();
                    textView.setText("111111111");

                    adapter.startDiscovery();
                    IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiver,intentFilter);
                }
                if(!adapter.isDiscovering()){
                    //check BT permissions in manifest
                    // checkBTPermissions();
                    adapter.startDiscovery();
                    IntentFilter  intentFilter=new IntentFilter();
                    intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiver,intentFilter);
                    textView.setText("3333333333");
                }
                */


            }

        });

         listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(btArray[position].getName());
                ClientClass clientClass=new ClientClass(btArray[position]);
                clientClass.start();

            }
        });


    }


    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device= (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArr.add(device.getName());
                textView.setText("22222222");

                textView.setText(device.getName());

                arrayAdapter.notifyDataSetChanged();
                arrayAdapter =new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArr);
                listDevice.setAdapter(arrayAdapter);
                Toast.makeText(getApplicationContext(),device.getName() , Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == enableRequestCode) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth Connection Canceled", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void BluetoothOnMethod() {

        ButtonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BAdapter ==null)
                {
                    Toast.makeText(getApplicationContext(),"Bluetooth is not Supported on this Device",Toast.LENGTH_LONG).show();
                }
                else{
                    if(!BAdapter.isEnabled())
                    {
                        startActivityForResult(BEnableIntent,enableRequestCode);
                    }
                }

            }
        });

    }

    private void BlueToothOffMethod() {
    ButtonOFF.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(BAdapter.isEnabled())
            {
                BAdapter.disable();
                Toast.makeText(getApplicationContext(),"Bluetooth Disconnected",Toast.LENGTH_LONG).show();
            }

        }
    });
    }


    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        public  void run()
        {
            try {
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}