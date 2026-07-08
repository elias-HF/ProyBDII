
package formularios;

import Conexion.ConexionSQLServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmCliente extends javax.swing.JPanel {
    private int idCliente = -1; // Variable para almacenar el id del cliente.
    
    // Constructor de 'FrmCliente'.
    public FrmCliente() {
        initComponents();
        
        cargarTabla();
        ocultarColumnaID();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR CLIENTES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Teléfono:");

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Correo electrónico:");

        txtNombre.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        txtTelefono.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        txtEmail.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

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
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE CLIENTES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblClientes.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Teléfono", "Correo electrónico"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // AGREGAR: Método para guardar los datos mediante el botón del formulario.
    private void btnCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCreateMouseClicked
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String Email = txtEmail.getText();
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("INSERT INTO Cliente(nombre,telefono,correo) VALUES(?,?,?)");
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, Email);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Cliente registrado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnCreateMouseClicked

    // MODIFICAR: Método por el cual se modificará después de la acción de búsqueda, esto mediante el nombre del cliente.
    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        if (idCliente == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente de la tabla.");
            return;
        }
        
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String Email = txtEmail.getText();
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("UPDATE Cliente SET nombre=?,telefono=?,correo=? WHERE id_cliente=?");
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, Email);
            ps.setInt(4, idCliente);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Cliente modificado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnUpdateMouseClicked

    // BUSCAR: Método para buscar según el nombre del cliente.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        if(txtNombre.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Ingrese un nombre.");
            return;
        }
        buscarCliente();
    }//GEN-LAST:event_btnReadMouseClicked

    // ELIMINAR: Método para eliminar al cliente presente en el formulario.
    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if (idCliente == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente de la tabla.");
            return;
        }
       
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Cliente WHERE id_cliente=?");
            ps.setInt(1, idCliente);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Cliente eliminado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnDeleteMouseClicked

    // Método para que se rellenen los campos cuando se haga clic en un fila de la tabla.
    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        int fila = tblClientes.getSelectedRow();

        if(fila >= 0){
            idCliente = Integer.parseInt(tblClientes.getValueAt(fila,0).toString());
            txtNombre.setText(tblClientes.getValueAt(fila,1).toString());
            txtTelefono.setText(tblClientes.getValueAt(fila,2).toString());
            txtEmail.setText(tblClientes.getValueAt(fila,3).toString());
        }
    }//GEN-LAST:event_tblClientesMouseClicked

    // LIMPIAR: Método para limpiar los campos después de una acción.
    public void limpiar(){
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        idCliente = -1;
    }
    
    // CARGAR REGISTROS DE LA TABLA
    public void cargarTabla(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblClientes.getModel();
        modeloTabla.setRowCount(0); // Reinicia siempre que se inicie el programa las filas y no se repita la información.
        
        PreparedStatement ps;
        ResultSet rs;
        ResultSetMetaData rsmd;
        int columnas;
        
        try {
            Connection con = ConexionSQLServer.obtenerConexion();
            ps = con.prepareStatement("SELECT id_cliente,nombre,telefono,correo FROM Cliente");
            rs = ps.executeQuery();
            rsmd = rs.getMetaData();
            columnas = rsmd.getColumnCount();
            
            while(rs.next()){
                Object[] fila = new Object[columnas];
                for (int i=0; i < columnas; i++){
                    fila[i] = rs.getObject(i + 1);
                }
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }
    
    // Método relacionado con la búsqueda.
    public void buscarCliente(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblClientes.getModel();
        modeloTabla.setRowCount(0);
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_cliente,nombre,telefono,correo FROM Cliente WHERE nombre LIKE ?");
            ps.setString(1,"%"+txtNombre.getText()+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo")
                });
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());
        }
    }
    
    // Ocultar la columna del ID.
    public void ocultarColumnaID() {
        tblClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tblClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tblClientes.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnRead;
    public javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTable tblClientes;
    public javax.swing.JTextField txtEmail;
    public javax.swing.JTextField txtNombre;
    public javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}