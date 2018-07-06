package com.leotech.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.leo.api.socket.SimpleCommand;
import com.leo.api.socket.SocketThread;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.os.HandlerThread;

public class SerialPrinter extends HandlerThread {

	
    private static SerialPrinter sInstance;
    private static Handler sHandler;


	private InputStream mInputStream;
	private OutputStream mOutputStream;
	
    private SerialPrinter(String comm) {
        super("SerialPrinter", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		
		setupComm(comm);
    }


	private void setupComm(String comm) {
		
		try {
			//LocalSocket socket = new LocalSocket();
			//socket.connect(new LocalSocketAddress(comm,	LocalSocketAddress.Namespace.RESERVED));
			
			//mInputStream = socket.getInputStream();
			
			//mOutputStream = socket.getOutputStream();
			mOutputStream=SimpleCommand.getOutputStream(1);
			mInputStream=SimpleCommand.getInputStream(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new SerialPrinter("ttysocket1");
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
        }
    }

    public static SerialPrinter getInstance() {
        synchronized (SerialPrinter.class) {
            ensureThreadLocked();
            return sInstance;
        }
    }


	
	public final boolean schedule(PrinterTask task){

		return sHandler.post(task);
	}


	final void write(byte[] dataArray) throws IllegalAccessException{

		if( Thread.currentThread().getId() 
			!= SerialPrinter.getInstance().getId())
			throw new IllegalAccessException();
		
		try {
			mOutputStream.write(SocketThread.form_print_pack(dataArray));
			
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	


	static public abstract class PrinterTask implements Runnable {


		protected final void send(byte data){
			send(new byte[]{data});
		}

		protected final void send(byte[] dataArray){
			try {
				
				SerialPrinter.getInstance().write(dataArray);	
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
		}
	}

	
}

