package com.example.hyun.myapplication;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.*;
import android.os.*;
import android.content.*;
import android.bluetooth.*;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.hyun.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final int ACTION_ENABLE_BT = 101;
    TextView mTextMsg;
    BluetoothAdapter mBA;
    ListView mListDevice;
    ArrayList<String> mArDevice;
    static final String BLUE_NAME = "BluetoothEx";
    static final UUID BLUE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ClientThread mCThread = null;
    ServerThread mSThread = null;
    SocketThread mSocketThread = null;
    public  BluetoothSocket mmSocket;
    public InputStream mmInStream;
    private OutputStream mmOutStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMsg = (TextView)findViewById(R.id.textView);

        initListView();

        boolean isBlue = canUseBluetooth();
        if(isBlue)
            getPairedDevice();
    }

    public boolean canUseBluetooth() {
        mBA = BluetoothAdapter.getDefaultAdapter();
        if(mBA == null) {
        //    mTextMsg.setText("Device not found");
            return false;
        }

      //  mTextMsg.setText("Device is exist");
        if(mBA.isEnabled()) {
          //  mTextMsg.append("\nDevice can use");
            return true;
        }

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, ACTION_ENABLE_BT);
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_ENABLE_BT) {
            if(resultCode == RESULT_OK) {
    //            mTextMsg.append("\nDevice can use");
                getPairedDevice();
            }
            else {
     //           mTextMsg.append("\nDevice can not use");
            }
        }
    }

    public void startFindDevice() {
        stopFindDevice();
        mBA.startDiscovery();
        registerReceiver(mBlueRecv, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void stopFindDevice() {
        if(mBA.isDiscovering()) {
            mBA.cancelDiscovery();
            unregisterReceiver(mBlueRecv);
        }
    }

    BroadcastReceiver mBlueRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == BluetoothDevice.ACTION_FOUND) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                    addDeviceToList(device.getName(), device.getAddress());
            }
        }
    };

    public void addDeviceToList(String name, String address) {
        String deviceInfo = name + " - " + address;
        Log.d("tag1", "Device Find:" + deviceInfo);
        mArDevice.add(deviceInfo);

        ArrayAdapter adapter = (ArrayAdapter)mListDevice.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public void initListView() {
        mArDevice = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArDevice);
        mListDevice = (ListView)findViewById(R.id.listDevice);
        mListDevice.setAdapter(adapter);
        mListDevice.setOnItemClickListener(this);
    }

    public void setDiscoverable() {
        if(mBA.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
            return;
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(intent);
    }

    public void getPairedDevice() {
        if(mSThread != null) return;
        mSThread = new ServerThread();
        mSThread.start();

        Set<BluetoothDevice> devices = mBA.getBondedDevices();
        for(BluetoothDevice device : devices) {
            addDeviceToList(device.getName(), device.getAddress());
        }

        startFindDevice();
        setDiscoverable();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String strItem = mArDevice.get(position);

        int pos = strItem.indexOf(" - ");
        if(pos <= 0)
            return;

        String address = strItem.substring(pos + 3);
  //      mTextMsg.setText("Sel Device: " + address);

        stopFindDevice();
        mSThread.cancel();
        mSThread = null;

        if(mCThread != null) return;
        BluetoothDevice device = mBA.getRemoteDevice(address);
        mCThread = new ClientThread(device);
        mCThread.start();
    }

    private class ClientThread extends Thread {
        private BluetoothSocket mmCSocket;

        public ClientThread(BluetoothDevice device) {
            try {
                mmCSocket = device.createInsecureRfcommSocketToServiceRecord(BLUE_UUID);
            } catch (IOException e) {
                showMessage("Create Client Socket error");
                return;
            }
        }

        public void run() {
            try {
                mmCSocket.connect();
            } catch(IOException e) {
                showMessage("기기연결 실패! 장치를 켜주세요.");

                try {
                    mmCSocket.close();
                } catch (IOException e2) {
                    showMessage("Client Socket close error");
                }
                return;
            }
            onConnected(mmCSocket);
        }

        public void cancel() {
            try {
                mmCSocket.close();
            } catch (IOException e) {
                showMessage("Client Socket close error");
            }
        }
    }

    private  class ServerThread extends Thread {
        private BluetoothServerSocket mmSSocket;

        public ServerThread() {
            try {
                mmSSocket = mBA.listenUsingInsecureRfcommWithServiceRecord(BLUE_NAME, BLUE_UUID);
            } catch(IOException e) {
                showMessage("Get Server Socket Error");
            }
        }

        public void run() {
            BluetoothSocket cSocket = null;

            try {
                cSocket = mmSSocket.accept();
            } catch (IOException e) {
                showMessage("기기 연결중...");
                return;
            }
            onConnected(cSocket);
        }

        public void cancel() {
            try {
                mmSSocket.close();
            } catch (IOException e) {
                showMessage("Server Socket close error");
            }
        }
    }

    public void showMessage(String strMsg) {
        Message msg = Message.obtain(mHandler, 0, strMsg);
        mHandler.sendMessage(msg);
        Log.d("tag1", strMsg);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                String strMsg = (String)msg.obj;
                mTextMsg.setText(strMsg);
            }
        }
    };

    public void onConnected(BluetoothSocket socket) {
        showMessage("기기연결 완료");

        if (mSocketThread != null)
            mSocketThread = null;

        mSocketThread = new SocketThread(socket);
        mSocketThread.start();
    }

    class SocketThread extends Thread {
        public SocketThread(BluetoothSocket socket) {
            mmSocket = socket;

            try {
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();
            } catch (IOException e) {
                showMessage("Get Stream error");
            }
        }

        public void run() {
            Intent intent4 = getIntent();
            String nps = intent4.getExtras().getString("nps");
            String tpot = intent4.getExtras().getString("tpot");

            if(mSocketThread == null) return;

            String strTpot = tpot;
            String strNps = nps;

            if(strNps.length() < 1) return;

            if(Integer.parseInt(strNps)<10)
                mSocketThread.write(strTpot+ "0" + strNps);
            else
                mSocketThread.write(strTpot+strNps);
        }

        public void write(String strBuf) {
            try {
                byte[] buffer = strBuf.getBytes();
                mmOutStream.write(buffer);

            } catch (IOException e) {
                showMessage("Socket wire error");
            }
        }
    }

    public void onMesButtonClicked(View v) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user");

        Intent intent4 = getIntent();
        final String id = intent4.getExtras().getString("id");
        final String kind = intent4.getExtras().getString("kind");
        final String set = intent4.getExtras().getString("set");
        final String nps = intent4.getExtras().getString("nps");
        final String tpot = intent4.getExtras().getString("tpot");
        final String detail = intent4.getExtras().getString("detail");

        myRef.child(id).child("kind").child(kind).child(detail).child("Ex"+set).child("nps").setValue(nps);
        myRef.child(id).child("kind").child(kind).child(detail).child("Ex"+set).child("tpot").setValue(tpot);
        myRef.child(id).child("kind").child(kind).child(detail).child("Ex"+set).child("correction").setValue("no");
        myRef.child(id).child("friends").setValue(" ");

        myRef.child(id).child("kind").child(kind).child(detail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                byte[] buffer = new byte[4];
                String s;
                String fb = new String();
                int cnt = 0;

                while (child.hasNext()) {
                    if(child.next().getKey().equals("sample")) {

                        int ex_time = 10 * Integer.parseInt(nps) * Integer.parseInt(tpot) -1;

                        mSocketThread.write("999");

                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        while (true) { // set횟수 * 회당 시간
                            try {
                                while (mmInStream.available() < 4) ;
                                mmInStream.read(buffer, 0, 4);
                                s = new String(buffer).replaceAll(" ", "");

                                if (cnt <= ex_time)
                                    fb += s + ",";
                                else {
                                    fb += s;
                                    sleep(100);

                                    break;
                                }

                                cnt++;
                            } catch (IOException e) {
                                showMessage("Socket disconnected");
                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        myRef.child(id).child("kind").child(kind).child(detail).child("Ex"+set).child("data").setValue(fb);

                        Toast.makeText(getApplicationContext(), "측정 완료", Toast.LENGTH_LONG).show();

                        Intent intent5 = new Intent(getApplicationContext(), StartActivity.class);
                        intent5.putExtra("id", id);
                        intent5.putExtra("kind", kind);
                        intent5.putExtra("set", set);
                        intent5.putExtra("detail", detail);

                        startActivity(intent5);
                    }

                    else {
                        Toast.makeText(MainActivity.this, "기준값을 먼저 측정하세요!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onSampleButtonClicked(View v) {
        byte[] buffer = new byte[4];
        String s;
        String fb = new String();
        int cnt = 0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");

        Intent intent4 = getIntent();
        String id = intent4.getExtras().getString("id");
        String kind = intent4.getExtras().getString("kind");
        String set = intent4.getExtras().getString("set");
        //String nps = intent4.getExtras().getString("nps");
        //String tpot = intent4.getExtras().getString("tpot");
        String detail = intent4.getExtras().getString("detail");
        String sample_tpot = "2";
        String sample_nps = "5";

        myRef.child(id).child("kind").child(kind).child(detail).child("sample").child("nps").setValue(sample_nps);
        myRef.child(id).child("kind").child(kind).child(detail).child("sample").child("tpot").setValue(sample_tpot);
        myRef.child(id).child("kind").child(kind).child(detail).child("sample").child("correction").setValue("no");

        int ex_time = 10 * Integer.parseInt(sample_nps) * Integer.parseInt(sample_tpot) -1;

        mSocketThread.write("999");

        while (true) { // set횟수 * 회당 시간
            try {
                while (mmInStream.available() < 4) ;
                mmInStream.read(buffer, 0, 4);
                s = new String(buffer).replaceAll(" ", "");

                if (cnt <= ex_time)
                    fb += s + ",";
                else {
                    fb += s;
                    sleep(100);

                    break;
                }
                cnt++;
            } catch (IOException e) {
                showMessage("Socket disconnected");
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        myRef.child(id).child("kind").child(kind).child(detail).child("sample").child("data").setValue(fb);

        Toast.makeText(getApplicationContext(), "측정 완료", Toast.LENGTH_LONG).show();

        Intent intent5 = new Intent(getApplicationContext(), StartActivity.class);
        intent5.putExtra("id", id);
        intent5.putExtra("kind", kind);
        intent5.putExtra("set", set);
        intent5.putExtra("detail", detail);

        startActivity(intent5);
    }

    public void onDestroy() {
        super.onDestroy();
        stopFindDevice();

        if(mCThread != null) {
            mCThread.cancel();
            mCThread = null;
        }
        if(mSThread != null) {
            mSThread.cancel();
            mSThread = null;
        }
        if(mSocketThread != null)
            mSocketThread = null;
    }
}