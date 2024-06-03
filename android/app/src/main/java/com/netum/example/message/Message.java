package com.netum.example.message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private Date date;
    private String host;
    private String message;

    public Message(String address,String content)
    {
        date=new Date();
        host=address;
        message=content;
    }

    public String getDate()
    {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        return ft.format(date);
    }

    public String getHost() {
        return host;
    }

    public String getMessage()
    {
        return message;
    }



}
