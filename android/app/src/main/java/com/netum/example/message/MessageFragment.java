package com.netum.example.message;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.netum.device.BLEManager;
import com.netum.device.callback.BleWriteCallback;
import com.netum.device.data.BleDevice;
import com.netum.device.exception.BleException;
import com.netum.device.instruction.Scanner;
import com.netum.device.instruction.ScannerUtil;
import com.netum.example.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;

public class MessageFragment extends Fragment implements View.OnClickListener
{
    private int receivedCount;
    private Spinner spinner;
    private EditText triggerSecond;
    private Button softTrigger;
    private EditText customBeepLevel;
    private Button customBeep;
    private EditText customBeepTime;
    private EditText customBeepType;
    private EditText customBeepFrequency;
    private Button customBeepSend;
    private Button enableTimeStamp;
    private Button disableTimeStamp;
    private Button timeStampSetFormat;
    private TextView txt_count;
    private MessageFragment.ResultAdapter mResultAdapter;
    private ArrayList<String> starArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message_list, null);
        initView(v);
        initSpinner(v);
        iniData();
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.softTrigger:
                if(TextUtils.isEmpty(triggerSecond.getText().toString())){
                    Toast.makeText(getContext(),getString(R.string.valueCanNotEmpty),Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(triggerSecond.getText().toString()) > 7||Integer.parseInt(triggerSecond.getText().toString()) < 1) {
                    Toast.makeText(getContext(),getString(R.string.softTriggerRange),Toast.LENGTH_SHORT).show();
                }else {
                    WriteData(ScannerUtil.SoftTrigger(Integer.parseInt(triggerSecond.getText().toString())));
                }
                break;
            case R.id.customBeep:
                if(TextUtils.isEmpty(customBeepLevel.getText().toString())){
                    Toast.makeText(getContext(),getString(R.string.valueCanNotEmpty),Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(customBeepLevel.getText().toString()) > 26||Integer.parseInt(customBeepLevel.getText().toString()) < 0) {
                    Toast.makeText(getContext(),getString(R.string.customBeepLevelRange),Toast.LENGTH_SHORT).show();
                }else {
                    WriteData(ScannerUtil.CustomBeep(Integer.parseInt(customBeepLevel.getText().toString())));
                }
                break;
            case R.id.customBeepSend:
                if(TextUtils.isEmpty(customBeepTime.getText().toString())|| TextUtils.isEmpty(customBeepType.getText().toString())|| TextUtils.isEmpty(customBeepFrequency.getText().toString())){
                    Toast.makeText(getContext(),getString(R.string.valueCanNotEmpty),Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(customBeepTime.getText().toString()) > 2540||Integer.parseInt(customBeepTime.getText().toString()) < 10) {
                    Toast.makeText(getContext(),getString(R.string.customBeepTimeRange),Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(customBeepType.getText().toString()) > 26||Integer.parseInt(customBeepType.getText().toString()) < 0) {
                    Toast.makeText(getContext(),getString(R.string.customBeepTypeRange),Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(customBeepFrequency.getText().toString()) > 5200||Integer.parseInt(customBeepFrequency.getText().toString()) < 100) {
                    Toast.makeText(getContext(),getString(R.string.customBeepFrequencyRange),Toast.LENGTH_SHORT).show();
                }else {
                    WriteData(ScannerUtil.CustomBeepTime(Integer.parseInt(customBeepTime.getText().toString()),Integer.parseInt(customBeepType.getText().toString()),Integer.parseInt(customBeepFrequency.getText().toString())));
                }
                break;
            case R.id.EnableTimeStamp:
                WriteData(ScannerUtil.ConvertByte(Scanner.EnableTimeStamp));
                break;
            case R.id.DisableTimeStamp:
                WriteData(ScannerUtil.ConvertByte(Scanner.DisableTimeStamp));
                break;
            case R.id.TimeStampSetFormat:
                Date date =new Date(System.currentTimeMillis());//获取当前时间
                WriteData(ScannerUtil.SetTimeStamp(date));
                break;
        }
    }

    private void WriteData(byte[] data){
        String name=spinner.getSelectedItem().toString();
        List<BleDevice> devices= BLEManager.getInstance().getAllConnectedDevice();
        if(devices.size()==0)
        {
            Toast.makeText(getContext(),getString(R.string.unconnectedScanner),Toast.LENGTH_SHORT).show();
        }
        else if(name=="")
        {
            Toast.makeText(getContext(),getString(R.string.selectScanner),Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BleDevice device:devices)
            {
                String path=device.getName()+"-"+device.getMac();
                if(name == "All")
                {
                    BLEManager.getInstance().ScannerCommand(device, data,new BleWriteCallback() {

                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {

                        }
                    });
                }
                else if(name.equals(path))
                {
                    BLEManager.getInstance().ScannerCommand(device, data,new BleWriteCallback() {

                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {

                        }
                    });
                }
            }
        }
    }

    private void initView(View v) {
        triggerSecond=v.findViewById(R.id.triggerSecond);
        softTrigger=v.findViewById(R.id.softTrigger);
        customBeepLevel=v.findViewById(R.id.customBeepLevel);
        customBeep=v.findViewById(R.id.customBeep);
        customBeepTime=v.findViewById(R.id.customBeepTime);
        customBeepType=v.findViewById(R.id.customBeepType);
        customBeepFrequency=v.findViewById(R.id.customBeepFrequency);
        customBeepSend=v.findViewById(R.id.customBeepSend);
        enableTimeStamp=v.findViewById(R.id.EnableTimeStamp);
        disableTimeStamp=v.findViewById(R.id.DisableTimeStamp);
        timeStampSetFormat=v.findViewById(R.id.TimeStampSetFormat);
        txt_count = (TextView) v.findViewById(R.id.txt_count);
        mResultAdapter = new MessageFragment.ResultAdapter(getActivity());
        ListView listView_message = (ListView) v.findViewById(R.id.list_message);
        listView_message.setAdapter(mResultAdapter);
        listView_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = mResultAdapter.getItem(position);
            }
        });
        softTrigger.setOnClickListener(this);
        customBeep.setOnClickListener(this);
        customBeepSend.setOnClickListener(this);
        enableTimeStamp.setOnClickListener(this);
        disableTimeStamp.setOnClickListener(this);
        timeStampSetFormat.setOnClickListener(this);
    }

    public void iniData() {
        receivedCount=0;
        txt_count.setText(String.valueOf(getActivity().getString(R.string.debugCount) + receivedCount));
        mResultAdapter.clear();
        mResultAdapter.notifyDataSetChanged();
    }

    public void addMessage(String address,String content){
        receivedCount++;
        txt_count.setText(String.valueOf(getActivity().getString(R.string.debugCount) + receivedCount));
        Message message=new Message(address,content);
        mResultAdapter.addResult(message);
        mResultAdapter.notifyDataSetChanged();
    }


    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private final List<Message> messages;

        ResultAdapter(Context context) {
            this.context = context;
            messages = new ArrayList<>();
        }

        void addResult(Message message) {
            messages.add(0,message);
        }

        void clear() {
            messages.clear();
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Message getItem(int position) {
            if (position > messages.size())
                return null;
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageFragment.ResultAdapter.ViewHolder holder;
            if (convertView != null) {
                holder = (MessageFragment.ResultAdapter.ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_message, null);
                holder = new MessageFragment.ResultAdapter.ViewHolder();
                convertView.setTag(holder);
                holder.txt_date = (TextView) convertView.findViewById(R.id.txt_date);
                holder.txt_host = (TextView) convertView.findViewById(R.id.txt_host);
                holder.txt_message = (TextView) convertView.findViewById(R.id.txt_message);
            }

            Message message = messages.get(position);

            holder.txt_date.setText(message.getDate());
            holder.txt_host.setText(message.getHost());
            holder.txt_message.setText(message.getMessage());
            return convertView;
        }

        class ViewHolder {
            TextView txt_date;
            TextView txt_host;
            TextView txt_message;
        }
    }

    private void initSpinner(View v) {
        List<BleDevice> devices= BLEManager.getInstance().getAllConnectedDevice();
        if(devices.size()>0)
        {
            starArray.add("All");
            for (BleDevice device:devices)
            {
                starArray.add(device.getName()+"-"+device.getMac());
            }
        }
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_select, starArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        spinner = v.findViewById(R.id.spinner);
        //设置下拉框的标题，不设置就没有难看的标题了
        spinner.setPrompt("请选择扫描枪");
        //设置下拉框的数组适配器
        spinner.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        spinner.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        spinner.setOnItemSelectedListener(new MySelectedListener());
    }

    private class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //Toast.makeText(TestActivity.this, "您选择的是：" + starArray[i], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
