package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Atendente implements Runnable {
	
	private Socket socket;
	private BufferedReader recebe;
	private PrintStream ENVIA;
	private boolean inicializado;
	private boolean executando;
	private Thread thread;
	
	
	public Atendente(Socket socket) throws Exception {
		this.socket = socket;
		this.inicializado = false;
		this.executando   = false;
		open();
	}
	
	private void open() throws Exception{
		try{
			recebe = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ENVIA  = new PrintStream(socket.getOutputStream());
		    
		    inicializado = true;
		}
		catch (Exception e){
			close();
			throw e;
		}
	}
	private void close(){
		if(recebe != null){
			try{
				recebe.close();
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
		
		if(ENVIA != null){
			try{
				ENVIA.close();
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
		
		try{
			socket.close();
		}
		catch (Exception e){
			System.out.println(e);
		}
		
	    ENVIA  = null;
		recebe = null;
		socket = null;
		
		inicializado = false;
		executando   = false;
		
		thread = null;
	}
	
	public void start(){
		if(!inicializado || executando){
			return;
		}
		
		executando = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run(){
		try{
			socket.setSoTimeout(2500);
			String opCod = recebe.readLine();
			
			if("999".equals(opCod)){
				enviaCandidatos();
			}
			else
			    gravaCandidatos(opCod);
		}
		catch (SocketTimeoutException e){
			//igonorar
		}
		catch (Exception e){
			System.out.println(e);
		}
		System.out.println("Encerrando Conexão.");	
		close();  
	}
	
	public void enviaCandidatos(){
		
		//le um arquivo JSON que contem as informções sobre candidatos
		BufferedReader leArquivo = null;
		try {
			leArquivo = new BufferedReader(new FileReader("Candidatos.json"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			//Envia um arquivo json contendo os dados dos candidatos para o cliente 
			ENVIA.println(leArquivo.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Grava os dados de cadidato mais votos atualizados no arquivo
	public void gravaCandidatos(String jsonCandidato){
		
		FileWriter escreve;
		try {
			escreve = new FileWriter("Candidatos.json");
			escreve.write(jsonCandidato);
			escreve.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
