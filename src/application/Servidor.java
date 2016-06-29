package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Servidor implements Runnable{

	private ServerSocket server; 
	private boolean inicializado;
	private boolean executando;
	private Thread thread;
	private static int port = 40006;

	public Servidor (int porta){

		//atende  = new ArrayList<Atende>();
		inicializado = false;
		executando   = false;

		open(porta);
	}

	public void open(int porta){
		try {
			server = new ServerSocket(porta);
		} catch (IOException e) {
			e.printStackTrace();
		}
		inicializado = true;
	}

	public void start (){
		if(!inicializado || executando){
			return;
		}
		executando = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() throws Exception{
		executando = false;
		if(thread != null){
			thread.join();
		}

	}

	private void close(){

		try{
			server.close();
		}
		catch (Exception e){
			System.out.println(e);
		}

		inicializado = false;
		executando   = false;
		thread       = null;
	}

	public void run(){
		System.out.println("Aguardando Conexão.");

		while(executando){
			try{
				server.setSoTimeout(2500);
				Socket socket = server.accept();

				System.out.println("Conexão estabelecida.");

				Atendente atentente = new Atendente(socket);
				atentente.start();

			}
			catch (SocketTimeoutException e){
				//igonora
			}
			catch (Exception e){
				System.out.println(e);
				break;
			}
		}
		close();
	}

	public static void main(String[] args) throws Exception {

		System.out.println("Iniciando Servidor");
		Servidor servidor = new Servidor(port);

		servidor.start();

		System.out.println("Pressione ENTER para encerrrar o servidor.");		 
		new Scanner(System.in).nextLine();

		System.out.println("Encerrando Servidor.");
		servidor.stop();
	}
}