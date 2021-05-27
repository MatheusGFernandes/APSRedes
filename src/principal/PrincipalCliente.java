package principal;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controle.Controle;
import modelo.Cliente;
import visao.IGCliente;

public class PrincipalCliente {
	public static void main(String[] args) {
    	try { 		
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	}
	    catch(UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException excecao) {
	    	excecao.printStackTrace();
	    }

		Cliente cliente = new Cliente();
		IGCliente interfaceCliente = new IGCliente(cliente);
		cliente.setInterfaceCliente(interfaceCliente);
		Controle controle = new Controle(interfaceCliente, cliente);
		interfaceCliente.setActionListeners(controle);
		controle.iniciarAplicacao();
	}
}
