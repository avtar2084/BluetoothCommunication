package com.example.bconnect;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAcftivity";



    Button ButtonON;
    Button ButtonOFF;
    Button listPaired;
    Button SendMessage;
    EditText message;
    BluetoothAdapter BAdapter=BluetoothAdapter.getDefaultAdapter();
    ListView listDevice;
    TextView textView;
    TextView recivedMessage;



    ArrayList<String> stringArr=new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    int index = 0;

    SendReceive sendReceive;

    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private static final String appName ="Bconnect";
    private static final UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID uuid2=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int psm=0;
    BluetoothDevice[] btArray;

    Intent BEnableIntent;
  // IntentFilter intentFilter;
    int enableRequestCode;

    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonON =(Button) findViewById(R.id.bOn);
        ButtonOFF =(Button) findViewById(R.id.bOff);
        listPaired=(Button) findViewById(R.id.listPaired);
        listDevice=(ListView) findViewById(R.id.listDevices);
        SendMessage=(Button) findViewById(R.id.send);
        message=(EditText) findViewById(R.id.message);
        textView=(TextView) findViewById(R.id.textView);
        recivedMessage = (TextView) findViewById(R.id.textView1);


        BEnableIntent =new Intent(BAdapter.ACTION_REQUEST_ENABLE);
        enableRequestCode =1;
        BluetoothOnMethod();
        BlueToothOffMethod();


        findPairedDevices();

        sendMessage();


    }

    private void sendMessage() {

        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str= String.valueOf(message.getText());
                
                sendReceive.write(str.getBytes());
            }
        });
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




                //List all the Available Devices
                if(adapter.isDiscovering()){
                    adapter.cancelDiscovery();
                    //check BT permissions in manifest
                    textView.setText("111111111");

                    adapter.startDiscovery();
                    IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiver,intentFilter);
                }
                if(!adapter.isDiscovering()){
                    //check BT permissions in manifest

                    adapter.startDiscovery();
                    //textView.setText("3333333333");
                }


                 broadcastReceiver=new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action=intent.getAction();
                        if(BluetoothDevice.ACTION_FOUND.equals(action))
                        {
                            BluetoothDevice device= (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            stringArr.add(device.getName());
                            textView.setText("22222222");

                            //textView.setText(device.getName());

                            arrayAdapter.notifyDataSetChanged();
                            arrayAdapter =new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArr);
                            listDevice.setAdapter(arrayAdapter);
                            Toast.makeText(getApplicationContext(),device.getName() , Toast.LENGTH_LONG).show();
                        }
                        else{
                            textView.setText("else part");
                        }
                    }
                };


                IntentFilter  intentFilter=new IntentFilter();
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                registerReceiver(broadcastReceiver,intentFilter);



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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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

              // ServerClass serverClass=new ServerClass();
               //serverClass.start();

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

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;
        private BluetoothServerSocket serverSocket2;


        public ServerClass()
        {
            try {

                serverSocket=bAdapter.listenUsingRfcommWithServiceRecord(appName,uuid);
                serverSocket2=bAdapter.listenUsingRfcommWithServiceRecord(appName,uuid2);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                  //  serverSocket=bAdapter.listenUsingInsecureL2capChannel();
                    //bAdapter.setName("My app");
                    Log.i(TAG, "In Server Class " );
                    int psm=serverSocket.getPsm();
                    Log.i(TAG, "Server Psm Value" +psm );

                    //recivedMessage.setText(String.valueOf(psm));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    socket=serverSocket.accept();
                    sendReceive =new SendReceive(socket);
                    sendReceive.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;
        private BluetoothServerSocket l2socket;
        private BluetoothManager bm;


        public ClientClass(BluetoothDevice device1)
        {
            device=device1;

            try {
              socket=device.createRfcommSocketToServiceRecord(uuid);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                   //recivedMessage.setText("12121212");
                    //l2socket=bAdapter.listenUsingL2capChannel();
                    //int psm=l2socket.getPsm();
                   //socket=device.createInsecureL2capChannel(4145);

                   // socket=device.createRfcommSocketToServiceRecord(uuid);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        public  void run()
        {
            try {
                socket.connect();
                sendReceive =new SendReceive(socket);
                sendReceive.start();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIN = null;
            OutputStream tempOut = null;

            try {
                tempIN=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream=tempIN;
            outputStream=tempOut;
        }

        public  void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {

                    //
                    bytes= inputStream.read(buffer);
                    byte[] readBuff=buffer;
                    String tempMsg=new String(readBuff,0,bytes);
                    if(tempMsg!=null)
                    {
                        sendReceive.write(tempMsg.getBytes());
                    }
                    recivedMessage.setText(tempMsg);
                    tempMsg=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}