package sockets;


import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class servidor  {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoServidor mimarco=new MarcoServidor();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}	
}

class MarcoServidor extends JFrame implements Runnable {
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);
		
		Thread  mihilo= new Thread(this);
		
		mihilo.start();
	}
	
	private	JTextArea areatexto;

	@Override
	public void run() {
		//-----
		try {
			ServerSocket servidor = new ServerSocket(999);
			String nick,ip,mensaje;
			ArrayList <String> combo_ip= new ArrayList<String>();
			paqueteEnvio recibo;
			
			
			while(true){//bucle para que no se cierre la conexion
			Socket mio= servidor.accept();
			
			
			ObjectInputStream pack_recibo= new ObjectInputStream(mio.getInputStream());
			
			recibo=(paqueteEnvio) pack_recibo.readObject();			
			nick=recibo.getNick();
			ip=recibo.getIp();
			mensaje=recibo.getMensaje();
			
			if(!mensaje.equals("Online")){
			areatexto.append("\n"+nick+": "+mensaje+ " para: "+ip);
			
			Socket enviaDestino= new Socket(ip,9090);
			ObjectOutputStream pack_reenvio = new ObjectOutputStream(enviaDestino.getOutputStream());
			
			pack_reenvio.writeObject(recibo);
			pack_reenvio.close();
			enviaDestino.close();
			mio.close();
			}
			else{
				//-------------------------detectar ip's en linea
				InetAddress lista_ip= mio.getInetAddress();
				String ip_remota=lista_ip.getHostAddress();
				//----------------
				combo_ip.add(ip_remota);
				recibo.setIps(combo_ip);
				
				for(String z:combo_ip){
					Socket enviaDestino= new Socket(z,9090);
					ObjectOutputStream pack_reenvio = new ObjectOutputStream(enviaDestino.getOutputStream());
					
					pack_reenvio.writeObject(recibo);
					pack_reenvio.close();
					enviaDestino.close();
					mio.close();
				}
				//----------------
				}
			}
			
		} catch (IOException | ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
}

