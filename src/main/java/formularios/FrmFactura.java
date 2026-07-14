
package formularios;

import Conexion.ConexionSQLServer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmFactura extends javax.swing.JPanel {
    private int idFactura = -1;
    
    public FrmFactura() {
        initComponents();
        
        txtCliente.setEditable(false);
        txtMontoTotal.setEditable(false);
        
        cargarPedidos();
        cargarTabla();
        ocultarColumnaID();
        
        cargarDatosPedido();
    }

    // Cargar la información en la lista de facturas (tabla).
    public void cargarTabla(){
        DefaultTableModel modelo = (DefaultTableModel) tlFacturas.getModel();
        modelo.setRowCount(0);
        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT f.id_factura, " +
                "f.id_pedido, " +
                "c.nombre, " +
                "f.fecha, " +
                "f.monto_total, " +
                "f.estado_pago " +
                "FROM Factura f " +
                "INNER JOIN Pedido p ON f.id_pedido = p.id_pedido " +
                "INNER JOIN Cliente c ON p.id_cliente = c.id_cliente"
            );
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_factura"),
                    rs.getInt("id_pedido"),
                    rs.getString("nombre"),
                    rs.getTimestamp("fecha"),
                    rs.getBigDecimal("monto_total"),
                    rs.getString("estado_pago")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }
    
    // Limpiar los campos del formulario.
    public void limpiar(){
        idFactura = -1;
        ftfFecha.setText("");
        txtMontoTotal.setText("");
        txtCliente.setText("");
        if(cbxPedido.getItemCount()>0){cbxPedido.setSelectedIndex(0);}
        cbxEstadoPago.setSelectedIndex(0);
        cargarDatosPedido();
    }
    
    // Método para llenar el combo box de pedidos.
    public void cargarPedidos() {
        cbxPedido.removeAllItems();

        try {
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_pedido FROM Pedido ORDER BY id_pedido");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){cbxPedido.addItem(rs.getString("id_pedido"));}
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());}
    }
    
    // Metodo para que el combo box rellene los campos de cliente y monto automáticamente.
    public void cargarDatosPedido(){
        if(cbxPedido.getSelectedItem()==null)return;
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.nombre, p.total " +
                "FROM Pedido p " +
                "INNER JOIN Cliente c " +
                "ON p.id_cliente=c.id_cliente " +
                "WHERE p.id_pedido=?"
            );
            ps.setInt(1,Integer.parseInt(cbxPedido.getSelectedItem().toString()));
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                txtCliente.setText(rs.getString("nombre"));
                txtMontoTotal.setText(rs.getBigDecimal("total").toString());
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }
    
    // Ocultar la columna ID del usuario.
    public void ocultarColumnaID(){
        tlFacturas.getColumnModel().getColumn(0).setMinWidth(0);
        tlFacturas.getColumnModel().getColumn(0).setMaxWidth(0);
        tlFacturas.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    // Verifica que los datos de la factura sean correctos.
    private boolean validarDatosFactura() {
        if (cbxPedido.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un pedido.");
            return false;
        }
        
        if (ftfFecha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese una fecha.");
            return false;
        }

        if (txtMontoTotal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existe monto.");
            return false;
        }

        try {
            BigDecimal monto = new BigDecimal(txtMontoTotal.getText());
            
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(null, "El monto debe ser mayor que cero.");
                return false;
            }
        } 
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Monto inválido.");
            return false;
        }
        return true;
    }
    
    // Método usado para el evento de BUSCAR, contiene la consulta que será enviada a la base de datos.
    public void buscarFactura(){
        DefaultTableModel modelo = (DefaultTableModel) tlFacturas.getModel();
        modelo.setRowCount(0);
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT f.id_factura, " +
                "f.id_pedido, " +
                "c.nombre, " +
                "f.fecha, " +
                "f.monto_total, " +
                "f.estado_pago " +
                "FROM Factura f " +
                "INNER JOIN Pedido p ON f.id_pedido = p.id_pedido " +
                "INNER JOIN Cliente c ON p.id_cliente = c.id_cliente " +
                "WHERE f.id_pedido = ?"
            );
            ps.setInt(1,Integer.parseInt(cbxPedido.getSelectedItem().toString()));
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_factura"),
                    rs.getInt("id_pedido"),
                    rs.getString("nombre"),
                    rs.getTimestamp("fecha"),
                    rs.getBigDecimal("monto_total"),
                    rs.getString("estado_pago")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtMontoTotal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        cbxPedido = new javax.swing.JComboBox<>();
        txtCliente = new javax.swing.JTextField();
        ftfFecha = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        cbxEstadoPago = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tlFacturas = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(875, 655));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR FACTURAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Pedido:");

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Fecha:");

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Monto total:");

        txtMontoTotal.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel4.setText("Cliente:");

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

        cbxPedido.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxPedido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Seleccione --" }));
        cbxPedido.addActionListener(this::cbxPedidoActionPerformed);

        txtCliente.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        ftfFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        ftfFecha.setToolTipText("Ejem: 17/09/08");
        ftfFecha.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel5.setText("Estado del pago:");

        cbxEstadoPago.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxEstadoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "Pagado", "Anulado" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cbxPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cbxEstadoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cbxPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cbxEstadoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE FACTURAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tlFacturas.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tlFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Pedido", "Cliente", "Fecha", "Monto total", "Estado del pago"
            }
        ));
        tlFacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tlFacturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tlFacturas);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    // Evento del combo box.
    private void cbxPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxPedidoActionPerformed
        cargarDatosPedido();
    }//GEN-LAST:event_cbxPedidoActionPerformed

    // Evento para AGREGAR la información de los campos.
    private void btnCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCreateMouseClicked
        if(cbxPedido.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Seleccione un pedido.");
            return;
        }

        String fecha = ftfFecha.getText();
        String monto = txtMontoTotal.getText();
        String estado = cbxEstadoPago.getSelectedItem().toString();
        int idPedido = Integer.parseInt(cbxPedido.getSelectedItem().toString());

        if (!validarDatosFactura()) {return;}
        
        if(txtMontoTotal.getText().trim().isEmpty()){ // Validar el monto aunque sea automático.
            JOptionPane.showMessageDialog(null, "No existe monto.");
            return;
        }
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("INSERT INTO Factura(fecha,monto_total,estado_pago,id_pedido) VALUES(?,?,?,?)");

            ps.setString(1, fecha);
            ps.setBigDecimal(2, new BigDecimal(monto));
            ps.setString(3, estado);
            ps.setInt(4, idPedido);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Factura registrada.");
            
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnCreateMouseClicked

    // Evento para BUSCAR una factura.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        if(cbxPedido.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Seleccione un pedido.");
            return;
        }
        buscarFactura();
    }//GEN-LAST:event_btnReadMouseClicked
    
    // Evento para MODIFICAR con la información de los campos una factura existente.
    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        if(idFactura==-1){
            JOptionPane.showMessageDialog(null,"Seleccione una factura.");
            return;
        }

        String fecha = ftfFecha.getText();
        String monto = txtMontoTotal.getText();
        String estado = cbxEstadoPago.getSelectedItem().toString();
        int idPedido = Integer.parseInt(cbxPedido.getSelectedItem().toString());

        if (!validarDatosFactura()) {return;}
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement( "UPDATE Factura SET fecha=?, monto_total=?, estado_pago=?, id_pedido=? WHERE id_factura=?");

            ps.setString(1, fecha);
            ps.setBigDecimal(2,new BigDecimal(monto));
            ps.setString(3,estado);
            ps.setInt(4,idPedido);
            ps.setInt(5,idFactura);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Factura modificada.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnUpdateMouseClicked

    // Evento para ELIMINAR una factura de la base de datos.
    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if(idFactura==-1){
            JOptionPane.showMessageDialog(null,"Seleccione una factura.");
            return;
        }

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Factura WHERE id_factura=?"
            );

            ps.setInt(1,idFactura);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,"Factura eliminada.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){ // Modificado el catch para informar cuando una factura tenga un pago registrado.
            if(ex.getErrorCode()==547){JOptionPane.showMessageDialog(null, "No se puede eliminar la factura porque tiene pagos registrados.");}
            else{JOptionPane.showMessageDialog(null, "ERROR: "+ex.getMessage());}
        }
    }//GEN-LAST:event_btnDeleteMouseClicked

    // Método para que al hacer clic en un registro se completen los campos y gurade el ID respectivo.
    private void tlFacturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tlFacturasMouseClicked
        int fila = tlFacturas.getSelectedRow();

        if(fila<0)return;
        idFactura = Integer.parseInt(tlFacturas.getValueAt(fila,0).toString());
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Factura WHERE id_factura=?");

            ps.setInt(1,idFactura);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ftfFecha.setText(rs.getString("fecha"));
                txtMontoTotal.setText(rs.getBigDecimal("monto_total").toString());
                cbxEstadoPago.setSelectedItem(rs.getString("estado_pago"));
                cbxPedido.setSelectedItem(String.valueOf(rs.getInt("id_pedido")));
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_tlFacturasMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnRead;
    public javax.swing.JButton btnUpdate;
    public javax.swing.JComboBox<String> cbxEstadoPago;
    public javax.swing.JComboBox<String> cbxPedido;
    private javax.swing.JFormattedTextField ftfFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTable tlFacturas;
    public javax.swing.JTextField txtCliente;
    public javax.swing.JTextField txtMontoTotal;
    // End of variables declaration//GEN-END:variables
}