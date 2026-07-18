
package formularios;

import Conexion.ConexionSQLServer;
import Utilidades.ItemCombo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FrmPedido extends javax.swing.JPanel {
    private int idPedido = -1;
    
    public FrmPedido() {
        initComponents();
        
        cargarTabla();
        cargarClientes();
        cargarMeseros();
        cargarEstados();
        cargarProductos();

        ocultarColumnaID();
        ocultarColumnaIDProducto();
    }

    // Limpiar formulario
    public void limpiar() {
        cbxCliente.setSelectedIndex(0);
        cbxMesero.setSelectedIndex(0);
        cbxEstado.setSelectedIndex(0);
        ftfFecha.setText("");
        ftfHora.setText("");
        txtCantidad.setText("");
        txtObservacion.setText("");
        idPedido = -1;
    }

    // Ocultar la columna del ID para la tabla de pedido.
    public void ocultarColumnaID() {
        tblPedidos.getColumnModel().getColumn(0).setMinWidth(0);
        tblPedidos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblPedidos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    // Ocultar la columna ID para la tabla de productos pedido.
    public void ocultarColumnaIDProducto(){
        tblProductosdelPedido.getColumnModel().getColumn(0).setMinWidth(0);
        tblProductosdelPedido.getColumnModel().getColumn(0).setMaxWidth(0);
        tblProductosdelPedido.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    // Cargar tabla con la información de pedido.
    public void cargarTabla(){
        DefaultTableModel modelo = (DefaultTableModel) tblPedidos.getModel();
        modelo.setRowCount(0);

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
            "SELECT " +
            "P.id_pedido," +
            "C.nombre cliente," +
            "M.nombre mesero," +
            "P.fecha," +
            "P.hora," +
            "E.nombre_estado," +
            "P.total " +
            "FROM Pedido P " +
            "INNER JOIN Cliente C ON P.id_cliente=C.id_cliente " +
            "INNER JOIN Mesero M ON P.id_mesero=M.id_mesero " +
            "INNER JOIN Estado_Pedido E ON P.id_estado=E.id_estado " +
            "ORDER BY P.id_pedido DESC"
            );
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_pedido"),
                    rs.getString("cliente"),
                    rs.getString("mesero"),
                    rs.getDate("fecha"),
                    rs.getTime("hora"),
                    rs.getString("nombre_estado"),
                    rs.getDouble("total")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,ex.toString());}
    }
    
    // Método para ACTUALIZAR el total del pedido.
    public void actualizarTotalPedido(){
        try{
            Connection con = ConexionSQLServer.obtenerConexion();

            PreparedStatement ps = con.prepareStatement(
                "UPDATE Pedido "
                +"SET total = "
                +"("
                +"SELECT ISNULL(SUM(cantidad * precio_unitario),0)"
                +" FROM Detalle_Pedido "
                +" WHERE id_pedido=?"
                +") "
                +"WHERE id_pedido=?"
            );

            ps.setInt(1,idPedido);
            ps.setInt(2,idPedido);

            ps.executeUpdate();

            cargarTabla();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,ex.toString());}
    }
    
    // Método de BUSCAR para implementarlo en el respectivo evento del botón.
    public void buscarPedido(){
        DefaultTableModel modelo = (DefaultTableModel) tblPedidos.getModel();
        modelo.setRowCount(0);

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT " +
                "P.id_pedido," +
                "C.nombre cliente," +
                "M.nombre mesero," +
                "P.fecha," +
                "P.hora," +
                "E.nombre_estado," +
                "P.total " +
                "FROM Pedido P " +
                "INNER JOIN Cliente C ON P.id_cliente=C.id_cliente " +
                "INNER JOIN Mesero M ON P.id_mesero=M.id_mesero " +
                "INNER JOIN Estado_Pedido E ON P.id_estado=E.id_estado " +
                "WHERE C.nombre LIKE ?"
            );

            ps.setString(1,"%"+((ItemCombo)cbxCliente.getSelectedItem()).toString()+"%");
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_pedido"),
                    rs.getString("cliente"),
                    rs.getString("mesero"),
                    rs.getDate("fecha"),
                    rs.getTime("hora"),
                    rs.getString("nombre_estado"),
                    rs.getDouble("total")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null, ex.toString());}
    }
    
    // Método para CARGAR a los CLIENTES en el combo box.
    public void cargarClientes(){
        cbxCliente.removeAllItems();

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                    "SELECT id_cliente,nombre " +
                    "FROM Cliente " +
                    "ORDER BY nombre"
            );

            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                cbxCliente.addItem(new ItemCombo(rs.getInt("id_cliente"),rs.getString("nombre")));
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,ex.toString());
        }
    }
    
    // Método para CARGAR a los MESEROS
    public void cargarMeseros(){
        cbxMesero.removeAllItems();

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                    "SELECT id_mesero,nombre " +
                    "FROM Mesero " +
                    "ORDER BY nombre"
            );

            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                cbxMesero.addItem(new ItemCombo(rs.getInt("id_mesero"), rs.getString("nombre")));
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null, ex.toString());}
    }
    
    // Método auxiliar para el ItemCombo para buscar uno en específico.
    public ItemCombo buscarItem(javax.swing.JComboBox<ItemCombo> combo,String nombre){
        for(int i=0;i<combo.getItemCount();i++){
            ItemCombo item = combo.getItemAt(i);
            if(item.toString().equals(nombre)){ return item;}
        }
        return null;
    }
    
    // Método para CARGAR los ESTADOS del PEDIDO.
    public void cargarEstados(){
        cbxEstado.removeAllItems();

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                    "SELECT id_estado,nombre_estado " +
                    "FROM Estado_Pedido"
            );

            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                cbxEstado.addItem(new ItemCombo(rs.getInt("id_estado"), rs.getString("nombre_estado")));
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null, ex.toString());}
    }
    
    // Método para CARGAR los PRODUCTOS.
    public void cargarProductos(){
        cbxProducto.removeAllItems();

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                    "SELECT id_producto,nombre " +
                    "FROM Producto " +
                    "ORDER BY nombre"
            );
            
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                cbxProducto.addItem(new ItemCombo(rs.getInt("id_producto"), rs.getString("nombre")));
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,ex.toString());}
    }
    
    // Método para CARGAR el PRODUCTO-PEDIDO.
    public void cargarProductosPedido(){
        DefaultTableModel modelo= (DefaultTableModel) tblProductosdelPedido.getModel();
        modelo.setRowCount(0);

        if(idPedido==-1) return;

        try{
            Connection con=ConexionSQLServer.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(
                "SELECT " +
                "P.id_producto," + // Inclusión de la ID para que no haya problemas en los nombres :P
                "P.nombre," +
                "D.cantidad," +
                "D.precio_unitario " +
                "FROM Detalle_Pedido D " +
                "INNER JOIN Producto P " +
                "ON D.id_producto=P.id_producto " +
                "WHERE D.id_pedido=?"
            );

            ps.setInt(1,idPedido);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                modelo.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario")
                });
            }
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null,ex.toString());}
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbxEstado = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        cbxCliente = new javax.swing.JComboBox<>();
        cbxMesero = new javax.swing.JComboBox<>();
        ftfFecha = new javax.swing.JFormattedTextField();
        ftfHora = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtObservacion = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btnAsociar = new javax.swing.JButton();
        btnQuitar = new javax.swing.JButton();
        cbxProducto = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPedidos = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProductosdelPedido = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(875, 655));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REGISTRAR PEDIDOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setText("Cliente:");

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel2.setText("Mesero:");

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel3.setText("Fecha:");

        jLabel4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel4.setText("Hora:");

        jLabel5.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel5.setText("Estado:");

        cbxEstado.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxEstado.setModel(new DefaultComboBoxModel<ItemCombo>());

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

        cbxCliente.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxCliente.setModel(new DefaultComboBoxModel<ItemCombo>());

        cbxMesero.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxMesero.setModel(new DefaultComboBoxModel<ItemCombo>());

        ftfFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yy-MM-dd"))));
        ftfFecha.setToolTipText("Ejem: 25-09-31");
        ftfFecha.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        ftfHora.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("h:mm:ss"))));
        ftfHora.setToolTipText("Ejem: 08:09:20");
        ftfHora.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(ftfHora, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(cbxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(cbxMesero, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(cbxMesero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(ftfFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(ftfHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(cbxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DETALLES DEL PEDIDO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel6.setText("Producto:");

        jLabel7.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel7.setText("Cantidad:");

        txtCantidad.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel10.setText("Observación:");

        txtObservacion.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

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

        cbxProducto.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        cbxProducto.setModel(new DefaultComboBoxModel<ItemCombo>());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cbxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(btnAsociar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cbxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator2))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAsociar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTA DE PEDIDOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblPedidos.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Cliente", "Mesero", "Fecha", "Hora", "Estado", "Total"
            }
        ));
        tblPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPedidosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPedidos);

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
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LSITA DE PRODUCTOS DEL PEDIDO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        tblProductosdelPedido.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        tblProductosdelPedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Producto", "Producto", "Cantidad", "Precio unitario"
            }
        ));
        jScrollPane3.setViewportView(tblProductosdelPedido);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Evento para AGREGAR un PEDIDO con la info. de los campos.  
    private void btnCreateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCreateMouseClicked
        // Validar campos
        if(cbxCliente.getSelectedItem() == null || cbxMesero.getSelectedItem() == null || cbxEstado.getSelectedItem() == null || ftfFecha.getText().isEmpty() || ftfHora.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Complete todos los campos.");
            return;
        }

        try{
            ItemCombo cliente = (ItemCombo) cbxCliente.getSelectedItem();
            ItemCombo mesero = (ItemCombo) cbxMesero.getSelectedItem();
            ItemCombo estado = (ItemCombo) cbxEstado.getSelectedItem();

            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Pedido(fecha,hora,total,id_estado,id_cliente,id_mesero) "
                + "VALUES(?,?,?,?,?,?)"
            );

            java.sql.Date fecha = java.sql.Date.valueOf(ftfFecha.getText());
            java.sql.Time hora = java.sql.Time.valueOf(ftfHora.getText());

            ps.setDate(1, fecha);
            ps.setTime(2, hora);

            // Al inicio el pedido no tiene productos
            ps.setDouble(3,0);

            ps.setInt(4,estado.getId());
            ps.setInt(5,cliente.getId());
            ps.setInt(6,mesero.getId());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Pedido registrado.");

            limpiar();
            cargarTabla();
            ocultarColumnaID();
        }
        catch(Exception ex){JOptionPane.showMessageDialog(null, "ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnCreateMouseClicked

    // Evento para BUSCAR el pedido en la base de datos.
    private void btnReadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadMouseClicked
        buscarPedido();
    }//GEN-LAST:event_btnReadMouseClicked

    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        if(idPedido == -1){
            JOptionPane.showMessageDialog(null, "Seleccione un pedido de la tabla.");
            return;
        }

        try{
            ItemCombo cliente =(ItemCombo)cbxCliente.getSelectedItem();
            ItemCombo mesero = (ItemCombo)cbxMesero.getSelectedItem();
            ItemCombo estado = (ItemCombo)cbxEstado.getSelectedItem();

            Connection con = ConexionSQLServer.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE Pedido SET "
                + "fecha=?,"
                + "hora=?,"
                + "id_estado=?,"
                + "id_cliente=?,"
                + "id_mesero=? "
                + "WHERE id_pedido=?"
            );

            ps.setDate(1, java.sql.Date.valueOf(ftfFecha.getText()));
            ps.setTime(2, java.sql.Time.valueOf(ftfHora.getText()));
            ps.setInt(3, estado.getId());
            ps.setInt(4, cliente.getId());
            ps.setInt(5, mesero.getId());
            ps.setInt(6,idPedido);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Pedido modificado.");

            limpiar();
            cargarTabla();
        }
        catch(Exception ex){JOptionPane.showMessageDialog(null, "ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnUpdateMouseClicked
    
    // Evento para ELIMINAR un pedido de la base de datos.
    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if(idPedido == -1){
            JOptionPane.showMessageDialog(null,"Seleccione un pedido.");
            return;
        }

        try{
            Connection con = ConexionSQLServer.obtenerConexion();

            // Primero eliminar detalles
            PreparedStatement ps1 = con.prepareStatement(
                "DELETE FROM Detalle_Pedido "
                + "WHERE id_pedido=?"
            );

            ps1.setInt(1,idPedido);
            ps1.executeUpdate();

            // Luego eliminar pedido
            PreparedStatement ps2 =con.prepareStatement(
                "DELETE FROM Pedido "
                + "WHERE id_pedido=?"
            );

            ps2.setInt(1,idPedido);
            ps2.executeUpdate();
            JOptionPane.showMessageDialog(null,"Pedido eliminado.");

            limpiar();
            cargarTabla();
            ((DefaultTableModel) tblProductosdelPedido.getModel()).setRowCount(0);
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null, "ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnDeleteMouseClicked

    // Evento para ASOCIAR los PRODUCTOS al PEDIDO.
    private void btnAsociarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAsociarMouseClicked
        // Validar que exista un pedido seleccionado
        if(idPedido == -1){
            JOptionPane.showMessageDialog(null,"Seleccione primero un pedido.");
            return;
        }

        // Validar cantidad
        if(txtCantidad.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Ingrese una cantidad.");
            return;
        }

        int cantidad;

        try{
            cantidad = Integer.parseInt(txtCantidad.getText());
            if(cantidad <= 0){JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a cero.");
                return;
            }

        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Ingrese una cantidad válida.");
            return;
        }

        // Obtener producto seleccionado
        ItemCombo producto = (ItemCombo)cbxProducto.getSelectedItem();
        if(producto == null){
            JOptionPane.showMessageDialog(null, "No hay productos registrados.");
            return;
        }

        int idProducto = producto.getId();

        try{
            Connection con = ConexionSQLServer.obtenerConexion();
            // VALIDAR DUPLICADO
            PreparedStatement validar = con.prepareStatement(
                "SELECT COUNT(*) "
                + "FROM Detalle_Pedido "
                + "WHERE id_pedido=? "
                + "AND id_producto=?"

            );

            validar.setInt(1,idPedido);
            validar.setInt(2,idProducto);
            ResultSet rs = validar.executeQuery();

            if(rs.next() && rs.getInt(1)>0){
                JOptionPane.showMessageDialog(null, "Ese producto ya está agregado al pedido.");
                return;
            }

            // Obtener precio actual del producto
            PreparedStatement precio = con.prepareStatement(
                "SELECT precio "
                + "FROM Producto "
                + "WHERE id_producto=?"
            );

            precio.setInt(1,idProducto);
            ResultSet rsp = precio.executeQuery();

            double precioUnitario=0;

            if(rsp.next()){precioUnitario = rsp.getDouble("precio");}
            else{
                JOptionPane.showMessageDialog(null, "No se encontró el producto.");
                return;
            }

            // Insertar detalle
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Detalle_Pedido"
                +"(id_pedido,id_producto,cantidad,precio_unitario,observacion)"
                +" VALUES(?,?,?,?,?)"
            );

            ps.setInt(1,idPedido);
            ps.setInt(2,idProducto);
            ps.setInt(3,cantidad);
            ps.setDouble(4,precioUnitario);

            if(txtObservacion.getText().isEmpty()){ps.setNull(5,java.sql.Types.VARCHAR);
            }
            else{ps.setString(5, txtObservacion.getText());}

            ps.executeUpdate();
            actualizarTotalPedido();

            JOptionPane.showMessageDialog(null, "Producto agregado al pedido.");

            txtCantidad.setText("");
            txtObservacion.setText("");
            cargarProductosPedido();
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null, "ERROR: "+ex.toString());}
    }//GEN-LAST:event_btnAsociarMouseClicked

    // Evento del botón para QUITAR un PRODUCTO de un PEDIDO
    private void btnQuitarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuitarMouseClicked
        if(idPedido==-1){
            JOptionPane.showMessageDialog(null, "Seleccione un pedido.");
            return;
        }

        int fila = tblProductosdelPedido.getSelectedRow();

        if(fila==-1){
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la lista.");
            return;
        }

        // Obtener directamente el ID del producto (columna oculta)
        int idProducto = Integer.parseInt(tblProductosdelPedido.getValueAt(fila,0).toString());

        try{
            Connection con = ConexionSQLServer.obtenerConexion();

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM Detalle_Pedido "
                +"WHERE id_pedido=? "
                +"AND id_producto=?"
            );

            ps.setInt(1,idPedido);
            ps.setInt(2,idProducto);
            ps.executeUpdate();
            
            actualizarTotalPedido();
            
            JOptionPane.showMessageDialog(null, "Producto eliminado.");
            cargarProductosPedido();          
        }
        catch(SQLException ex){JOptionPane.showMessageDialog(null, ex.toString());}
    }//GEN-LAST:event_btnQuitarMouseClicked

    // Evento para CARGAR un PEDIDO en los campos.
    private void tblPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPedidosMouseClicked
        int fila = tblPedidos.getSelectedRow();

        if(fila>=0){
            idPedido = Integer.parseInt(tblPedidos.getValueAt(fila,0).toString());
            cbxCliente.setSelectedItem(buscarItem(cbxCliente,tblPedidos.getValueAt(fila,1).toString()));
            cbxMesero.setSelectedItem(buscarItem(cbxMesero,tblPedidos.getValueAt(fila,2).toString()));
            cbxEstado.setSelectedItem(buscarItem(cbxEstado,tblPedidos.getValueAt(fila,5).toString()));
            ftfFecha.setText(tblPedidos.getValueAt(fila,3).toString());
            ftfHora.setText(tblPedidos.getValueAt(fila,4).toString());

            cargarProductosPedido();
        }
    }//GEN-LAST:event_tblPedidosMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnAsociar;
    public javax.swing.JButton btnCreate;
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnQuitar;
    public javax.swing.JButton btnRead;
    public javax.swing.JButton btnUpdate;
    public javax.swing.JComboBox<ItemCombo> cbxCliente;
    public javax.swing.JComboBox<ItemCombo> cbxEstado;
    public javax.swing.JComboBox<ItemCombo> cbxMesero;
    public javax.swing.JComboBox<ItemCombo> cbxProducto;
    public javax.swing.JFormattedTextField ftfFecha;
    public javax.swing.JFormattedTextField ftfHora;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public javax.swing.JTable tblPedidos;
    public javax.swing.JTable tblProductosdelPedido;
    public javax.swing.JTextField txtCantidad;
    public javax.swing.JTextField txtObservacion;
    // End of variables declaration//GEN-END:variables
}