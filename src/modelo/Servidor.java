package modelo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// Convenção: uma mensagem válida é diferente de -1, de qualquer quantidade de espaços em branco e de qualquer caractere de controle (como o nulo por exemplo)

public class Servidor {
	private final static List<SocketCliente> clientes = new LinkedList<>();
	private static int porta;
	private ServerSocket servidor;
	private Conexao tunelTCP;
	
	public void iniciar() {    	 	   	   	
        try {   	
        	servidor = new ServerSocket(0);
        	porta = servidor.getLocalPort();
        	tunelTCP = new Conexao(porta);
        	tunelTCP.abrirTunel();
        	System.out.println("Servidor inicializado e ouvindo na porta " + porta + ".\nURL: " + tunelTCP.getURL() + "\nPorta: " + tunelTCP.getPorta() + "\nAguardando alguma conexão...");
        	
        	ThreadServidor leitura = new ThreadServidor(this);
        	new Thread(leitura).start();
        	
        	while(true) {
        		SocketCliente clienteSocket = new SocketCliente(servidor.accept());      
        		System.out.println("O cliente " + clienteSocket.getIP() + " se conectou.");
        		clientes.add(clienteSocket);
        		System.out.println("Total de clientes conectados: " + clientes.size());
        	
        		new Thread(() -> lerMensagens(clienteSocket)).start();     
        	}         
        }
        catch(IOException excecao) {

        }
    }
	
	public static void lerMensagens(SocketCliente clienteSocket) {
		String msg;

		try {
			while((msg = clienteSocket.obterMensagem()) != null) {				
				System.out.println("Mensagem recebida de " + clienteSocket.getIP() + ": " + msg);
				repassarMensagemClientes(clienteSocket, msg);
			}
		}
		finally {
			clienteSocket.encerrar();
		}
	}
	
	public static void repassarMensagemClientes(SocketCliente emissor, String mensagem) {
		Iterator<SocketCliente> iterador = clientes.iterator();
		
		while(iterador.hasNext()) {
			SocketCliente clienteSocket = iterador.next();
			if(!clienteSocket.enviarMensagem("\n" + Calendar.getInstance().getTime() + "\n" + emissor.getIP() + " diz: " + mensagem)) { 
				iterador.remove(); // Se o cliente se desconectou, ele é removido da lista
				System.out.println("Total de clientes conectados: " + clientes.size());
			}
		}
	}
	
	public void finalizar() {
        try {
			servidor.close();
		}
        catch(IOException excecao) {
        	excecao.printStackTrace();
        }
        finally {
        	tunelTCP.taskKill();
        	System.exit(0);
        }
	}
}