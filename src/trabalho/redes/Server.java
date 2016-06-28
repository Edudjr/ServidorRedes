package trabalho.redes;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
	
public class Server implements Runnable {
	
	private ServerSocket server; 
	private List<Atendente> atendentes;
	private boolean inicializado;
	private boolean executando;
	private Thread thread;
	
	//M�todo construtor para inicializar o novo objeto Servidor.
	public Server(int porta) throws Exception {
		
		atendentes = new ArrayList<Atendente>();
		inicializado = false; // Servidor ainda nao foi inicializado
		executando = false;   // Servidor ainda nao em execu��o
		
		open(porta); //M�todo privado open() ser� respons�vel pela inicializa��o do servidor
	}
	
	/*O metodo privado open() deve criar o objeto ServerSocket e 
	marcar o objeto Servidor como inializado.*/
	private void open(int porta) throws Exception{
		
		server = new ServerSocket(porta);
		inicializado = true;
	}
	
	//O METODO PRIVADO CLOSE() TEM A RESPONSABILIDADE DE LIBERAR OS RECURSOS ALOCADOS PELO SERVIDOR.
    /*As exce��es que podem ocorrer durante o fechamento de recursos s�o, de certa forma, secund�rias. 
	Em outras palavras, n�o s�o fatais para a aplica��o. Uma boa pr�tica e grav�-las em uma log da aplica��o para que a equipe de suporte possa investigar. 
	Se os recursos continuarem a ficar presos, com o tempo, a aplica��o ficar� sem recursos, o que ser� fatal.*/
	private void close(){
		
		for(Atendente atendente : atendentes){
			try{
				atendente.stop();
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
		try{
			server.close();
		}
		catch (Exception e){
			System.out.println(e);
		}
		
		inicializado = false;
		executando = false;
		thread = null;
	}
	
	/*Precisaremos de um m�todo p�blico para iniciar a execu��o do servidor. 
	Em outras palavras, para iniciar a thread auxiliar do servidor.*/
	public void start(){
		if(!inicializado ||  executando){ //Caso o objeto Servidor j� tenha sido inicializado ou esteja em execu��o, ent�o simplesmente retornamos.
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
	
	/*A� est� o m�todo run() que ser� executado pela nova thread. 
	Somos obrigados a implement�-lo para garantir a realiza��o do 
	contrato definido pela interface Runnable.*/
	@Override
	public void run() {
		System.out.println("Aguardando Conex�o.");
		
		while(executando){
			try{
				server.setSoTimeout(2500);
				Socket socket = server.accept();
				
				System.out.println("Conex�o estabelecida.");
				
				Atendente atentente = new Atendente(socket);
				atentente.start();
				
				atendentes.add(atentente);
				
			}
			catch (SocketTimeoutException e){
				//igonora
				
				System.out.println("Tempo");
			}
			catch (Exception e){
				System.out.println(e);
				break;
			}
		}
		close();
	}
	
	public static void main(String[] args)throws Exception {  
		
		System.out.println("Iniciando Servidor.");
		
		Server servidor = new Server(2525);
		
		servidor.start();
		
		System.out.println("Pressione ENTER para encerrrar o servidor.");		 
		new Scanner(System.in).nextLine();
		 
		 System.out.println("Encerrando Servidor.");
		 servidor.stop();
	}
}
