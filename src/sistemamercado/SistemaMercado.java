/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemamercado;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import tela.main;



/**
 *
 * @author Luiz Otavio
 */
public class SistemaMercado {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        main tela = new main();
        tela.setVisible(true);
        tela.setResizable(false);
        tela.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
    
}
