package modelo;

import java.util.Scanner;

public class ThreadServidor implements Runnable {
	private Scanner entradaPadrao; 
	private Servidor servidor;
	
	public ThreadServidor(Servidor servidor) {
		entradaPadrao = new Scanner(System.in);
		this.servidor = servidor;
	}
	
	@Override
	public void run() {
		while(!(entradaPadrao.nextLine()).equals("</FINALIZAR>"));
		System.out.println("Servidor finalizado.");
		servidor.finalizar();
	}
}
