package visao;

import java.awt.Font;
import java.awt.Toolkit;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import controle.Controle;
import modelo.Cliente;

import javax.swing.SwingConstants;
import java.awt.Color;

public class IGCliente extends JFrame {
	private JPanel painelConteudo, painelBotao;
	private JTextField campoTexto;
	private JTextPane painelTexto;
	private JButton btnEnviar, btnArquivo;
	private boolean encerrar;
	private JFileChooser exploradorArquivos;
	private JScrollPane painelRolagem;
	private String mensagemCampoTexto;
	
	public IGCliente(Cliente cliente) { // Construtor
		painelConteudo = new JPanel();
		painelConteudo.setLayout(null);
				
		encerrar = false;
		mensagemCampoTexto = "Digite uma mensagem aqui...";
		
		this.setSize(400, 559); 
		this.setTitle("Atividades Pr\u00E1ticas Supervisionadas");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
		this.setResizable(false);
		this.setContentPane(painelConteudo);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(IGCliente.class.getResource("/imagens/favicon.png")));
		
		painelTexto = new JTextPane();
		painelTexto.setEditable(false);
		painelTexto.setText("");
		painelTexto.setFont(new Font("Arial", Font.PLAIN, 12));
		
		painelRolagem = new JScrollPane();
		painelRolagem.setBounds(10, 11, 374, 388);
		painelConteudo.add(painelRolagem);
		painelRolagem.setViewportView(painelTexto);
		
		campoTexto = new JTextField();
		campoTexto.setForeground(Color.LIGHT_GRAY);
		campoTexto.setHorizontalAlignment(SwingConstants.LEFT);
		campoTexto.setText(mensagemCampoTexto);
		campoTexto.setFont(new Font("Arial", Font.PLAIN, 12));
		campoTexto.setBounds(10, 410, 374, 41);
		painelConteudo.add(campoTexto);
		campoTexto.setColumns(10);
		
		painelBotao = new JPanel();
		painelBotao.setBounds(10, 462, 374, 57);
		painelConteudo.add(painelBotao);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setFont(new Font("Arial", Font.PLAIN, 12));
		btnEnviar.setActionCommand("BTN_ENVIAR");
		painelBotao.add(btnEnviar);		
		
		btnArquivo = new JButton("Arquivo");
		btnArquivo.setFont(new Font("Arial", Font.PLAIN, 12));
		btnArquivo.setActionCommand("BTN_ARQUIVO");
		painelBotao.add(btnArquivo);
		
		exploradorArquivos = new JFileChooser();
		exploradorArquivos.setCurrentDirectory(new File(System.getProperty("user.home")));
	}
	
	public void escrever(String mensagem) { // Concatena (append) o conteúdo do painel de texto com a mensagem recebida
		painelTexto.setText(painelTexto.getText() + "\n " + mensagem);
	}
	
	public void limparCampoTexto() {
		campoTexto.setText("");
	}
	
	public String obterConteudoCampoTexto() {
		return campoTexto.getText();
	}

	public String caminhoArquivo() {
		if(exploradorArquivos.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File arquivo = exploradorArquivos.getSelectedFile();
			return arquivo.getPath();
		}
		
		return null;
	}

	public JTextPane getPainelTexto() {
		return painelTexto;
	}

	public JButton getBtnEnviar() {
		return btnEnviar;
	}

	public boolean isEncerrar() {
		return encerrar;
	}
	
	public void perdeuFoco() {
		if(campoTexto.getText().length() == 0) {
			campoTexto.setForeground(Color.LIGHT_GRAY);
			campoTexto.setText(mensagemCampoTexto);
			campoTexto.setFocusable(false);
			campoTexto.setFocusable(true);
		}
	}
	
	public void ganhouFoco() {
		if(campoTexto.getText().length() != 0) {
			campoTexto.setText("");
			campoTexto.setForeground(Color.BLACK);
		}
	}
	
	public String getMensagemCampoTexto() {
		return mensagemCampoTexto;
	}

	public JTextField getCampoTexto() {
		return campoTexto;
	}

	public void setActionListeners(Controle controle) {
		this.addWindowListener(controle);
		campoTexto.addKeyListener(controle);
		campoTexto.addFocusListener(controle);
		btnEnviar.addActionListener(controle);
		btnArquivo.addActionListener(controle);
	}
}
