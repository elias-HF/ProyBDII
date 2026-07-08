
// ESTA CLASE TENDRA DOS FASES
// FASE 1: CRUD normal para la parte de productos
// FASE 2: La asociación de los o el insumo para ese producto.

package formularios;

import Conexion.ConexionSQLServer;
import Utilidades.ItemCombo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmProducto extends javax.swing.JPanel {
    private int idProducto = -1;
    
    // Contructor de 'Productos'
    public FrmProducto() {
        initComponents();
        
        cargarTabla();
        cargarInsumos();
        ocultarColumnaID();
    }

    // Método para limpiar los campos después de una acción.
    public void limpiar(){
        txtNombre.setText("");
        cbxCategoria.setSelectedItem(0);
        txtPrecio.setText("");
        
        idProducto = -1;
    }
    
    // Ocultar la columna del ID.
    public void ocultarColumnaID() {
        tblProductos.getColumnModel().getColumn(0).setMinWidth(0);
        tblProductos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblProductos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    // Cargar la tabla de productos.
    public void cargarTabla(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblProductos.getModel();
        modeloTabla.setRowCount(0); // Reinicia siempre que se inicie el programa las filas y no se repita la información.
        
        PreparedStatement ps;
        ResultSet rs;
        ResultSetMetaData rsmd;
        int columnas;
        
        try {
            Connection con = ConexionSQLServer.obtenerConexion();
            ps = con.prepareStatement(
                    "SELECT\n" +
                    "id_producto,\n" +
                    "nombre,\n" +
                    "categoria,\n" +
                    "precio\n" +
                    "FROM Producto\n" +
                    "ORDER BY nombre;"
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
    
    // Método relacionado con la búsqueda.
    public void buscarProducto(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblProductos.getModel();
        modeloTabla.setRowCount(0);
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT\n" +
                    "id_producto,\n" +
                    "nombre,\n" +
                    "categoria,\n" +
                    "precio\n" +
                    "FROM Producto\n" +
                    "WHERE nombre LIKE ?;"
            );
            ps.setString(1,"%"+txtNombre.getText()+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio")
                });
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"ERROR: " + ex.toString());
        }
    }
    
    // Método para cargar la información del comboBox.
    public void cargarInsumos() {
        cbxInsumo.removeAllItems();
        try {
            Connection con = ConexionSQLServer.obtenerConexion();

            PreparedStatement ps = con.prepareStatement("SELECT id_insumo,nombre FROM Insumo ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                cbxInsumo.addItem( new ItemCombo(rs.getInt("id_insumo"),rs.getString("nombre")));
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex.toString());
        }
    }
    
    // Método para cargar los insumos en la 2da tabla.
    public void cargarInsumosProductos(){
        DefaultTableModel modeloTabla = (DefaultTableModel) tblInsumosProducto.getModel();
        modeloTabla.setRowCount(0);

        if(idProducto == -1){return;}

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT I.nombre, PI.cant_requerida " +
                "FROM Producto_Insumo PI " +
                "INNER JOIN Insumo I ON PI.id_insumo = I.id_insumo " +
                "WHERE PI.id_producto = ? " +
                "ORDER BY I.nombre"
            );
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                modeloTabla.addRow(new Object[]{
                    rs.getString("nombre"),
                    rs.getDouble("cant_requerida")
                });
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        cbxCategoria = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCantidadRequerida = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btnAsociar = new javax.swing.JButton();
        btnQuitar = new javax.swing.JButton();
        cbxInsumo = new javax.swing.JComboBox<ItemCombo>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInsumosProducto = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(875, 555));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR PRODUCTO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Categoría:");

        txtNombre.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Precio:");

        txtPrecio.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

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

        cbxCategoria.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrada", "Plato marino", "Plato criollo", "Bebida", "Postre" }));

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
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
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
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "INSUMOS DEL PRODUCTO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel4.setText("Insumo:");

        jLabel5.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel5.setText("Cantidad requerida:");

        txtCantidadRequerida.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btnAsociar.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnAsociar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconAsoc.png"))); // NOI18N
        btnAsociar.setText(" Asociar");
        btnAsociar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAsociarMouseClicked(evt);
            }
        });

        btnQuitar.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnQuitar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/IconQui.png"))); // NOI18N
        btnQuitar.setText(" Quitar");
        btnQuitar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQuitarMouseClicked(evt);
            }
        });

        cbxInsumo.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxInsumo.setModel(new javax.swing.DefaultComboBoxModel<ItemCombo>());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(cbxInsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtCantidadRequerida, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(btnAsociar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77)
                .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAsociar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cbxInsumo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtCantidadRequerida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE PRODUCTOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblProductos.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Categoría", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProductos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE INSUMOS DEL PRODUCTO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblInsumosProducto.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblInsumosProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Insumo", "Cantidad requerida"
            }
        ));
        jScrollPane2.setViewportView(tblInsumosProducto);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // AGREGAR: método para agregar un producto a la base de datos.
    private void btnCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCreateMouseClicked
        
        // Validar campos vacíos
        if(txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Complete todos los campos.");
            return;
        }
        double precio;
        // Validar que el precio sea un número
        try{
            precio = Double.parseDouble(txtPrecio.getText());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Ingrese un precio válido.");
            return;
        }
        String nombre = txtNombre.getText();
        String categoria = cbxCategoria.getSelectedItem().toString();
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("INSERT INTO Producto(nombre,categoria,precio) VALUES(?,?,?)");
            ps.setString(1, nombre);
            ps.setString(2, categoria);
            ps.setDouble(3, precio);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Producto registrado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnCreateMouseClicked

    // BUSCAR: método para buscar un producto por medio del nombre.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        if(txtNombre.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Ingrese un nombre.");
            return;
        }
        buscarProducto();
    }//GEN-LAST:event_btnReadMouseClicked

    // MODIFICAR: método para modificar un producto mediante el id.
    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        if (idProducto == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla.");
            return;
        }
        
        // Validar campos vacíos
        if(txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Complete todos los campos.");
            return;
        }
        double precio;
        // Validar que el precio sea un número
        try{
            precio = Double.parseDouble(txtPrecio.getText());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Ingrese un precio válido.");
            return;
        }
        String nombre = txtNombre.getText();
        String categoria = cbxCategoria.getSelectedItem().toString();
        
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("UPDATE Producto SET nombre=?,categoria=?,precio=? WHERE id_producto=?");
            ps.setString(1, nombre);
            ps.setString(2, categoria);
            ps.setDouble(3, precio);
            ps.setInt(4, idProducto);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Producto modificado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnUpdateMouseClicked

    // ELIMINAR: método para eliminar a un producto por medio de su id.
    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if (idProducto == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla.");
            return;
        }
       
        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement("DELETE FROM Producto WHERE id_producto=?");
            ps.setInt(1, idProducto);
            
            ps.executeUpdate(); // Se ejecuta la consulta con los datos que se desea guardar.
            JOptionPane.showMessageDialog(null, "Producto eliminado.");
            limpiar();
            cargarTabla();
            ocultarColumnaID();
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnDeleteMouseClicked

    // ASOCIAR: método para asociar insumos a un producto.
    private void btnAsociarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAsociarMouseClicked
        if (idProducto == -1) { // Validación para asegurarse de que se haya seleccionado un producto.
            JOptionPane.showMessageDialog(null, "Seleccione primero un producto.");
            return;
        }

        if (txtCantidadRequerida.getText().isEmpty()) { // Validación para asegurarse de que se haya ingresado una cantidad.
            JOptionPane.showMessageDialog(null, "Ingrese la cantidad requerida.");
            return;
        }

        double cantidad;

        try {
            cantidad = Double.parseDouble(txtCantidadRequerida.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Ingrese una cantidad válida.");
            return;
        }

        ItemCombo item = (ItemCombo) cbxInsumo.getSelectedItem();
        int idInsumo = item.getId();

        try {
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Producto_Insumo(id_producto,id_insumo,cant_requerida) VALUES(?,?,?)"
            );

            ps.setInt(1, idProducto);
            ps.setInt(2, idInsumo);
            ps.setDouble(3, cantidad);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Insumo asociado correctamente.");

            txtCantidadRequerida.setText("");

            cargarInsumosProductos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnAsociarMouseClicked

    // QUITAR: método para quitar un insumo de un producto.
    private void btnQuitarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuitarMouseClicked
        if (idProducto == -1) { // Validación para ver si se selcciono algún producto.
            JOptionPane.showMessageDialog(null, "Seleccione primero un producto.");
            return;
        }

        ItemCombo item = (ItemCombo) cbxInsumo.getSelectedItem();
        int idInsumo = item.getId();
        try {
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM Producto_Insumo WHERE id_producto=? AND id_insumo=?"
            );

            ps.setInt(1, idProducto);
            ps.setInt(2, idInsumo);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Insumo eliminado.");
            } else {
                JOptionPane.showMessageDialog(null, "Ese insumo no está asociado al producto.");
            }

            cargarInsumosProductos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
        }
    }//GEN-LAST:event_btnQuitarMouseClicked

    // Método para poder rellenar los campos con solo seleccionar algún registro de la tabla.
    private void tblProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductosMouseClicked
        int fila = tblProductos.getSelectedRow();

        if(fila >= 0){
            idProducto = Integer.parseInt(tblProductos.getValueAt(fila,0).toString());
            txtNombre.setText(tblProductos.getValueAt(fila,1).toString());
            cbxCategoria.setSelectedItem(tblProductos.getValueAt(fila,2).toString());
            txtPrecio.setText(tblProductos.getValueAt(fila,3).toString());
            
            cargarInsumosProductos();
        }
    }//GEN-LAST:event_tblProductosMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnAsociar;
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnQuitar;
    public javax.swing.JButton btnRead;
    public javax.swing.JButton btnUpdate;
    public javax.swing.JComboBox<String> cbxCategoria;
    public javax.swing.JComboBox<ItemCombo> cbxInsumo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public javax.swing.JTable tblInsumosProducto;
    public javax.swing.JTable tblProductos;
    public javax.swing.JTextField txtCantidadRequerida;
    public javax.swing.JTextField txtNombre;
    public javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}