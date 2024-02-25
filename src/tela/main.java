/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tela;

import classe.conexaobd;
import static classe.conexaobd.statement;
import classe.converteValor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Luiz Otavio
 */
public class main extends javax.swing.JFrame {
    
    String nome = "", linha = ""; 
    Double total = 0.00, valor = 0.00, preco;
    int qntd = 1, quantiatotal = 0, sequencia = 0, numvendas = 0, sequenciaant = 0, sequenciavendas = 0;
    boolean valida = false, cupom = false;
    
    
    conexaobd objBD = new conexaobd();
    converteValor cv = new converteValor();
    Calendar now = Calendar.getInstance();
    Date dataHora = new Date();
    SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
    
    public main() {
        initComponents();
    }
    
    private void chamaProduto(){
        String sql = "SELECT tb01_descricao, tb01_cod_prod FROM tb01_produto WHERE tb01_cod_prod = '"+ lbcod.getText() + "' " ;
        
        try{
            objBD.conectar();
            ResultSet rs = statement.executeQuery(sql);     
            nome = rs.getString("tb01_descricao");
            objBD.desconectar();      
        }catch(Exception e){
            System.out.println(e.getMessage());
            nome = "";
            valor = 0.00;
        }
    }
    
    private void  carregaTabela(){
        String sql = "select * from tb01_produto where tb01_cod_prod= '" + lbcod.getText() + "'";
        DefaultTableModel tab = (DefaultTableModel) tbproduto.getModel();
        try {
            objBD.conectar();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                if(cupom == false){
                notaMax();            
                criaCupomNoBanco();
            }
                vendasMax();
                lbnome.setText(rs.getString("tb01_descricao"));
                lbvalor.setText(cv.ConvMoeda(rs.getString("tb01_preco")));
                lbvalor.setText(cv.ConvMoeda(String.valueOf(rs.getDouble("tb01_preco") * Integer.parseInt(lbqntd.getText()))));
                tab.addRow(new Object[]{rs.getString("tb01_descricao"), cv.ConvMoeda(rs.getString("tb01_preco")),
                lbqntd.getText(), cv.ConvMoeda(String.valueOf(rs.getDouble("tb01_preco") * Integer.parseInt(lbqntd.getText())))});
                linha+="<p style='font-size:5px; text-align:center'><a>" +lbnome.getText().substring(0, lbnome.getText().length())+"</a></p>";
                linha+="<p style='font-size:5px; color:black;'>"+lbvalor.getText()+" x "+lbqntd.getText()+" = "+lbvalor.getText()+"</p>";
                qntd = Integer.parseInt(lbqntd.getText());
                
                quantiatotal += qntd;
                valor = (rs.getDouble("tb01_preco"));
                preco = valor * qntd;
                total = total + preco;
                
                tbproduto.setModel(tab);
                
                lbtotal.setText(String.valueOf(total));
                valida = true;
                numvendas+=1;
                guardaVendas();
            }
            objBD.desconectar();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);

        }
        catch (Exception e){
           System.out.println(e.getMessage());
        } 
    } 
    
    private void geraCupom() {
        if(valida == true){
            guardaVendas();
            atualizaCupom();
            lbcupom.setVisible(true);
            lbcupom.setText("<html><br><p style='font-size:5px;"
                    + "text-align:center'>=======================</p><br><center><p "
                    + "text-align:center'><h3>CUPOM</h3></p></center><br><p "
                    + "text-align:center'>Av. Salvador Markowicz, 339</p><br><p "
                    + "text-align:center'>Bregança-Paulista - SP</p><br><p "
                    + "text-align:center'>=======================</p><br><p "
                    + "text-align:center'>Cupom Fiscal</p><br><p "
                    + "text-align:center'> "+lbdata.getText()+" - "+lbhora.getText()+"</p><br><p "
                    + "text-align:center'>=======================</p><br><p "
                    + "text-align:center'> "   +linha+"</p><br><p "             
                    + "text-align:center'>Quantidade: "+quantiatotal+"</p><br><p "    
                    + "text-align:center'>TOTAL: R$: "+total+"0 </p><br><p "      
                    + "text-align:center'>=======================</p><br><p " 
                    + "text-align:center'>OBRIGADO PELA PREFERÊCIA</p><br><p "        
                    + "</p><html>");
            cupom = false;
            sequencia = 0;
            numvendas = 0;
            sequenciavendas = 0;
                    
        }else{
            JOptionPane.showMessageDialog(rootPane, "Nenhum produto inserido");
            lbcupom.setText("");
        }
        total = 0.00;
        valor = 0.00;
        quantiatotal = 0;
        qntd = 1;
        linha = "";
        lbtotal.setText("");
        lbnome.setText("");
        lbvalor.setText("");
        lbimg.setIcon(null);
        valida = false;
        lbqntd.setText("1");
        lbqntd.enable(false);
        lbcod.requestFocus();        
    }    
    private void carregaImagem(){
        ImageIcon imageIcon = new ImageIcon(new ImageIcon( "src/img/"+lbcod.getText()+".jpg").getImage().getScaledInstance(lbimg.getWidth(), lbimg.getHeight(), Image.SCALE_DEFAULT));
        lbimg.setIcon(imageIcon);    
    }
    
    private void notaMax(){
        String sql = "Select max(tb02_nro_cupom) from tb02_nota_fiscal";         
        try{
            sequencia = 0;
            int maxSenha = 0;

            objBD.conectar();

            ResultSet rs = statement.executeQuery(sql);  

            if (rs.next()) 
            {
                maxSenha = rs.getInt(1);
            }
            sequencia = maxSenha + 1; 
            objBD.desconectar();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private void vendasMax(){
        String sql = "Select max(tb03_id_compra) from tb03_vendas";         
        try{
            sequenciavendas = 0;
            int maxVendas = 0;

            objBD.conectar();

            ResultSet rs = statement.executeQuery(sql);  

            if (rs.next()) 
            {
                maxVendas = rs.getInt(1);
            }

            sequenciavendas = maxVendas + 1; 
            objBD.desconectar();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private void criaCupomNoBanco(){
        String sql = "INSERT INTO tb02_nota_fiscal VALUES('"+sequencia+"', NOW(), NOW(), '"+ total +"')";
        
        try{
            objBD.conectar();
            objBD.runSQL(sql);
            objBD.desconectar();
            cupom = true;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    } 
    
    private void guardaVendas(){
        String sql = "INSERT INTO tb03_vendas values ('"+ sequenciavendas +"', 1, '"+ sequencia +"', '"+ lbcod.getText() +"', '"+ valor +"', '"+ qntd +"', '"+ preco +"' )";
        try{
            objBD.conectar();
            objBD.runSQL(sql);
            objBD.desconectar();
            cupom = true;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private void atualizaCupom(){
        String sql="UPDATE (tb02_nota_fiscal) SET tb02_valor_total = '"+ total +"' "
                    + "WHERE tb02_nro_cupom = '"+ sequencia +"' ";
        try{
            objBD.conectar();
            objBD.runSQL(sql);
            objBD.desconectar();
            cupom = true;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private void deletaCupomVendas(){
        String sql = "DELETE FROM tb03_vendas WHERE tb03_cupom = '"+ sequencia +", '";
            try{
                objBD.conectar();
                objBD.runSQL(sql);
                objBD.desconectar();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
            
    }
    private void deletaCupom(){
        String sql = "DELETE FROM tb02_nota_fiscal WHERE tb02_nro_cupom = '"+ sequencia +"'";
            try{
                objBD.conectar();
                objBD.runSQL(sql);
                objBD.desconectar();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lbvalor = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbnome = new javax.swing.JLabel();
        btnfinal = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblogo = new javax.swing.JLabel();
        lbtotal = new javax.swing.JLabel();
        lbdata = new javax.swing.JLabel();
        lbhora = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lbimg = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbproduto = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        lbcod = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lbcupom = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lbqntd = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(970, 789));
        setPreferredSize(new java.awt.Dimension(970, 810));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(990, 780));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("VALOR:");

        lbvalor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("NOME:");

        lbnome.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        btnfinal.setBackground(new java.awt.Color(0, 102, 255));
        btnfinal.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnfinal.setForeground(new java.awt.Color(255, 255, 255));
        btnfinal.setText("Finalizar");
        btnfinal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnfinalMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("F5 - Alterar Quantidade");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("F1 - Confirma");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("ESC - Encerra");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jLabel7)
                .addGap(144, 144, 144)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(67, 67, 67))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(19, 19, 19))
        );

        lblogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.PNG"))); // NOI18N

        lbtotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        lbdata.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        lbhora.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbhora.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbimg, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbimg, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Painel");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("QNTDE:");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("TOTAL:");

        tbproduto.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tbproduto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Valor", "Quantidade", "Total"
            }
        ));
        tbproduto.setEnabled(false);
        tbproduto.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(tbproduto);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("CÓDIGO:");

        lbcod.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbcod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lbcodKeyReleased(evt);
            }
        });

        lbcupom.setBackground(new java.awt.Color(255, 204, 153));
        lbcupom.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbcupom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jScrollPane2.setViewportView(lbcupom);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        lbqntd.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#+0"))));
        lbqntd.setText("1");
        lbqntd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lbqntdActionPerformed(evt);
            }
        });
        lbqntd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lbqntdKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lbqntd, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13))))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGap(65, 65, 65)
                                        .addComponent(jLabel1))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(btnfinal, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 125, Short.MAX_VALUE)
                        .addComponent(lbdata, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbhora, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(lbvalor, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbcod, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbnome, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(210, 210, 210)
                .addComponent(lblogo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(lblogo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lbcod, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(lbnome, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(lbvalor, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lbqntd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbdata, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbhora, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnfinal, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 982, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        lbqntd.enable(false);
        class dataHora implements ActionListener{   
            public void actionPerformed(ActionEvent ae){
                Calendar now = Calendar .getInstance();
                lbhora.setText(String.format("%1$tH:%1$tM:%1$tS", now));
            }
        }
        Timer timer = new Timer(1000, new dataHora());
        timer.start();
        lbhora.setText(String.format("%1$tH:%1$tM:%1$tS", now));
        lbdata.setText(dt.format(dataHora)); 
    }//GEN-LAST:event_formWindowOpened

    private void lbcodKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lbcodKeyReleased
        if (lbcod.getText().length() == 13){
            evt.setKeyCode(KeyEvent.VK_ENTER);
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
        {
            chamaProduto();
            carregaImagem();
            lbnome.setText(nome);

            carregaTabela();
           
            lbtotal.setText(cv.ConvMoeda(String.valueOf(total)));
            
            lbvalor.setText(cv.ConvMoeda(String.valueOf(valor)));
            lbcod.setText("");
            lbqntd.setText("1");
            lbqntd.enable(false);
        }
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F5) {
            lbqntd.setEnabled(true);
            lbqntd.setText("");
            lbqntd.requestFocus();
            lbcod.setEnabled(false);
        }
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
            JOptionPane sair = new JOptionPane();

            int Sair = sair.showConfirmDialog(null, "CONFIRME PARA ENCERRAR O CAIXA", "Sair", JOptionPane.YES_NO_OPTION);

            if (Sair == JOptionPane.YES_OPTION) {
                deletaCupomVendas();
                deletaCupom();
                System.exit(0);
            }
        }
        
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F1){
            geraCupom();
            ((DefaultTableModel) tbproduto.getModel()).setRowCount(0);
        }
      
    }//GEN-LAST:event_lbcodKeyReleased

    private void btnfinalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnfinalMouseClicked
        geraCupom();
        ((DefaultTableModel) tbproduto.getModel()).setRowCount(0);
    }//GEN-LAST:event_btnfinalMouseClicked

    private void lbqntdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lbqntdKeyReleased
       
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
           lbcod.requestFocus();
           lbcod.enable(true);
           qntd = 1;
           lbqntd.enable(false);
        }
        String EMPTY = "";
        if(lbqntd.getText().equals("0") || lbqntd.getText().equals(EMPTY)){
            JOptionPane.showMessageDialog(rootPane, "Valor inserido inválido.\nTente novamente!");
            lbcod.enable(true);
            lbcod.requestFocus(true);
        }
    }//GEN-LAST:event_lbqntdKeyReleased

    private void lbqntdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbqntdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lbqntdActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main().setVisible(true);
            }
        });
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnfinal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField lbcod;
    private javax.swing.JLabel lbcupom;
    private javax.swing.JLabel lbdata;
    private javax.swing.JLabel lbhora;
    private javax.swing.JLabel lbimg;
    private javax.swing.JLabel lblogo;
    private javax.swing.JLabel lbnome;
    private javax.swing.JFormattedTextField lbqntd;
    private javax.swing.JLabel lbtotal;
    private javax.swing.JLabel lbvalor;
    private javax.swing.JTable tbproduto;
    // End of variables declaration//GEN-END:variables
}
