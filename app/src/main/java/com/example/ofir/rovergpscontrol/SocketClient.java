package com.example.ofir.rovergpscontrol;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by ofir on 20-Jul-18.
 */

public class SocketClient extends Thread
{
    Socket socket;
    boolean ready = false;
    private String ip;

    SocketClient(String ip)
    {
        this.ip = ip;
    }

    @Override
    public void run()
    {
        try
        {
            InetAddress inet = InetAddress.getByName(ip);
            socket = new Socket(inet, 9152);
            ready = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
