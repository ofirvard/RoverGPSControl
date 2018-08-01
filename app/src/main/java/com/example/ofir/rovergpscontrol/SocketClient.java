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
    private int port;

    SocketClient(String ip, String port)
    {
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    @Override
    public void run()
    {
        try
        {
            InetAddress inet = InetAddress.getByName(ip);
            socket = new Socket(inet, port);
            ready = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
