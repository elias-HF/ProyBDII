
package formularios;

import java.awt.CardLayout; // Importar el 'CardLayout' para poder hacer el cambio entre JPanel's.
// Importar Font y UIManager para poder realizar cambios en las cabezeras de las tablas.
import java.awt.Font;
import javax.swing.UIManager;

public class VentanaPrincipal extends javax.swing.JFrame {
    
    private CardLayout card; // Declaración de Layout.
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    // Declaración para los JPanel's.
    private InicioPanel inicioPanel;
    private DashboardPanel dashboardPanel;
    private FrmCliente clientePanel;
    private FrmFactura facturaPanel;
    private FrmInsumo insumoPanel;
    private FrmMovInventario movInventarioPanel;
    private FrmPago pagoPanel;
    private FrmPedido pedidoPanel;
    private FrmProducto productoPanel;
    
    // PARTE IMPORTANTE: Código del constructor.
    public VentanaPrincipal() {
        initComponents();
        setLocationRelativeTo(null); // Centrar la ventana
        
        // Configuración del CardLayout.
        card = new CardLayout();
        PanelContenedor.setLayout(card);
        
        // Crear los paneles una sola vez.
        inicioPanel = new InicioPanel();
        dashboardPanel = new DashboardPanel();
        clientePanel = new FrmCliente();
        facturaPanel = new FrmFactura();
        insumoPanel = new FrmInsumo();
        movInventarioPanel = new FrmMovInventario();
        pagoPanel = new FrmPago();
        pedidoPanel = new FrmPedido();
        productoPanel = new FrmProducto();
        
        // Agregar los JPanel's al contenedor.
        PanelContenedor.add(inicioPanel, "INICIO");
        PanelContenedor.add(dashboardPanel,"DASHBOARD");
        PanelContenedor.add(clientePanel, "CLIENTE");
        PanelContenedor.add(facturaPanel, "FACTURA");
        PanelContenedor.add(insumoPanel, "INSUMO");
        PanelContenedor.add(movInventarioPanel, "MOVIMIENTO INVENTARIO");
        PanelContenedor.add(pagoPanel, "PAGO");
        PanelContenedor.add(pedidoPanel, "PEDIDO");
        PanelContenedor.add(productoPanel, "PRODUCTO");
        
        // El primer JPanel mostrado por defecto: clientePanel.
        card.show(PanelContenedor, "INICIO");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelContenedor = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenu9 = new javax.swing.JMenu();
        jMenu10 = new javax.swing.JMenu();
        jMenu11 = new javax.swing.JMenu();
        jMenu13 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema Gestor del Restaurante");

        PanelContenedor.setPreferredSize(new java.awt.Dimension(889, 708));

        javax.swing.GroupLayout PanelContenedorLayout = new javax.swing.GroupLayout(PanelContenedor);
        PanelContenedor.setLayout(PanelContenedorLayout);
        PanelContenedorLayout.setHorizontalGroup(
            PanelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 889, Short.MAX_VALUE)
        );
        PanelContenedorLayout.setVerticalGroup(
            PanelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 698, Short.MAX_VALUE)
        );

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuBar1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N

        jMenu1.setText("Inicio");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Dashboard");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Cliente");
        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu3MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu3);

        jMenu5.setText("Factura");
        jMenu5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu5MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu5);

        jMenu6.setText("Insumo");
        jMenu6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu6MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu6);

        jMenu9.setText("Mov. de inventario");
        jMenu9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu9MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu9);

        jMenu10.setText("Pago");
        jMenu10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu10MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu10);

        jMenu11.setText("Pedido");
        jMenu11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu11MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu11);

        jMenu13.setText("Producto");
        jMenu13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu13MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu13);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // PARTE: Código de acciones para mostrar los JPanel's en la 'VentanaPrincipal'.
    private void jMenu3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu3MouseClicked
        card.show(PanelContenedor, "CLIENTE");
    }//GEN-LAST:event_jMenu3MouseClicked

    private void jMenu5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu5MouseClicked
        card.show(PanelContenedor, "FACTURA");
    }//GEN-LAST:event_jMenu5MouseClicked

    private void jMenu6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu6MouseClicked
        card.show(PanelContenedor, "INSUMO");
    }//GEN-LAST:event_jMenu6MouseClicked

    private void jMenu9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu9MouseClicked
        card.show(PanelContenedor, "MOVIMIENTO INVENTARIO");
    }//GEN-LAST:event_jMenu9MouseClicked

    private void jMenu10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu10MouseClicked
        card.show(PanelContenedor, "PAGO");
    }//GEN-LAST:event_jMenu10MouseClicked

    private void jMenu11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu11MouseClicked
        card.show(PanelContenedor, "PEDIDO");
    }//GEN-LAST:event_jMenu11MouseClicked

    private void jMenu13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu13MouseClicked
        card.show(PanelContenedor, "PRODUCTO");
    }//GEN-LAST:event_jMenu13MouseClicked

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        card.show(PanelContenedor, "INICIO");
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        card.show(PanelContenedor, "DASHBOARD");
    }//GEN-LAST:event_jMenu2MouseClicked

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        // Cambio de fuente para las cabezeras de las tablas.
        UIManager.put("Table.font", new Font("Consolas", Font.BOLD, 12));
        UIManager.put("TableHeader.font", new Font("Consolas", Font.BOLD, 12));
        
        java.awt.EventQueue.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelContenedor;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
}