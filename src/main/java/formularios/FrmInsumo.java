
package formularios;

import Conexion.ConexionSQLServer;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmInsumo extends javax.swing.JPanel {
    private int idInsumo = -1; // Variable para almacenar el id del insumo.
    
    // Constructor de 'FrmInsumo'.
    public FrmInsumo() {
        initComponents();
        
        cargarTabla();
        ocultarColumnaID();
    }
    
    // LIMPIAR: Método para limpiar los campos después de una acción.
    public void limpiar(){
        txtNombre.setText("");
        cbxTipo.setSelectedIndex(0);
        cbxUnidad.setSelectedIndex(0);
        txtStockMin.setText("");
        txtStockMax.setText(""); // Llamado Stock Actual en la BD
        idInsumo = -1;
    }
    
    // Ocultar la columna del ID.
    public void ocultarColumnaID() {
        tblInsumos.getColumnModel().getColumn(0).setMinWidth(0);
        tblInsumos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblInsumos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    // Cargar los registros de la base de datos en la tabla.
    public void cargarTabla(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblInsumos.getModel();
        modeloTabla.setRowCount(0); // Reinicia siempre que se inicie el programa las filas y no se repita la información.
        
        PreparedStatement ps;
        ResultSet rs;
        ResultSetMetaData rsmd;
        int columnas;
        
        try {
            Connection con = ConexionSQLServer.obtenerConexion();
            ps = con.prepareStatement(
                    "SELECT " +
                    "i.id_insumo, " +
                    "i.nombre, " +
                    "t.nombre_tipo, " +
                    "u.nombre_unidad, " +
                    "i.stock_minimo, " +
                    "i.stock_actual " +
                    "FROM Insumo i " +
                    "INNER JOIN Tipo_Insumo t ON i.id_tipo_insumo = t.id_tipo_insumo " +
                    "INNER JOIN Unidad_Medida u ON i.id_unidad_medida = u.id_unidad_medida"
            );
            rs = ps.executeQuery();
            rsmd = rs.getMetaData();
            columnas = rsmd.getColumnCount();
            
            while(rs.next()){
                Object[] fila = new Object[columnas];
                for (int i=0; i < columnas; i++){fila[i] = rs.getObject(i + 1);}
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }
    
    // Método destinado a validaciones para el MODIFICAR y AGREGAR.
    private boolean validarDatos() {
        if (cbxUnidad.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Seleccione una unidad.");
            return false;
        }

        if (cbxTipo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un tipo.");
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre del insumo.");
            return false;
        }

        if (txtNombre.getText().length() > 100) {
            JOptionPane.showMessageDialog(null, "El nombre no puede superar los 100 caracteres.");
            return false;
        }

        if (txtStockMin.getText().trim().isEmpty() || txtStockMax.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete los campos de stock.");
            return false;
        }

        double stockMin;
        double stockAct;

        try {
            stockMin = Double.parseDouble(txtStockMin.getText());
            stockAct = Double.parseDouble(txtStockMax.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Los stocks deben ser numéricos.");
            return false;
        }

        if (stockMin < 0 || stockAct < 0) {
            JOptionPane.showMessageDialog(null, "Los stocks no pueden ser negativos.");
            return false;
        }

        if (stockMin > stockAct) {
            JOptionPane.showMessageDialog(null, "El stock mínimo no puede ser mayor que el stock actual.");
            return false;
        }
        return true;
    }
    
    // Método relacionado con la búsqueda.
    public void buscarInsumo(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblInsumos.getModel();
        modeloTabla.setRowCount(0);
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT " +
                    "i.id_insumo, " +
                    "i.nombre, " +
                    "t.nombre_tipo, " +
                    "u.nombre_unidad, " +
                    "i.stock_minimo, " +
                    "i.stock_actual " +
                    "FROM Insumo i " +
                    "INNER JOIN Tipo_Insumo t ON i.id_tipo_insumo = t.id_tipo_insumo " +
                    "INNER JOIN Unidad_Medida u ON i.id_unidad_medida = u.id_unidad_medida " +
                    "WHERE i.nombre LIKE ?"
            );
            ps.setString(1,"%"+txtNombre.getText()+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_insumo"),
                    rs.getString("nombre"),
                    rs.getString("nombre_tipo"),
                    rs.getString("nombre_unidad"),
                    rs.getDouble("stock_minimo"),
                    rs.getDouble("stock_actual")
                });
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());
        }
    }
    
    // Métodos para obtención de id de las tablas vinculadas a insumo.
    private int obtenerIdTipo(String tipo) { // Respecto a el id del tipo.
        switch (tipo) {
            case "Ingrediente": return 1;
            case "Bebida": return 2;
            case "Limpieza": return 3;
            default: return 1;
        }
    }
    
    private int obtenerIdUnidad(String unidad) { // Respecto a el id de la unidad.
        switch (unidad) {
            case "Kilogramo": return 1;
            case "Litro": return 2;
            case "Unidad": return 3;
            default: return 1;
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbxTipo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cbxUnidad = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtStockMin = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtStockMax = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInsumos = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(875, 655));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR INSUMOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Nombre:");

        txtNombre.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreKeyTyped(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Tipo:");

        cbxTipo.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ingrediente", "Bebida", "Limpieza" }));

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Unidad medida:");

        cbxUnidad.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxUnidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Kilogramo", "Litro", "Unidad" }));

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel4.setText("Stock mínimo:");

        txtStockMin.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel5.setText("Stock actual:");

        txtStockMax.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

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
                        .addComponent(cbxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cbxUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtStockMin, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtStockMax, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(cbxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(cbxUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtStockMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(txtStockMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE INSUMOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblInsumos.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblInsumos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Tipo", "Unidad medida", "Stock mínimo", "Stock actual"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblInsumos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblInsumosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblInsumos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        String tipo = cbxTipo.getSelectedItem().toString();
        String unidad = cbxUnidad.getSelectedItem().toString();
        
        int idTipo = obtenerIdTipo(tipo);
        int idUnidad = obtenerIdUnidad(unidad);
       
        if (!validarDatos()) {return;} // VALIDACIÓN: llamar al método para validar.
        
        double stockMin = Double.parseDouble(txtStockMin.getText());
        double stockAct = Double.parseDouble(txtStockMax.getText());
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Insumo(nombre,id_tipo_insumo,id_unidad_medida,stock_minimo,stock_actual) VALUES(?,?,?,?,?)"        
            );
            ps.setString(1, nombre);
            ps.setInt(2, idTipo);
            ps.setInt(3, idUnidad);
            ps.setDouble(4, stockMin);
            ps.setDouble(5, stockAct);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Insumo registrado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());}
    }//GEN-LAST:event_btnCreateMouseClicked

    // BUSCAR: Método para buscar según el nombre del insumo.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        if(txtNombre.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null,"Ingrese un nombre.");
            return;
        }
        buscarInsumo();
    }//GEN-LAST:event_btnReadMouseClicked

    // MODIFICAR: Método por el cual se modificará después de la acción de búsqueda, esto mediante el nombre del insumo;
    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        String nombre = txtNombre.getText();     
        String tipo = cbxTipo.getSelectedItem().toString();
        String unidad = cbxUnidad.getSelectedItem().toString();
        
        int idTipo = obtenerIdTipo(tipo);
        int idUnidad = obtenerIdUnidad(unidad);
        
        if (!validarDatos()) {return;} // Llamar al validador.
        
        double stockMin = Double.parseDouble(txtStockMin.getText());
        double stockAct = Double.parseDouble(txtStockMax.getText());
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("UPDATE Insumo SET nombre=?, id_tipo_insumo=?, id_unidad_medida=?, stock_minimo=?, stock_actual=? WHERE id_insumo=?");
            
            ps.setString(1, nombre);
            ps.setInt(2, idTipo);
            ps.setInt(3, idUnidad);
            ps.setDouble(4, stockMin);
            ps.setDouble(5, stockAct);
            ps.setInt(6, idInsumo);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Insumo modificado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch (SQLException ex){JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());}
    }//GEN-LAST:event_btnUpdateMouseClicked

    // ELIMINAR: Método para eliminar al cliente presente en el formulario.
    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if (idInsumo == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un insumo de la tabla.");
            return;
        }
       
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Insumo WHERE id_insumo=?");
            ps.setInt(1, idInsumo);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Insumo eliminado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } 
        catch(SQLException ex){ // Modificado el 'catch' para que muestre el mensaje concreto en caso de un insumo referido en otras tablas.
            if(ex.getErrorCode()==547){JOptionPane.showMessageDialog(null, "No se puede eliminar el insumo porque esta siendo utilizado.");
            }
            else{JOptionPane.showMessageDialog(null,"ERROR: "+ex.getMessage());}
        }
    }//GEN-LAST:event_btnDeleteMouseClicked

    // Método para completar los campos al seleccionar una fila de la tabla de manera automática.
    private void tblInsumosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblInsumosMouseClicked
        int fila = tblInsumos.getSelectedRow();

        if(fila >= 0){
            idInsumo = Integer.parseInt(tblInsumos.getValueAt(fila,0).toString());
            txtNombre.setText(tblInsumos.getValueAt(fila,1).toString());
            cbxTipo.setSelectedItem(tblInsumos.getValueAt(fila, 2));
            cbxUnidad.setSelectedItem(tblInsumos.getValueAt(fila, 3));
            txtStockMin.setText(tblInsumos.getValueAt(fila,4).toString());
            txtStockMax.setText(tblInsumos.getValueAt(fila,5).toString());
        }
    }//GEN-LAST:event_tblInsumosMouseClicked

    // VALIDACIÓN: evitar que se ingrese algún numero en el nombre.
    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        char c = evt.getKeyChar();
        
        if(!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE){
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Solo se permiten letras.");
        }
    }//GEN-LAST:event_txtNombreKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnRead;
    public javax.swing.JButton btnUpdate;
    public javax.swing.JComboBox<String> cbxTipo;
    public javax.swing.JComboBox<String> cbxUnidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTable tblInsumos;
    public javax.swing.JTextField txtNombre;
    public javax.swing.JTextField txtStockMax;
    public javax.swing.JTextField txtStockMin;
    // End of variables declaration//GEN-END:variables
}