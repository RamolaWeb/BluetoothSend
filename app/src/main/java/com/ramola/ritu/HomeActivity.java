package com.ramola.ritu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_DISCOVERY = 3000;
    private TextView name, address, status, enableBluetooth;
    private ProgressBar progressBar;
    private Button searchDeviceBtn;
    private RecyclerView recyclerView;
    private LinearLayout deviceDetail, searchLayout, listLayout;
    private BluetoothAdapter bluetoothAdapter;
    private Intent intent;
    private BluetoothBroadCastReceiver receiver;
    private IntentFilter intentFilter, messageFilter;
    private Adapter adapter;
    private MessageReciever messageReciever;
    private ServerSide serverSide;
    private String adapterDevice;
    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialising the View
        name = (TextView) findViewById(R.id.bluetooth_name_textField);
        address = (TextView) findViewById(R.id.bluetooth_mac_address_textField);
        status = (TextView) findViewById(R.id.bluetooth_device_status_textField);
        enableBluetooth = (TextView) findViewById(R.id.bluetooth_availability_textField);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        searchDeviceBtn = (Button) findViewById(R.id.start_discovery);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        deviceDetail = (LinearLayout) findViewById(R.id.layout_device_detail);
        searchLayout = (LinearLayout) findViewById(R.id.layout_start_discovery);
        listLayout = (LinearLayout) findViewById(R.id.layout_device_discover);
        imageView = (ImageView) findViewById(R.id.image);
        //initialising the receiver and intent filter
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        receiver = new BluetoothBroadCastReceiver(this);

        messageFilter = new IntentFilter();
        messageFilter.addCategory(Intent.CATEGORY_DEFAULT);
        messageFilter.addAction(MessageReciever.ACTION_IMAGE);
        messageFilter.addAction(MessageReciever.ACTION_RECIEVE);
        messageReciever = new MessageReciever(this);

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapterDevice = adapter.getList().get(position).address;
            }
        }));
        //initialising the bluetooth adapter and finding wheather  bluetooth is enabled

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            enableBluetooth.setText("No Bluetooth Aviailable");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                enableBluetooth.setText("Enable Bluetooth");
                enableBluetooth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE);
                    }
                });

            } else {
                enableBluetooth.setText("Disable Bluetooth");
                putDetail();
                enableBluetooth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bluetoothAdapter.disable();
                    }
                });
            }
        }

        searchDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, REQUEST_DISCOVERY);
                startActivityForResult(intent, REQUEST_DISCOVERY);
            }
        });

        serverSide = new ServerSide();


    }

    /**
     * @param result true if u want to display
     *               This method is used to make device detail layout visible or not
     */

    private void displayDeviceDetail(boolean result) {
        if (result) {
            deviceDetail.setVisibility(View.VISIBLE);
        } else {
            deviceDetail.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to make search button layout visible or not
     *
     * @param displayButton   true if u want to display search layout
     * @param displayProgress true if u want to display progress bar
     */
    public void displaySearchLayout(boolean displayButton, boolean displayProgress) {
        searchLayout.setVisibility(View.VISIBLE);
        if (displayButton) {
            searchDeviceBtn.setVisibility(View.VISIBLE);
        } else {
            searchDeviceBtn.setVisibility(View.GONE);
        }
        if (displayProgress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to make device List layout visible or not
     *
     * @param result true if u want to display the List layout
     */
    public void displayList(boolean result) {
        if (result) {
            listLayout.setVisibility(View.VISIBLE);
        } else {
            listLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:
                    enableBluetooth.setText("Disable Bluetooth");
                    putDetail();
                    break;
            }
        }
        if (resultCode == REQUEST_DISCOVERY) {
            if (requestCode == REQUEST_DISCOVERY) {
                displaySearchLayout(false, true);
                if (bluetoothAdapter.startDiscovery()) {
                    Log.d("TAG", "started discovery");
                }
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(filePath, filePathColumn, null, null, null);
            c.moveToFirst();
            String imgDecodableString = c.getString(c.getColumnIndex(filePathColumn[0]));
            c.close();
            Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
            if (bluetoothAdapter != null && !adapterDevice.isEmpty())
                new ClientSide(bluetoothAdapter, this, decodeImage(bitmap)).execute(adapterDevice);
            else {
                Toast.makeText(this, "Please Select any device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * To display the bluetooth status of device
     *
     * @param code return from broadcast receiver
     */
    public void setStatus(int code) {
        status.setText(Utility.getStatus(code));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        registerReceiver(messageReciever, messageFilter);
        startService(new Intent(HomeActivity.this, ServerSide.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(messageReciever);
        stopService(new Intent(HomeActivity.this, ServerSide.class));
    }

    /**
     * @param d Bluetooth device return by Broadcast reciever
     */
    public void addDevice(BluetoothDevice d) {
        adapter.add(new Device(d.getName(), d.getAddress()));
    }

    private void putDetail() {
        displayDeviceDetail(true);
       if(adapter!=null){
           if(adapter.getList().size()==0)
        displaySearchLayout(true, false);}
        name.setText(bluetoothAdapter.getName());
        address.setText(bluetoothAdapter.getAddress());
        status.setText(Utility.getStatus(bluetoothAdapter.getState()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sendImage:
                createchooser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private byte[] decodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    private void createchooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "CHOOSE PHOTO"), PICK_IMAGE_REQUEST);
    }

    public void displayImage(boolean is) {
        if (is)
            imageView.setVisibility(View.VISIBLE);
        else {
            imageView.setVisibility(View.GONE);
        }
    }

    public void doIFNoDeviceFound() {
        if (adapter.getList().size() == 0) {
            displaySearchLayout(true,false);
            Toast.makeText(this, "No Device Found", Toast.LENGTH_LONG).show();
        }
    }
}
