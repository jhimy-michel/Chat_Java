package sockets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
public class cliente {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoCliente mimarco=new MarcoCliente();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
		
		setBounds(600,300,280,350);
				
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		
		add(milamina);
		
		setVisible(true);
		addWindowListener(new EnvioOnline());
		}	
	
}
///--------envio de señal online
class EnvioOnline extends WindowAdapter{
	
	public void windowOpened(WindowEvent e){
		try{
			
			Socket mio= new Socket("192.168.1.133",999);
			paqueteEnvio datos = new paqueteEnvio();
			datos.setMensaje("Online");
			ObjectOutputStream pack_dato= new ObjectOutputStream(mio.getOutputStream());
			pack_dato.writeObject(datos);
			mio.close();
			
			
		}catch(Exception excep){}
	}
	
}
////////----------
class LaminaMarcoCliente extends JPanel implements Runnable{
	
	public LaminaMarcoCliente(){
		String nick_usuario=JOptionPane.showInputDialog("Nick:..");
		
		
		JLabel n_nick=new JLabel("Nick: ");
		add(n_nick);
		nick= new JLabel();
		nick.setText(nick_usuario);
		add(nick);
		
		
		
		JLabel texto=new JLabel("-->Online:");
		
		add(texto);
		//---------------
		ip= new JComboBox();
		
		//-----------------------
		add(ip);
		
		campo_chat=new JTextArea(12,20);
		add(campo_chat);
		
		campo1=new JTextField(20);
		
		add(campo1);		
	
		miboton=new JButton("Enviar");
		EnviaTexto oir= new EnviaTexto();
		miboton.addActionListener(oir);
		add(miboton);	
		Thread mi_hilo= new Thread(this);
		mi_hilo.start();
	}
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			campo_chat.append("\n"+campo1.getText());
			try {
				
				Socket mensaje= new Socket("192.168.1.133",999);
				paqueteEnvio datos= new paqueteEnvio();
				
				datos.setNick(nick.getText());
				datos.setIp(ip.getSelectedItem().toString());
				datos.setMensaje(campo1.getText());
			
				ObjectOutputStream pack= new ObjectOutputStream(mensaje.getOutputStream());
				pack.writeObject(datos);
				pack.close();
				
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}	
		}
		
	}
	
		
	private JTextArea campo_chat;	
	
	private JTextField campo1;
	
	private JComboBox ip;
	
	private JLabel nick;
	
	private JButton miboton;

	
	public void run() {
		try{
			ServerSocket servidor_cliente = new ServerSocket(9090);
			Socket cliente;
			paqueteEnvio pack_recibido;
			while(true){
				cliente=servidor_cliente.accept();//aceptar las conexiones dle exterior
				ObjectInputStream flujo_entrada= new ObjectInputStream(cliente.getInputStream());
				pack_recibido=(paqueteEnvio) flujo_entrada.readObject();
				
				if(!pack_recibido.getMensaje().equals("Online")){
					campo_chat.append("\n"+pack_recibido.getNick()+": "+pack_recibido.getMensaje());
				}else{
					//-----agregar Ips activas
					
					ArrayList <String> IpsMenu= new ArrayList<String>();
					IpsMenu= pack_recibido.getIps();
					ip.removeAllItems();//borrar antes de agregar el arrayList actualizado
					for(String z:IpsMenu){
						ip.addItem(z);
					}
					
				}
			}
			
			
		}catch(Exception e){}
		
	}
	
}
class paqueteEnvio implements Serializable{
	private String nick,ip,mensaje;
	private ArrayList<String> Ips;

	public String getNick() {
		return nick;
	}

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
}