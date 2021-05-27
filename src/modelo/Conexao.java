package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.JSONObject;

public class Conexao {
	private String caminhoProjeto, caminhoNgrok, caminhoJSON, caminhoEncaminhamento, caminhoConfiguracao, URL;
	private int porta, portaServidor;
	private static File arquivoJSON = null, arquivoEncaminhamento = null;
	public static final char CARRIAGE_RETURN = 13;
	private static final String PAGINA_DE_CODIGO = "CP850"; // COodificação utilizada no prompt de comando
	private static final int TENTATIVAS = 20;
	
	public Conexao(int portaServidor) {
		this.portaServidor = portaServidor;
	}
	
	public void abrirTunel() { // Cria um túnel entre a porta que o servidor está ouvindo e uma porta arbitrária
		inicializarArquivos();
		taskKill(); // Caso o ngrok.exe já esteja aberto, ele será fechado e uma nova sessão será iniciada

		tentativaInicializar(String.format("cmd /c cd %s & ngrok tcp %d --log=\"%s\" --config=\"%s\"", caminhoNgrok, portaServidor, caminhoJSON, caminhoConfiguracao));

		extrairEncaminhamento();
		registrarEncaminhamento();
	}
	
	private void tentativaInicializar(String instrucoes) {
		String linha;
		StringBuilder saidaConsole = new StringBuilder();
		int j = 0;
		BufferedReader leitor = null;
		Process processo;
		boolean terminar = true;
		
		try {
			System.out.println("Inicializando...\n\n" + instrucoes + "\n");	
			
			while(j < TENTATIVAS) {
				System.out.println((j + 1) + "ª tentativa de construir o túnel TCP...");
				
				Runtime.getRuntime().exec(instrucoes); // Inicia o ngrok, cria o túnel TCP e envia o log para arquivo.json
				
				Thread.sleep(5000);
				
				processo = Runtime.getRuntime().exec("tasklist"); 
				leitor = new BufferedReader(new InputStreamReader(processo.getInputStream(), PAGINA_DE_CODIGO));
				   
			    while((linha = leitor.readLine()) != null) {		    	
			    	saidaConsole.append(linha);
			    }
			    
			    if(saidaConsole.toString().contains("ngrok.exe")) {
			    	System.out.println("Sucesso!");
			    	terminar = false;
			    	break;
			    }
			    else {
			    	System.out.println("Falha.");
			    	j++;
			    }
			}
			
		    leitor.close();
		    
		    if(terminar) {
		    	System.out.println("Ocorreu um erro ao iniciar o servidor!");
		    	System.exit(-1);
		    }
		}
		catch(IOException excecao) {
			excecao.printStackTrace();
		}
		catch(InterruptedException excecao) {
			excecao.printStackTrace();
		} 
	}
	
	private void inicializarArquivos() {
		caminhoProjeto = System.getProperty("user.dir"); // Obtém o caminho absoluto para a pasta do projeto
		caminhoNgrok = caminhoProjeto + "\\bin\\executaveis"; // Obtém o caminho absoluto do ngrok.exe
		caminhoJSON = caminhoProjeto + "\\bin\\arquivos\\registro da sessão.json"; // Obtém o caminho absoluto do registro da sessão.json			
		caminhoEncaminhamento = caminhoProjeto  + "\\bin\\arquivos\\encaminhamento.txt"; // Obtém o caminho absoluto do encaminhamento.txt
		caminhoConfiguracao = caminhoProjeto + "\\bin\\arquivos\\configuracao_ngrok.yml";
				
		arquivoEncaminhamento = new File(caminhoEncaminhamento);
		arquivoJSON = new File(caminhoJSON);
	}
	
	private void extrairEncaminhamento() { // Método para extrair a URL e o número da porta	
		String objetoLido;
		int i;
		StringBuilder portaString, URLLimpa;
		portaString = new StringBuilder();
		URLLimpa = new StringBuilder();
		char caractere;
		boolean extrairPorta = true;
		
		try {		
			Scanner leitura = new Scanner(arquivoJSON);
				
			// ..."url":"tcp://4.tcp.ngrok.io:18808"}...
			// Faz a leitura do arquivo .json:
			while(leitura.hasNextLine()) {
				objetoLido = leitura.nextLine();
				
				if(objetoLido.contains("url")) { // Ao encontrar o objeto que contém a chave url
					JSONObject objetoJSON = new JSONObject(objetoLido);
					URL = objetoJSON.getString("url"); // Transforma a string em um objeto .json e retorna o valor da chave especificada
					break;
				}
			}

			leitura.close();
		}
		catch(FileNotFoundException excecao) {
			excecao.printStackTrace();
		}
		
		i = URL.length() - 1; // Obtém o índice do último caractere da string	
		
		while(i >= 0) {
			caractere = URL.charAt(i); // Retorna o caractere da posição i
			
			if(extrairPorta) { // Procedimento para a extração do número da porta
				if(Character.isDigit(caractere)) { // Se o caractere é um dígito
					portaString.append(caractere); // Adiciona na string
				}
				else {
					extrairPorta = !extrairPorta; // Isso faz com que o caractere ":" se perca
				}
			}
			else {
				if(caractere != '/') { // Enquanto não alcançar o caractere barra
					URLLimpa.append(caractere); // Adiciona o caractere na string
				}
				else {
					break;
				}
			}
			
			i--;
		}
		
		URL = URLLimpa.reverse().toString(); // Inverte a string
		porta = Integer.parseInt(portaString.reverse().toString()); // Inverte a string da porta e converte para int				
	}
	
	private void registrarEncaminhamento() {		
		String saida = URL + "\n" + porta;
		/*
			4.tcp.ngrok.io
			18808
		*/
		
		FileWriter arquivoEncaminhamento;
		
		try {
			arquivoEncaminhamento = new FileWriter(caminhoEncaminhamento);
			arquivoEncaminhamento.append(saida);
			arquivoEncaminhamento.close(); 
		}
		catch(IOException excecao) {
			excecao.printStackTrace();
		}
	}
	
	public void taskKill() { // Destrói o túnel TCP, mata o ngrok.exe e limpa os arquivos
		try {
			Runtime.getRuntime().exec("taskkill /im \"ngrok.exe\" /f");
			limparArquivos();
		}
		catch(IOException excecao) {
			excecao.printStackTrace();
		}	
	}
	
	private void limparArquivos() {
		try {
			PrintWriter apagar = new PrintWriter(arquivoJSON);
			apagar.write("");
			apagar.close();
			apagar = new PrintWriter(arquivoEncaminhamento);
			apagar.write("");
			apagar.close();	
		}
		catch(FileNotFoundException excecao) {
			excecao.printStackTrace();
		}
	}
	
	public static String getURLPortaEncaminhamento(byte opcao) {
		// opcao:
		// 0 = retornar URL
		// 1 = retornar porta
		String conteudoLido;
		arquivoEncaminhamento = new File(System.getProperty("user.dir") + "\\bin\\arquivos\\encaminhamento.txt");
		Scanner leitura = null;
		int j = 0;
		
		try {
			leitura = new Scanner(arquivoEncaminhamento);
			
			while(leitura.hasNextLine()) { 
				conteudoLido = leitura.nextLine(); 			
				
				if(conteudoLido == null) {
					leitura.close();
					return null;
				}
				
				if((j == 0) && (opcao == 0)) { // A URL é sempre escrita primeiro
					leitura.close();
					return conteudoLido;
				}
				else if ((j == 1) && (opcao == 1)) {
					leitura.close();
					return conteudoLido;
				}
				
				j++;
			}
		}
		catch(FileNotFoundException excecao) {
			excecao.printStackTrace();
		}

		return null;
	}
	
	public String getURL() {
		return URL;
	}
	
	public int getPorta() {
		return porta;
	}
	
	@Override
	public String toString() {
		return String.format("[tcp://%s:%d]", URL, porta);
	}
}