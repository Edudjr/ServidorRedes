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
	
	//Método construtor para inicializar o novo objeto Servidor.
	public Server(int porta) throws Exception {
		
		atendentes = new ArrayList<Atendente>();
		inicializado = false; // Servidor ainda nao foi inicializado
		executando = false;   // Servidor ainda nao em execução
		
		open(porta); //Método privado open() será responsável pela inicialização do servidor
	}
	
	/*O metodo privado open() deve criar o objeto ServerSocket e 
	marcar o objeto Servidor como inializado.*/
	private void open(int porta) throws Exception{
		
		server = new ServerSocket(porta);
		inicializado = true;
	}
	
	//O METODO PRIVADO CLOSE() TEM A RESPONSABILIDADE DE LIBERAR OS RECURSOS ALOCADOS PELO SERVIDOR.
    /*As exceções que podem ocorrer durante o fechamento de recursos são, de certa forma, secundárias. 
	Em outras palavras, não são fatais para a aplicação. Uma boa prática e gravá-las em uma log da aplicação para que a equipe de suporte possa investigar. 
	Se os recursos continuarem a ficar presos, com o tempo, a aplicação ficará sem recursos, o que será fatal.*/
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
	
	/*Precisaremos de um método público para iniciar a execução do servidor. 
	Em outras palavras, para iniciar a thread auxiliar do servidor.*/
	public void start(){
		if(!inicializado ||  executando){ //Caso o objeto Servidor já tenha sido inicializado ou esteja em execução, então simplesmente retornamos.
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
	
	/*Aí está o método run() que será executado pela nova thread. 
	Somos obrigados a implementá-lo para garantir a realização do 
	contrato definido pela interface Runnable.*/
	@Override
	public void run() {
		System.out.println("Aguardando Conexão.");
		
		while(executando){
			try{
				server.setSoTimeout(2500);
				Socket socket = server.accept();
				
				System.out.println("Conexão estabelecida.");
				
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
