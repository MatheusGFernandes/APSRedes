package modelo;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.sound.midi.SysexMessage;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import visao.IGCliente;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Cliente implements Runnable {
	private static SocketCliente cliente;
	private PrintWriter saida;
	private IGCliente interfaceCliente;
	public static boolean liberar;	
	// O endereço e a porta são fornecidos pelo ngrok
	private String endereco;
	private int porta;   
	
    public void iniciar() {
    	liberar = false;
		
    	try {
    		File arquivoEncaminhamento = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator + "arquivoxxxxs"); // \bin\arquivos
    		
        	if(arquivoEncaminhamento.exists()) { // Se o pacote "arquivos" existe, i.e., se o cliente e servidor estão na mesma máquina        		
        		endereco = Conexao.getURLPortaEncaminhamento((byte) 0); // Retorna a URL
        		porta = Integer.parseInt(Conexao.getURLPortaEncaminhamento((byte) 1)); // Se o servidor estiver fechado, NumberFormatException será lançado
        		criarCliente();
        	}
        	else { // Solicitar ao usuário que digite o URL e a porta
        		coletarURLPorta();
        		criarCliente();
        	}   
        	
    		liberar = !liberar;
    		
    		new Thread(this).start();
    	}
    	catch(NumberFormatException excecao) {
    		JOptionPane.showMessageDialog(null, "O servidor está fechado.\nTente novamente mais tarde.", "Servidor fechado", JOptionPane.ERROR_MESSAGE);  
    		System.exit(0);
    	}
    	catch(UnknownHostException excecao) {
    		JOptionPane.showMessageDialog(null, "Hospedeiro desconhecido.", "Erro", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	}
    	catch(IllegalArgumentException excecao) {
    		excecao.printStackTrace();
    	}
    	catch(SecurityException excecao) {
    		excecao.printStackTrace();
    	}
    	catch(IOException excecao) {
    		excecao.printStackTrace();
    	}	
    }
    
    public void criarCliente() throws IOException {
		cliente = new SocketCliente(new Socket(endereco, porta)); // Tentativa de se conectar ao servidor
		JOptionPane.showMessageDialog(null, "Conectado no servidor.\nSeja bem-vindo(a)!", "Saudações", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void coletarURLPorta() {
		JPanel painelConteudo, lblRotulos, controles;
		JTextField campoURL, campoPorta;
		
        painelConteudo = new JPanel(new BorderLayout(5, 5));

        lblRotulos = new JPanel(new GridLayout(0, 1, 2, 2));
        lblRotulos.add(new JLabel("URL: ", SwingConstants.TRAILING));
        lblRotulos.add(new JLabel("Porta: ", SwingConstants.TRAILING));
        painelConteudo.add(lblRotulos, BorderLayout.LINE_START);

        controles = new JPanel(new GridLayout(0,1,2,2));
        campoURL = new JTextField();
        controles.add(campoURL);
        campoPorta = new JTextField();
        controles.add(campoPorta);
        painelConteudo.add(controles, BorderLayout.CENTER);
       
        if( JOptionPane.showConfirmDialog(null, painelConteudo, "Conectar-se ao servidor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
        	System.exit(0);
        }
        
        endereco = campoURL.getText();

        try {
        	porta = Integer.parseInt(campoPorta.getText());
        }
    	catch(NumberFormatException excecao) {
    		JOptionPane.showMessageDialog(null, "O valor fornecido para a porta é inválido!", "Porta inválida", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	}
    }

	@Override
	public void run() { // Lê o conteúdo enviado pelo servidor e envia para o painel de texto
		String mensagem;

		while((mensagem = cliente.obterMensagem()) != null) {			
			interfaceCliente.escrever(mensagem);
		}
	}
	
	public void sendMessage(String message) {
		cliente.enviarMensagem(message);
	}
	
	public void finalizar() {
		cliente.encerrar();
	}
	
	public void setInterfaceCliente(IGCliente interfaceCliente) {
		this.interfaceCliente = interfaceCliente;
	}																																									
}