package com.netum.device.instruction;

import android.util.Log;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ScannerUtil {

    /**
     * 将字符串命令转换Byte数组命令
     * @param cmd
     * @return
     */
    public static byte[] ConvertByte(String cmd){
        byte[] values=cmd.getBytes();
        List<Byte> data = new ArrayList();
        for (byte value:values) {
            data.add(value);
        }
        return toPackage(data);
    }

    /**
     * Software triggered scanning
     * @param second
     * Range 1-7 seconds
     * @return
     * Command byte array
     */
    public static byte[] SoftTrigger(int second){
        if(second<1)
            second=1;
        if(second>7)
            second=7;
        String cmd= MessageFormat.format(Scanner.SoftTrigger,second);
        byte[] values=cmd.getBytes();
        List<Byte> data = new ArrayList();
        for (byte value:values) {
            data.add(value);
        }
        return toPackage(data);
    }

    /**
     * 设定扫描枪时间戳格式2
     * @param date
     * 设定时间
     * @return
     */
    public static byte[] SetTimeStamp(Date date){
        TimeZone timeZone = TimeZone.getDefault();
        long offset=timeZone.getRawOffset();
        String format=String.valueOf((date.getTime() +offset+ 1000)/1000);
        String command= MessageFormat.format(Scanner.TimeStampSetFormat2,format);
        byte[] values=command.getBytes();
        List<Byte> data = new ArrayList();
        for (byte value:values) {
            data.add(value);
        }
        return toPackage(data);
    }

    /**
     * Custom Beep
     * @param level
     * Range 0-26
     * @return
     */
    public static byte[] CustomBeep(int level){
        byte[] values=Scanner.BeepCustomOpt.getBytes();
        List<Byte> data = new ArrayList();
        for (byte value:values) {
            data.add(value);
        }
        data.add((byte)(level+0x30));
        return toPackage(data);
    }


    /**
     * Custom Beep Time
     * @param time Range 10-2540 ms
     * @param type Range 0-2
     *             0:beep+vibration
     *             1:only beep
     *             2:only vibration
     * @param frequency Range 100-5200 Hz
     * @return
     */
    public static byte[] CustomBeepTime(int time,int type,int frequency){
        if(time<10)
            time=10;
        if(time>2540)
            time=2540;
        if(type<0)
            type=0;
        if(type>2)
            type=2;
        if(frequency<100)
            frequency=100;
        if(frequency>5200)
            frequency=5200;
        int temp=(time+10)/10;
        String s1=Integer.toHexString(temp);
        String s2=Integer.toString(type);
        temp=(frequency-100)/20;
        String s3=Integer.toHexString(temp);
        String command= MessageFormat.format(Scanner.BeepCustomTime,s1,s2,s3);
        byte[] values=command.getBytes();
        List<Byte> data = new ArrayList();
        for (byte value:values) {
            data.add(value);
        }
        return toPackage(data);
    }

    private static byte[] toPackage(List<Byte> data){
        ArrayList<Byte> values = new ArrayList();
        values.add((byte) 0x02);
        values.add((byte) (data.size() + 4));
        values.add((byte) 0x0A);
        values.addAll(data);
        values.addAll(checkSum(data));
        values.add((byte) 0x03);
        return CommonUtil.toPrimitives(values.toArray(new Byte[values.size()]));
    }

    private static List<Byte> checkSum(List<Byte> data){
        ArrayList<Byte> values = new ArrayList();
        int sum = 0;
        int bt_length = data.size() + 4;
        int size = data.size() + 2;
        for (int s = 0; s < size; s++) {
            switch (s) {
                case 0:
                    sum = (sum + (bt_length * (size - s)));
                    break;
                case 1:
                    sum =(sum + ((byte) 0x0A * (size - s)));
                    break;
                default:
                    int k = data.get(s - 2) * (size - s);
                    sum = sum + k;
                    break;
            }
        }
        int crc = new BigInteger("10000", 16).intValue() - (sum & 0xffff);

        values.add((byte) ((crc & 0xFF00) >> 8));
        values.add((byte) (crc & 0xff));
        return values;
    }
}
