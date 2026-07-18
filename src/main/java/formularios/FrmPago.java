
package formularios;

import Conexion.ConexionSQLServer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmPago extends javax.swing.JPanel {
    private int idPago = -1;
    
    public FrmPago() {
        initComponents();
        
        cargarFacturas();
        cargarTabla();
        ocultarColumnaID();
    }
    
    // Método para limpiar campos después de determinados eventos.
    public void limpiar(){
        ftfFecha.setText("");
        txtMonto.setText("");
        cbxMetodoPago.setSelectedIndex(0);
        cbxFactura.setSelectedIndex(0);
        idPago = -1;
    }
    
    // Método para cargar las facturas en el combo box.
    public void cargarFacturas(){
        cbxFactura.removeAllItems();
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_factura FROM Factura ORDER BY id_factura");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){cbxFactura.addItem(rs.getString("id_factura"));}
        }catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }
    
    // Método para cargar la tabla con los pagos registrados en la base de datos.
    public void cargarTabla(){
        DefaultTableModel modelo=(DefaultTableModel)btlPagos.getModel(); 
        modelo.setRowCount(0);

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                "SELECT pa.id_pago,"+
                "c.nombre,"+
                "pa.fecha,"+
                "pa.metodo_pago,"+
                "pa.monto "+
                "FROM Pago pa "+
                "INNER JOIN Factura f ON pa.id_factura=f.id_factura "+
                "INNER JOIN Pedido pe ON f.id_pedido=pe.id_pedido "+
                "INNER JOIN Cliente c ON pe.id_cliente=c.id_cliente"
            );
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_pago"),
                    rs.getString("nombre"),
                    rs.getTimestamp("fecha"),
                    rs.getString("metodo_pago"),
                    rs.getBigDecimal("monto")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }
    
    // Método para buscar el pago y usarlo en el evento de BUSCAR.
    public void buscarPago(){
        DefaultTableModel modelo=(DefaultTableModel)btlPagos.getModel();
        modelo.setRowCount(0);
        
        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                "SELECT pa.id_pago,"+
                "c.nombre,"+
                "pa.fecha,"+
                "pa.metodo_pago,"+
                "pa.monto "+
                "FROM Pago pa "+
                "INNER JOIN Factura f ON pa.id_factura=f.id_factura "+
                "INNER JOIN Pedido pe ON f.id_pedido=pe.id_pedido "+
                "INNER JOIN Cliente c ON pe.id_cliente=c.id_cliente "+
                "WHERE pa.id_factura=?"
            );
            ps.setInt(1,Integer.parseInt(cbxFactura.getSelectedItem().toString()));
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_pago"),
                    rs.getString("nombre"),
                    rs.getTimestamp("fecha"),
                    rs.getString("metodo_pago"),
                    rs.getBigDecimal("monto")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }
    
    // Método para validar en REGISTRAR y MODIFICAR.
    private boolean validarPago(){
        if(cbxFactura.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Seleccione una factura.");
            return false;
        }

        if(ftfFecha.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null,"Ingrese una fecha.");
            return false;
        }

        if(cbxMetodoPago.getSelectedIndex()==0){
            JOptionPane.showMessageDialog(null,"Seleccione un método de pago.");
            return false;
        }

        BigDecimal monto;
        try{monto = new BigDecimal(txtMonto.getText());}
        catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null,"Ingrese un monto válido.");
            return false;
        }

        if(monto.compareTo(BigDecimal.ZERO)<=0){
            JOptionPane.showMessageDialog(null,"El monto debe ser mayor que cero.");
            return false;
        }
        return true;
    }
    
    // Método para ocultar la columna ID del usuario.
    public void ocultarColumnaID(){
        btlPagos.getColumnModel().getColumn(0).setMinWidth(0);
        btlPagos.getColumnModel().getColumn(0).setMaxWidth(0);
        btlPagos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        cbxMetodoPago = new javax.swing.JComboBox<>();
        cbxFactura = new javax.swing.JComboBox<>();
        ftfFecha = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        btlPagos = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR PAGOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Factura:");

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Fecha:");

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Método del pago:");

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel4.setText("Monto:");

        txtMonto.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnCreate.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        btnCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconAdd.png"))); // NOI18N
        btnCreate.setToolTipText("Agregar");
        btnCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCreateMouseClicked(evt);
            }
        });

        btnRead.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        btnRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconReq.png"))); // NOI18N
        btnRead.setToolTipText("Buscar");
        btnRead.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnReadMouseClicked(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconUpd.png"))); // NOI18N
        btnUpdate.setToolTipText("Modificar");
        btnUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUpdateMouseClicked(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconDel.png"))); // NOI18N
        btnDelete.setToolTipText("Eliminar");
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMouseClicked(evt);
            }
        });

        cbxMetodoPago.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta", "Yape", "Plin" }));

        cbxFactura.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxFactura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Seleccione --" }));

        ftfFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yy-MM-dd"))));
        ftfFecha.setToolTipText("Ejem: 25-09-31");
        ftfFecha.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cbxFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(cbxFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(cbxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE PAGOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        btlPagos.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        btlPagos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Cliente", "Fecha", "Método del pago", "Monto"
            }
        ));
        btlPagos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btlPagosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(btlPagos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Evento para AGREGAR un pago con la información de los campos.
    private void btnCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCreateMouseClicked
        if(cbxFactura.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Seleccione una factura.");
            return;
        }

        if(!validarPago()){return;}
        
        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement("INSERT INTO Pago(fecha,monto,metodo_pago,id_factura) VALUES(?,?,?,?)");
            
            ps.setString(1, ftfFecha.getText());
            ps.setBigDecimal(2,new BigDecimal(txtMonto.getText()));
            ps.setString(3,cbxMetodoPago.getSelectedItem().toString());
            ps.setInt(4,Integer.parseInt(cbxFactura.getSelectedItem().toString()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Pago registrado.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnCreateMouseClicked

    // Evento para BUSCAR un pago de la base de datos.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        if(cbxFactura.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Seleccione una factura.");
            return;
        }
        buscarPago();
    }//GEN-LAST:event_btnReadMouseClicked

    // Evento para MODIFICAR a un pago de la base de datos.
    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        if(idPago==-1){
            JOptionPane.showMessageDialog(null,"Seleccione un pago.");
            return;
        }

        if(!validarPago()){return;}
        
        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement("UPDATE Pago SET fecha=?,monto=?,metodo_pago=?,id_factura=? WHERE id_pago=?");

            ps.setString(1,ftfFecha.getText());
            ps.setBigDecimal(2,new BigDecimal(txtMonto.getText()));
            ps.setString(3,cbxMetodoPago.getSelectedItem().toString());
            ps.setInt(4,Integer.parseInt(cbxFactura.getSelectedItem().toString()));
            ps.setInt(5,idPago);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Pago modificado.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();

        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnUpdateMouseClicked

    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if(idPago==-1){
            JOptionPane.showMessageDialog(null,"Seleccione un pago.");
            return;
        }

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement("DELETE FROM Pago WHERE id_pago=?");
            
            ps.setInt(1,idPago);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,"Pago eliminado.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnDeleteMouseClicked

    // Evento para RELLENAR los campos del formulario con la información seleccionada de la tabla.
    private void btlPagosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btlPagosMouseClicked
        int fila=btlPagos.getSelectedRow();
        if(fila<0)return;
        idPago=Integer.parseInt(btlPagos.getValueAt(fila,0).toString());

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM Pago WHERE id_pago=?");

            ps.setInt(1,idPago);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                ftfFecha.setText(rs.getString("fecha"));
                txtMonto.setText(rs.getBigDecimal("monto").toString());
                cbxMetodoPago.setSelectedItem(rs.getString("metodo_pago"));
                cbxFactura.setSelectedItem(String.valueOf(rs.getInt("id_factura")));
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btlPagosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTable btlPagos;
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnRead;
    public javax.swing.JButton btnUpdate;
    public javax.swing.JComboBox<String> cbxFactura;
    public javax.swing.JComboBox<String> cbxMetodoPago;
    public javax.swing.JFormattedTextField ftfFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTextField txtMonto;
    // End of variables declaration//GEN-END:variables
}