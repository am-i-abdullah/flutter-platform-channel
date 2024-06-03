package com.netum.example.message;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.netum.device.BLEManager;
import com.netum.device.data.BleDevice;
import com.netum.example.R;
import com.netum.example.common.Observer;
import com.netum.example.common.ObserverManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class MessageActivity extends AppCompatActivity implements Observer,View.OnClickListener{

    public static  MessageActivity Instance=null;
    private Toolbar toolbar;
    private MessageFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Instance=this;

        setContentView(R.layout.activity_message);
        initData();
        initView();
        initPage();

        ObserverManager.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Instance=null;
        ObserverManager.getInstance().deleteObserver(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.valueOf(getString(R.string.debugTitle)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {

    }

    private void initPage() {
        prepareFragment();
        updateFragment();
    }

    private void prepareFragment() {
        fragment=new MessageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).hide(fragment).commit();
    }

    private void updateFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(fragment);
        //transaction.hide(fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {

    }

    public void addMessage(String address,String content){
        if(fragment!=null){
            fragment.addMessage(address,content);
        }
    }

    @Override
    public void disConnected(BleDevice bleDevice) {
        int count= BLEManager.getInstance().getAllConnectedDevice().size();
        if (count==0) {
            finish();
        }

    }
}
