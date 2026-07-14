
package formularios;

import Conexion.ConexionSQLServer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmMovInventario extends javax.swing.JPanel {
    private int idMovimiento = -1;
    
    public FrmMovInventario() {
        initComponents();
        
        cargarInsumos();
        cargarTabla();
        ocultarColumnaID();
    }

    // Método para limpiar los campos después de una acción del CRUD.
    public void limpiar(){
        idMovimiento = -1;
        ftfFecha.setText("");
        txtCantidad.setText("");
        cbxTipoMovimiento.setSelectedIndex(0);
        if(cbxInsumo.getItemCount()>0)cbxInsumo.setSelectedIndex(0);
    }

    // Método para cargar la tabla con la información mediante una consulta.
    public void cargarTabla(){
        DefaultTableModel modelo = (DefaultTableModel) tblMovimientos.getModel();
        modelo.setRowCount(0);

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT m.id_movimiento, " +
                "i.nombre, " +
                "m.fecha, " +
                "m.tipo_movimiento, " +
                "m.cantidad " +
                "FROM Movimiento_Inventario m " +
                "INNER JOIN Insumo i " +
                "ON m.id_insumo=i.id_insumo"
            );
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getTimestamp("fecha"),
                    rs.getString("nombre"),
                    rs.getString("tipo_movimiento"),
                    rs.getBigDecimal("cantidad")
                });
            }
        }catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }

    // Hacer que el combo box de insumos se rellene automáticamente mediante los datos de la BD.
    public void cargarInsumos(){
        cbxInsumo.removeAllItems();
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_insumo,nombre FROM Insumo");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){cbxInsumo.addItem(rs.getInt("id_insumo") + " - " + rs.getString("nombre"));}
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }

    // Método para buscar mediante una consulta el movimiento mediante el id.
    public void buscarMovimiento(){
        DefaultTableModel modelo=(DefaultTableModel)tblMovimientos.getModel();
        modelo.setRowCount(0);
        try{
            int idInsumo=Integer.parseInt(cbxInsumo.getSelectedItem().toString().split(" - ")[0]);
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                "SELECT m.id_movimiento," +
                "i.nombre," +
                "m.fecha," +
                "m.tipo_movimiento," +
                "m.cantidad " +
                "FROM Movimiento_Inventario m " +
                "INNER JOIN Insumo i " +
                "ON m.id_insumo=i.id_insumo " +
                "WHERE m.id_insumo=?"
            );
            ps.setInt(1,idInsumo);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getTimestamp("fecha"),
                    rs.getString("nombre"),
                    rs.getString("tipo_movimiento"),
                    rs.getBigDecimal("cantidad")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }

    // Ocultar al ususario la columna del ID de la tabla.
    public void ocultarColumnaID(){
        tblMovimientos.getColumnModel().getColumn(0).setMinWidth(0);
        tblMovimientos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblMovimientos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        cbxTipoMovimiento = new javax.swing.JComboBox<>();
        cbxInsumo = new javax.swing.JComboBox<>();
        ftfFecha = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMovimientos = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR CAMBIOS DEL INVENTARIO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Insumo:");

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Fecha:");

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Tipo de movimiento:");

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel4.setText("Cantidad:");

        txtCantidad.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

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

        btnModificar.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconUpd.png"))); // NOI18N
        btnModificar.setToolTipText("Modificar");
        btnModificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnModificarMouseClicked(evt);
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

        cbxTipoMovimiento.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxTipoMovimiento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrada", "Salida", "Ajuste" }));

        cbxInsumo.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxInsumo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                        .addComponent(cbxInsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cbxTipoMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
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
                                    .addComponent(cbxInsumo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(cbxTipoMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE MOVIMIENTOS DEL INVENTARIO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblMovimientos.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Fecha", "Insumo", "Tipo de movimiento", "Cantidad"
            }
        ));
        tblMovimientos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMovimientosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMovimientos);

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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Evento para AGREGAR el movimiento.
    private void btnCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCreateMouseClicked
        if(cbxInsumo.getSelectedItem()==null){ // VALIDACIÓN: por si el combo box de insumo queda vacío.
            JOptionPane.showMessageDialog(null, "Seleccione un insumo."); 
            return;
        }
        
        if(ftfFecha.getText().trim().isEmpty()){ // VALIDACIÓN: evitar que la fecha quede vacía.
            JOptionPane.showMessageDialog(null,"Ingrese una fecha.");
            return;
        }

        BigDecimal cantidad;
        try{cantidad = new BigDecimal(txtCantidad.getText());} // VALIDACIÓN: evitar un exception en caso de un ingreso de carácteres inadecuado.
        catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null,"Ingrese una cantidad válida.");
            return;
        }

        if(cantidad.compareTo(BigDecimal.ZERO)<=0){ // VALIDACIÓN: evitar que se ingrese un número negativo. 
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que cero.");
            return;
        }

        if(cbxTipoMovimiento.getSelectedIndex()==0){ // VALIDACIÓN: en caso de que se quede seleccionado: '-- seleccione --'.
            JOptionPane.showMessageDialog(null, "Seleccione un tipo de movimiento.");
            return;
        }

        try{
            int idInsumo=Integer.parseInt(cbxInsumo.getSelectedItem().toString().split(" - ")[0]);
            
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement("INSERT INTO Movimiento_Inventario(fecha,tipo_movimiento,cantidad,id_insumo) VALUES(?,?,?,?)");

            ps.setString(1,ftfFecha.getText());
            ps.setString(2,cbxTipoMovimiento.getSelectedItem().toString());
            ps.setBigDecimal(3, cantidad);
            ps.setInt(4,idInsumo);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,"Movimiento registrado.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnCreateMouseClicked

    // Evento para BUSCAR el movimiento en la BD.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        if(cbxInsumo.getSelectedItem()==null){ // VALIDACIÓN: pedir que el usuario seleccione un insumo.
            JOptionPane.showMessageDialog(null, "Seleccione un insumo.");
            return;
        }
        
        buscarMovimiento();
    }//GEN-LAST:event_btnReadMouseClicked

    // Evento para MODIFICAR
    private void btnModificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnModificarMouseClicked
        if(idMovimiento == -1){
            JOptionPane.showMessageDialog(null,"Seleccione un movimiento.");
            return;
        }

        if(ftfFecha.getText().trim().isEmpty()){ // VALIDACIÓN: evitar que la fecha quede vacía.
            JOptionPane.showMessageDialog(null,"Ingrese una fecha.");
            return;
        }

        BigDecimal cantidad;
        try{cantidad = new BigDecimal(txtCantidad.getText());} // VALIDACIÓN: evitar un exception en caso de un ingreso de carácteres inadecuado.
        catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null,"Ingrese una cantidad válida.");
            return;
        }

        if(cantidad.compareTo(BigDecimal.ZERO)<=0){ // VALIDACIÓN: evitar que se ingrese un número negativo. 
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que cero.");
            return;
        }

        if(cbxTipoMovimiento.getSelectedIndex()==0){ // VALIDACIÓN: en caso de que se quede seleccionado: '-- seleccione --'.
            JOptionPane.showMessageDialog(null, "Seleccione un tipo de movimiento.");
            return;
        }

        if(cbxInsumo.getSelectedItem()==null){ // VALIDACIÓN: por si el combo box de insumo queda vacío.
            JOptionPane.showMessageDialog(null, "Seleccione un insumo."); 
            return;
        }
        
        try{
            int idInsumo = Integer.parseInt(cbxInsumo.getSelectedItem().toString().split(" - ")[0]);
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE Movimiento_Inventario " +
                "SET fecha=?, tipo_movimiento=?, cantidad=?, id_insumo=? " +
                "WHERE id_movimiento=?"
            );

            ps.setString(1, ftfFecha.getText());
            ps.setString(2, cbxTipoMovimiento.getSelectedItem().toString());
            ps.setBigDecimal(3, new BigDecimal(txtCantidad.getText()));
            ps.setInt(4, idInsumo);
            ps.setInt(5, idMovimiento);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,"Movimiento modificado.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());}
    }//GEN-LAST:event_btnModificarMouseClicked

    // Evento para ELIMINAR el movimiento de la base de datos.
    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if(idMovimiento == -1){
            JOptionPane.showMessageDialog(null,"Seleccione un movimiento.");
            return;
        }

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Movimiento_Inventario WHERE id_movimiento=?");
            ps.setInt(1, idMovimiento);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Movimiento eliminado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());}
    }//GEN-LAST:event_btnDeleteMouseClicked

    // Evento para que al hacer clic en un registro se completen los campos.
    private void tblMovimientosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMovimientosMouseClicked
        int fila = tblMovimientos.getSelectedRow();
        if(fila < 0) return;
        idMovimiento = Integer.parseInt(tblMovimientos.getValueAt(fila,0).toString());

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movimiento_Inventario WHERE id_movimiento=?");
            ps.setInt(1, idMovimiento);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ftfFecha.setText(rs.getString("fecha"));
                cbxTipoMovimiento.setSelectedItem(rs.getString("tipo_movimiento"));
                txtCantidad.setText(rs.getBigDecimal("cantidad").toString());
                int idInsumo = rs.getInt("id_insumo");
                for(int i=0; i<cbxInsumo.getItemCount(); i++){
                    if(cbxInsumo.getItemAt(i).startsWith(idInsumo + " -")){
                        cbxInsumo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());}
    }//GEN-LAST:event_tblMovimientosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnModificar;
    public javax.swing.JButton btnRead;
    public javax.swing.JComboBox<String> cbxInsumo;
    public javax.swing.JComboBox<String> cbxTipoMovimiento;
    public javax.swing.JFormattedTextField ftfFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTable tblMovimientos;
    public javax.swing.JTextField txtCantidad;
    // End of variables declaration//GEN-END:variables
}