package controle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import modelo.Cliente;
import principal.PrincipalCliente;
import visao.IGCliente;

public class Controle implements ActionListener, KeyListener, FocusListener, WindowListener {
	private IGCliente interfaceCliente;
	private Cliente cliente; 
	public static final String INSTRUCAO = "</ARQUIVO>";

	public Controle(IGCliente interfaceCliente, Cliente cliente) {
		this.interfaceCliente = interfaceCliente;  
		this.cliente = cliente;
	}
	
	public void iniciarAplicacao() {	
		cliente.iniciar();
		while(!Cliente.liberar);
		interfaceCliente.setVisible(true);
	}
	
	private void enviarMensagem() {
		String mensagem = interfaceCliente.obterConteudoCampoTexto();
		
		if(!mensagem.isEmpty()) {
			cliente.sendMessage(mensagem);
			interfaceCliente.limparCampoTexto();	
		}
	}
	
	// ActionListener:
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		String componente = actionEvent.getActionCommand();
		
		if(componente.equals("BTN_ENVIAR")) {
			enviarMensagem();
			
		}
		else if(componente.equals("BTN_ARQUIVO")) {
			String caminho;
			
			if((caminho = interfaceCliente.caminhoArquivo()) != null) {
//				try {
//					//SocketCliente.enviarArquivo(caminho, cliente.getCliente().getOutputStream(), cliente.getCliente().getInputStream());
//					System.out.println(caminho);
//				}
////				catch(IOException excecao) {
//					excecao.printStackTrace();
//				}
				System.out.println(caminho);
			}
		}
	}

	// KeyListener:
	@Override
	public void keyPressed(KeyEvent teclaPressionada) {
		if(teclaPressionada.getKeyChar() == '\n') { // Se o usuário pressionou a tecla Enter
			enviarMensagem();
			interfaceCliente.perdeuFoco();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	// FocusListener
	@Override
	public void focusGained(FocusEvent arg0) {
		interfaceCliente.ganhouFoco();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		interfaceCliente.perdeuFoco();
	}

	// WindowListener
	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(JOptionPane.showConfirmDialog(null, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			//cliente.finalizar();
			System.exit(0);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	
	}
}
