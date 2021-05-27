package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketCliente {
	private final Socket socket;
	private final BufferedReader entrada;
	private PrintWriter saida;

	public SocketCliente(Socket socket) throws IOException {
		this.saida = null;
		this.socket = socket;
		entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		saida = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public String obterMensagem() {
		try {
			return entrada.readLine();
		}
		catch(IOException excecao) {
			return null;
		}
	}
	
	public boolean enviarMensagem(String msg) {
		saida.println(msg);
		return !saida.checkError();
	}
	
	public String getIP() {
		return socket.getInetAddress().getHostAddress();
	}
	
	public void encerrar() {
		try {
			entrada.close();
			saida.close();
			socket.close();
		}
		catch(IOException excecao) {
			excecao.printStackTrace();
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
}
