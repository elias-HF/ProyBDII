
package formularios;

// Importar los recursos necesarios del javafx
import java.awt.BorderLayout;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class DashboardPanel extends javax.swing.JPanel {

    // Declaración de variables
    private JFXPanel jfxPanel;
    private WebView webView;
    private WebEngine webEngine;
    
    public DashboardPanel() {
        initComponents();
        
        iniciarWebView();
    }

    // Creamos el método para que se muestre el dashboard
    private void iniciarWebView(){
        jfxPanel = new JFXPanel();
        panelDashboard.setLayout(new BorderLayout());
        panelDashboard.add(jfxPanel, BorderLayout.CENTER);
        
        // Inicializar JavaFX
        Platform.runLater(() -> {
            // Crear el navegador
            webView = new WebView();
            webEngine = webView.getEngine();
            
            // Crear la escena
            Scene escena = new Scene(webView);
            jfxPanel.setScene(escena);
            
            webEngine.load("https://app.powerbi.com/view?r=eyJrIjoiZDI1OGU3ODMtOWUxZC00YjE2LWFiYjQtNTFjOWUzZmMwMTczIiwidCI6ImM0YTY2YzM0LTJiYjctNDUxZi04YmUxLWIyYzI2YTQzMDE1OCIsImMiOjR9");
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelDashboard = new javax.swing.JPanel();

        panelDashboard.setPreferredSize(new java.awt.Dimension(875, 875));

        javax.swing.GroupLayout panelDashboardLayout = new javax.swing.GroupLayout(panelDashboard);
        panelDashboard.setLayout(panelDashboardLayout);
        panelDashboardLayout.setHorizontalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 875, Short.MAX_VALUE)
        );
        panelDashboardLayout.setVerticalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 655, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelDashboard;
    // End of variables declaration//GEN-END:variables
}