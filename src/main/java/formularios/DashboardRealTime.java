
package formularios;


import javax.swing.SwingUtilities;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import dashBoard.utilities.SincronizarEncuestasDesdeGoogle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import static mssql.googlecode.concurrentlinkedhashmap.Weighers.collection;
import org.bson.conversions.Bson;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class DashboardRealTime extends javax.swing.JPanel {
    // Fechas por defecto ejm: todo el año 2026
    private String fechaInicioFiltro = "2026-01-01";
    private String fechaFinFiltro = "2026-12-31";
    private String anioSeleccionado = "2026";
    private int contadorEncuestas = 0;

    public DashboardRealTime() {
        // LLAMAR SIEMPRE PRIMERO al inicializador de NetBeans
        initComponents(); 

        //  Configuraciones de la ventana 

        // Carga el total real de registros que ya están en la BD
        actualizarDashboard();

        // Iniciar el hilo de base de datos
        iniciarEscuchaTiempoReal();
        
        aplicarFiltrosYActualizar();
    }
    //para el filtro de las fechas.
    Bson filtroFechas = Filters.and(
    Filters.gte("fecha_formulario", Instant.parse(fechaInicioFiltro + "T00:00:00Z")),
    Filters.lte("fecha_formulario", Instant.parse(fechaFinFiltro + "T23:59:59Z"))
    );
    
    Bson filtroFechasYComentarios = Filters.and(
    Filters.gte("fecha_formulario", Instant.parse(fechaInicioFiltro + "T00:00:00Z")),
    Filters.lte("fecha_formulario", Instant.parse(fechaFinFiltro + "T23:59:59Z")),
    Filters.ne("comentario", "")
    );

    
    
    
    
    public void iniciarEscuchaTiempoReal() {
        
        new Thread(() -> {
            try {
                MongoDatabase db = dashBoard.utilities.ConexionMongo.getDatabase();
                MongoCollection<Document> coleccion = db.getCollection("encuestas");

                System.out.println("Escuchando cambios en tiempo real en MongoDB Atlas...");


                for (com.mongodb.client.model.changestream.ChangeStreamDocument<Document> change : coleccion.watch()) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        actualizarDashboard();
                    });
                }

            } catch (Exception e) {
                System.err.println("Error en la escucha en tiempo real: " + e.getMessage());
            }
        }).start(); 
    }
    
    private void actualizarDashboard() {
        // Todo el proceso de red corre de fondo en un solo hilo
        new Thread(() -> {
            try {

                MongoDatabase database = dashBoard.utilities.ConexionMongo.getDatabase();
                MongoCollection<Document> collection = database.getCollection("encuestas");


                long total = collection.countDocuments();

                SwingUtilities.invokeLater(() -> {
                    lblTotalEncuestas.setText("Total Encuestas: " + total);

                    mostrarGraficoLinea(panelGraficoLinea);
                    mostrarGraficoBarras(panelGraficoBarras);
                    mostrarTablaComentarios(tablaComentarios);
                    actualizarCardsPromedios(lblPromedioAtencion, lblPromedioComida, lblPromedioCalidad);

                    System.out.println("Dashboard visual refrescado con éxito.");
                });

            } catch (Exception e) {
                System.err.println("Error al obtener datos en segundo plano: " + e.getMessage());
            }
        }).start();
    }
    
    public void aplicarFiltrosYActualizar() {
        new Thread(() -> {
            try {
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    mostrarGraficoLinea(panelGraficoLinea); 
                    mostrarGraficoBarras(panelGraficoBarras);
                    mostrarTablaComentarios(tablaComentarios);
                    actualizarCardsPromedios(lblPromedioAtencion, lblPromedioComida, lblPromedioCalidad);

                    actualizarCardIngresos(lblIngresosTotales);
                    mostrarRendimientoMeseros(panelMeseros);
                    mostrarPlatillosMasVendidos(panelPlatillos);
                    mostrarEstadoPedidos(panelEstadoPedidos);
                    mostrarIngresosTemporales(panelIngresosLineas);
                });
            } catch (Exception e) {
                System.err.println("Error al aplicar filtros: " + e.getMessage());
            }
        }).start();
    }
    
    //parte SQL Server
    public void actualizarCardIngresos(javax.swing.JLabel lblIngresos) {
        String sql = "SELECT SUM(total) as ingresos_totales FROM Pedido WHERE fecha BETWEEN ? AND ?";

        try (Connection con = Conexion.ConexionSQLServer.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicioFiltro);
            ps.setString(2, fechaFinFiltro);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("ingresos_totales");
                lblIngresos.setText(String.format(java.util.Locale.US, "S/ %.2f", total));
            } else {
                lblIngresos.setText("S/ 0.00");
            }
        } catch (Exception e) {
            System.err.println("Error en Card Ingresos: " + e.getMessage());
        }
    }
    public void mostrarRendimientoMeseros(javax.swing.JPanel panelMeseros) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT TOP 5 m.nombre, SUM(p.total) as ingresos_totales " +
                        "FROM Pedido p " +
                        "JOIN Mesero m ON p.id_mesero = m.id_mesero " +
                        "WHERE p.fecha BETWEEN ? AND ? " +
                        "GROUP BY m.nombre " +
                        "ORDER BY ingresos_totales DESC";

        try (Connection con = Conexion.ConexionSQLServer.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicioFiltro);
            ps.setString(2, fechaFinFiltro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dataset.addValue(rs.getDouble("ingresos_totales"), "Ingresos", rs.getString("nombre"));
            }
        } catch (Exception e) {
            System.err.println("Error en Rendimiento Meseros: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Rendimiento de los Meseros", "Mesero", "S/.", 
                dataset, PlotOrientation.HORIZONTAL, false, true, false
        );
        ajustarYMostrarGrafico(chart, panelMeseros);
    }
    public void mostrarPlatillosMasVendidos(javax.swing.JPanel panelPlatillos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT TOP 5 pr.nombre, SUM(dp.cantidad) as total_vendido " +
                "FROM Detalle_Pedido dp " +
                "JOIN Producto pr ON dp.id_producto = pr.id_producto " +
                "JOIN Pedido pe ON dp.id_pedido = pe.id_pedido " +
                "WHERE pe.fecha BETWEEN ? AND ? " +
                "GROUP BY pr.nombre " +
                "ORDER BY total_vendido DESC";

        try (Connection con = Conexion.ConexionSQLServer.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicioFiltro);
            ps.setString(2, fechaFinFiltro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dataset.addValue(rs.getDouble("total_vendido"), "Unidades", rs.getString("nombre"));
            }
        } catch (Exception e) {
            System.err.println("Error en Platillos: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Platillos más vendidos", "Platillo", "Cantidad", 
                dataset, PlotOrientation.HORIZONTAL, false, true, false
        );
        ajustarYMostrarGrafico(chart, panelPlatillos);
    }
    public void mostrarEstadoPedidos(javax.swing.JPanel panelEstadoPedidos) {
        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();

        String sql = "SELECT ep.nombre_estado, COUNT(*) as cantidad " +
                     "FROM Pedido p " +
                     "JOIN Estado_Pedido ep ON p.id_estado = ep.id_estado " +
                     "WHERE p.fecha BETWEEN ? AND ? " +
                     "GROUP BY ep.nombre_estado";

        try (Connection con = Conexion.ConexionSQLServer.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicioFiltro);
            ps.setString(2, fechaFinFiltro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dataset.setValue(rs.getString("nombre_estado"), rs.getInt("cantidad"));
            }
        } catch (Exception e) {
            System.err.println("Error en Estado Pedidos: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Estados de los Pedidos", dataset, true, true, false
        );
        ajustarYMostrarGrafico(chart, panelEstadoPedidos);
    }
    public void mostrarIngresosTemporales(javax.swing.JPanel panelIngresosLineas) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT fecha, SUM(total) as venta_diaria " +
                     "FROM Pedido " +
                     "WHERE fecha BETWEEN ? AND ? " +
                     "GROUP BY fecha " +
                     "ORDER BY fecha ASC";

        try (Connection con = Conexion.ConexionSQLServer.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicioFiltro);
            ps.setString(2, fechaFinFiltro);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fechaStr = rs.getDate("fecha").toString(); 
                dataset.addValue(rs.getDouble("venta_diaria"), "Ingresos (S/.)", fechaStr);
            }
        } catch (Exception e) {
            System.err.println("Error en Ingresos Temporales: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Ingresos por Meses y Días", "Fecha", "Monto", 
                dataset, PlotOrientation.VERTICAL, false, true, false
        );
        ajustarYMostrarGrafico(chart, panelIngresosLineas);
    }
        //metodo auxiliar para una mejor renderizacion.
    private void ajustarYMostrarGrafico(JFreeChart chart, javax.swing.JPanel panelContenedor) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(panelContenedor.getWidth(), panelContenedor.getHeight()));
        chartPanel.setSize(panelContenedor.getWidth(), panelContenedor.getHeight());

        panelContenedor.removeAll();
        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelContenedor.add(chartPanel, java.awt.BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }
    
    
    //parte mongoDB
    public void mostrarGraficoLinea(javax.swing.JPanel panelContenedor) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    try {
            MongoDatabase database = dashBoard.utilities.ConexionMongo.getDatabase();
            MongoCollection<Document> collection = database.getCollection("encuestas");

            // 1. Convertir rango de fechas a objetos Date (UTC) para MongoDB
            Instant inicio = Instant.parse(fechaInicioFiltro + "T00:00:00Z");
            Instant fin = Instant.parse(fechaFinFiltro + "T23:59:59Z");

            // 2. Crear filtros: rango de fechas y que el campo no esté vacío
            Bson filtroFechas = Filters.and(
                Filters.gte("fecha_formulario", Date.from(inicio)),
                Filters.lte("fecha_formulario", Date.from(fin))
            );

            // 3. Pipeline de agregación con el filtro incorporado
            List<Bson> pipeline = Arrays.asList(
                Aggregates.match(filtroFechas), // <--- FILTRO DE FECHA APLICADO
                Aggregates.group("$marca_temporal", 
                    Accumulators.avg("promedioCalidad", "$calidad.frescura_mariscos")
                ),
                Aggregates.sort(Sorts.ascending("_id"))
            );

            for (Document doc : collection.aggregate(pipeline)) {
                String fecha = doc.getString("_id");
                // Extraer solo el día y mes "DD/MM" de la marca temporal para que el eje X no se amontone
                String fechaCorta = (fecha != null && fecha.length() >= 5) ? fecha.substring(0, 5) : "Sin fecha";

                Double promedio = doc.get("promedioCalidad") != null ? ((Number) doc.get("promedioCalidad")).doubleValue() : 0.0;
                dataset.addValue(promedio, "Calidad Alimentos", fechaCorta);
            }

        } catch (Exception e) {
            System.err.println("Error al cargar gráfico de línea de Mongo: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Calidad Alimentos por Fecha", 
                "Fecha", "Calificación Promedio", 
                dataset, PlotOrientation.VERTICAL, false, true, false
        );

        ajustarYMostrarGrafico(chart, panelContenedor);
    }
    public void mostrarGraficoBarras(javax.swing.JPanel panelContenedor) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            MongoDatabase database = dashBoard.utilities.ConexionMongo.getDatabase();
            MongoCollection<Document> collection = database.getCollection("encuestas");

            //  Convertir fechas
            Instant inicio = Instant.parse(fechaInicioFiltro + "T00:00:00Z");
            Instant fin = Instant.parse(fechaFinFiltro + "T23:59:59Z");

            Bson filtroFechas = Filters.and(
                Filters.gte("fecha_formulario", Date.from(inicio)),
                Filters.lte("fecha_formulario", Date.from(fin))
            );

            // Ejecutar la agregación con filtro
            List<Bson> pipeline = Arrays.asList(
                Aggregates.match(filtroFechas), // <--- FILTRO DE FECHA APLICADO
                Aggregates.group(null, 
                    Accumulators.avg("promHigiene", "$infraestructura.higiene_banos"),
                    Accumulators.avg("promAmbiente", "$infraestructura.ambiente_fresco"),
                    Accumulators.avg("promPresentacion", "$infraestructura.vajilla_impecable")
                )
            );

            Document resultado = collection.aggregate(pipeline).first();

            if (resultado != null) {
                Double promHigiene = resultado.get("promHigiene") != null ? ((Number) resultado.get("promHigiene")).doubleValue() : 0.0;
                Double promAmbiente = resultado.get("promAmbiente") != null ? ((Number) resultado.get("promAmbiente")).doubleValue() : 0.0;
                Double promPresentacion = resultado.get("promPresentacion") != null ? ((Number) resultado.get("promPresentacion")).doubleValue() : 0.0;

                // Agregamos los datos estructurados al dataset
                dataset.addValue(promHigiene, "Higiene", "Servicios Higiénicos");
                dataset.addValue(promAmbiente, "Ambiente", "Ambiente Fresco");
                dataset.addValue(promPresentacion, "Presentación", "Vajilla y Cubiertos");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar gráfico de barras de Mongo: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Puntuación de Infraestructura y Limpieza", 
                "Áreas Evaluadas", "Calificación Promedio", 
                dataset, PlotOrientation.VERTICAL, true, true, false
        );

        ajustarYMostrarGrafico(chart, panelContenedor);
    }
    public void mostrarTablaComentarios(javax.swing.JTable tabla) {
        // Obtener el modelo de la tabla y limpiarlo por completo
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); 
        
        try {
            // Conectar a MongoDB
            MongoDatabase db = dashBoard.utilities.ConexionMongo.getDatabase(); 
            MongoCollection<Document> coleccion = db.getCollection("encuestas"); 

            // Convertimos los strings "YYYY-MM-DD" a Instantes UTC
            Instant inicio = Instant.parse(fechaInicioFiltro + "T00:00:00Z");
            Instant fin = Instant.parse(fechaFinFiltro + "T23:59:59Z");

            Bson filtroFechasYComentarios = Filters.and(
                Filters.gte("fecha_formulario", Date.from(inicio)),
                Filters.lte("fecha_formulario", Date.from(fin)),
                Filters.ne("comentario", "") // Que el comentario no esté vacío
            );

            // Consultar y ordenar de manera descendente
            for (Document doc : coleccion.find(filtroFechasYComentarios).sort(Sorts.descending("fecha_formulario"))) {
                if (doc != null) { // Cambiado 'while' por una simple verificación 'if'
                    // Extraer fecha del formulario
                    Date fechaForm = doc.getDate("fecha_formulario");
                    String fechaStr = (fechaForm != null) ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaForm) : "";

                    // Extraer las puntuaciones
                    Document atencion = (Document) doc.get("atencion");
                    int amabilidad = (atencion != null) ? atencion.getInteger("amabilidad_cortesia", 0) : 0;

                    Document calidad = (Document) doc.get("calidad");
                    int calidadComida = (calidad != null) ? calidad.getInteger("sabor_sazon_balance", 0) : 0;

                    // Extraer comentario
                    String comentario = doc.getString("comentario");

                    // Agregamos la fila de manera segura
                    modelo.addRow(new Object[]{fechaStr, amabilidad, calidadComida, comentario});
                    
                    String ids [] = {"Fecha","Amabilidad","Calidad Comida","Comentario"};
                    modelo.setColumnIdentifiers(ids);
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar comentarios filtrados: " + e.getMessage());
        }
    }
    public void actualizarCardsPromedios(javax.swing.JLabel lblAtencion, javax.swing.JLabel lblComida, javax.swing.JLabel lblCalidad) {

        String connectionString = "mongodb+srv://eliashuaringa244_db_user:daXgsII4Fy9ICjaj@cluster0.tswulag.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("restauranteBD");
            MongoCollection<Document> collection = database.getCollection("encuestas");

            // Agrupamos en MongoDB Atlas y promediamos las 3 preguntas exactas
            List<Bson> pipeline = Arrays.asList(
                Aggregates.group(null, 
                    Accumulators.avg("promAtencion", "$atencion.amabilidad_cortesia"),
                    Accumulators.avg("promComida", "$calidad.sabor_sazon_balance"),
                    Accumulators.avg("promCalidad", "$calidad.frescura_mariscos")
                )
            );

            Document resultado = collection.aggregate(pipeline).first();

            if (resultado != null) {
                Double promAtencion = resultado.get("promAtencion") != null ? ((Number) resultado.get("promAtencion")).doubleValue() : 0.0;
                Double promComida = resultado.get("promComida") != null ? ((Number) resultado.get("promComida")).doubleValue() : 0.0;
                Double promCalidad = resultado.get("promCalidad") != null ? ((Number) resultado.get("promCalidad")).doubleValue() : 0.0;

                // Formateamos y colocamos los números en las etiquetas de tus tarjetas
                lblAtencion.setText(String.format(Locale.US, "%.1f", promAtencion));
                lblComida.setText(String.format(Locale.US, "%.1f", promComida));
                lblCalidad.setText(String.format(Locale.US, "%.1f", promCalidad));
            } else {
                lblAtencion.setText("0.0");
                lblComida.setText("0.0");
                lblCalidad.setText("0.0");
            }

        } catch (Exception e) {
            System.err.println("Error al calcular los promedios de las cards: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
 
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        comboAnio = new javax.swing.JComboBox<>();
        panelGraficoLinea = new javax.swing.JPanel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        lblPromedioAtencion = new javax.swing.JLabel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        lblPromedioComida = new javax.swing.JLabel();
        java.awt.Button button1 = new java.awt.Button();
        panelGraficoBarras = new javax.swing.JPanel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        lblPromedioCalidad = new javax.swing.JLabel();
        panelMeseros = new javax.swing.JPanel();
        panelPlatillos = new javax.swing.JPanel();
        panelEstadoPedidos = new javax.swing.JPanel();
        panelIngresosLineas = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        tablaComentarios = new javax.swing.JTable();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        lblIngresosTotales = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        lblTotalEncuestas = new javax.swing.JLabel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        java.awt.Button btnEnero = new java.awt.Button();
        java.awt.Button btnFebrero = new java.awt.Button();
        java.awt.Button btnMayo = new java.awt.Button();
        java.awt.Button btnJunio = new java.awt.Button();
        java.awt.Button btnJulio = new java.awt.Button();
        java.awt.Button btnNoviembre = new java.awt.Button();
        java.awt.Button btnOctubre = new java.awt.Button();
        java.awt.Button btnSeptiembre = new java.awt.Button();
        java.awt.Button btnAbril = new java.awt.Button();
        java.awt.Button btnAgosto = new java.awt.Button();
        java.awt.Button btnDiciembre = new java.awt.Button();
        java.awt.Button btnMarzo = new java.awt.Button();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel8 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel7 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 51, 153));
        jLabel4.setText("Alimentos");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 150, -1, -1));

        comboAnio.setBackground(new java.awt.Color(0, 102, 204));
        comboAnio.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        comboAnio.setForeground(new java.awt.Color(255, 255, 255));
        comboAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026", "2027", "2028" }));
        comboAnio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAnioActionPerformed(evt);
            }
        });
        jPanel5.add(comboAnio, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, -1, -1));

        panelGraficoLinea.setBackground(new java.awt.Color(255, 255, 255));
        panelGraficoLinea.setMaximumSize(new java.awt.Dimension(525, 195));
        panelGraficoLinea.setMinimumSize(new java.awt.Dimension(525, 195));

        javax.swing.GroupLayout panelGraficoLineaLayout = new javax.swing.GroupLayout(panelGraficoLinea);
        panelGraficoLinea.setLayout(panelGraficoLineaLayout);
        panelGraficoLineaLayout.setHorizontalGroup(
            panelGraficoLineaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 525, Short.MAX_VALUE)
        );
        panelGraficoLineaLayout.setVerticalGroup(
            panelGraficoLineaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 195, Short.MAX_VALUE)
        );

        jPanel5.add(panelGraficoLinea, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 200, -1, -1));

        jPanel2.setBackground(new java.awt.Color(153, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255)));
        jPanel2.setForeground(new java.awt.Color(153, 255, 255));

        lblPromedioAtencion.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        lblPromedioAtencion.setForeground(new java.awt.Color(0, 0, 204));
        lblPromedioAtencion.setText("A");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPromedioAtencion)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPromedioAtencion)
                .addContainerGap())
        );

        jPanel5.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 60, -1, -1));

        jPanel1.setBackground(new java.awt.Color(153, 255, 255));
        jPanel1.setForeground(new java.awt.Color(153, 255, 255));

        lblPromedioComida.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        lblPromedioComida.setForeground(new java.awt.Color(0, 0, 204));
        lblPromedioComida.setText("C");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPromedioComida)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPromedioComida)
                .addContainerGap())
        );

        jPanel5.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 60, -1, -1));

        button1.setActionCommand("button1");
        button1.setBackground(new java.awt.Color(0, 153, 204));
        button1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        button1.setForeground(new java.awt.Color(255, 255, 255));
        button1.setLabel("Sincronizar Registros");
        button1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button1MouseClicked(evt);
            }
        });
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });
        jPanel5.add(button1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 42, -1, -1));
        button1.getAccessibleContext().setAccessibleDescription("");

        panelGraficoBarras.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelGraficoBarrasLayout = new javax.swing.GroupLayout(panelGraficoBarras);
        panelGraficoBarras.setLayout(panelGraficoBarrasLayout);
        panelGraficoBarrasLayout.setHorizontalGroup(
            panelGraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 525, Short.MAX_VALUE)
        );
        panelGraficoBarrasLayout.setVerticalGroup(
            panelGraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel5.add(panelGraficoBarras, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 410, -1, 340));

        jPanel3.setBackground(new java.awt.Color(153, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255)));

        lblPromedioCalidad.setBackground(new java.awt.Color(153, 255, 255));
        lblPromedioCalidad.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        lblPromedioCalidad.setForeground(new java.awt.Color(0, 0, 204));
        lblPromedioCalidad.setText("C.A ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPromedioCalidad)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPromedioCalidad)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 60, -1, -1));

        panelMeseros.setBackground(new java.awt.Color(255, 255, 255));
        panelMeseros.setMaximumSize(new java.awt.Dimension(382, 100));
        panelMeseros.setMinimumSize(new java.awt.Dimension(382, 100));
        panelMeseros.setPreferredSize(new java.awt.Dimension(382, 100));
        panelMeseros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel5.add(panelMeseros, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 50, -1, 160));

        panelPlatillos.setBackground(new java.awt.Color(255, 255, 255));
        panelPlatillos.setMaximumSize(new java.awt.Dimension(382, 167));
        panelPlatillos.setMinimumSize(new java.awt.Dimension(382, 167));

        javax.swing.GroupLayout panelPlatillosLayout = new javax.swing.GroupLayout(panelPlatillos);
        panelPlatillos.setLayout(panelPlatillosLayout);
        panelPlatillosLayout.setHorizontalGroup(
            panelPlatillosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        panelPlatillosLayout.setVerticalGroup(
            panelPlatillosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 167, Short.MAX_VALUE)
        );

        jPanel5.add(panelPlatillos, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 210, -1, -1));

        panelEstadoPedidos.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelEstadoPedidosLayout = new javax.swing.GroupLayout(panelEstadoPedidos);
        panelEstadoPedidos.setLayout(panelEstadoPedidosLayout);
        panelEstadoPedidosLayout.setHorizontalGroup(
            panelEstadoPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );
        panelEstadoPedidosLayout.setVerticalGroup(
            panelEstadoPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        jPanel5.add(panelEstadoPedidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 270, -1));

        panelIngresosLineas.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelIngresosLineasLayout = new javax.swing.GroupLayout(panelIngresosLineas);
        panelIngresosLineas.setLayout(panelIngresosLineasLayout);
        panelIngresosLineasLayout.setHorizontalGroup(
            panelIngresosLineasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelIngresosLineasLayout.setVerticalGroup(
            panelIngresosLineasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
        );

        jPanel5.add(panelIngresosLineas, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 380, 535, 170));

        jScrollPane2.setBackground(new java.awt.Color(204, 255, 255));

        tablaComentarios.setBackground(new java.awt.Color(204, 255, 255));
        tablaComentarios.setForeground(new java.awt.Color(0, 51, 153));
        tablaComentarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tablaComentarios);

        jPanel5.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 550, 800, 211));

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        lblIngresosTotales.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lblIngresosTotales.setForeground(new java.awt.Color(51, 153, 255));
        lblIngresosTotales.setText("Ingresos");

        jLabel2.setForeground(new java.awt.Color(0, 153, 255));
        jLabel2.setText("Ingresos Totales");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(109, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(105, 105, 105))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblIngresosTotales)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 62, Short.MAX_VALUE)
                .addComponent(jLabel2))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(lblIngresosTotales)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 300, 80));

        lblTotalEncuestas.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTotalEncuestas.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalEncuestas.setText("Total Encuestas:");
        jPanel5.add(lblTotalEncuestas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Metricas de las Encuestas");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 10, -1, -1));
        jPanel5.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 20, -1, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 204));
        jLabel1.setText("Selector de Año");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 179, -1, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 51, 153));
        jLabel3.setText("Comida");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 150, -1, -1));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 153));
        jLabel5.setText("Calidad Alimentos");
        jPanel5.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 150, -1, -1));

        btnEnero.setBackground(new java.awt.Color(0, 102, 204));
        btnEnero.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnEnero.setForeground(new java.awt.Color(255, 255, 255));
        btnEnero.setLabel("Enero");
        btnEnero.setMinimumSize(new java.awt.Dimension(97, 25));
        btnEnero.setPreferredSize(new java.awt.Dimension(97, 25));
        btnEnero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEneroActionPerformed(evt);
            }
        });
        jPanel5.add(btnEnero, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 77, -1, -1));

        btnFebrero.setBackground(new java.awt.Color(0, 102, 204));
        btnFebrero.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnFebrero.setForeground(new java.awt.Color(255, 255, 255));
        btnFebrero.setLabel("Febrero");
        btnFebrero.setPreferredSize(new java.awt.Dimension(97, 25));
        btnFebrero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFebreroActionPerformed(evt);
            }
        });
        jPanel5.add(btnFebrero, new org.netbeans.lib.awtextra.AbsoluteConstraints(107, 77, -1, -1));

        btnMayo.setBackground(new java.awt.Color(0, 102, 204));
        btnMayo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnMayo.setForeground(new java.awt.Color(255, 255, 255));
        btnMayo.setLabel("Mayo");
        btnMayo.setPreferredSize(new java.awt.Dimension(97, 25));
        btnMayo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMayoActionPerformed(evt);
            }
        });
        jPanel5.add(btnMayo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 112, -1, -1));

        btnJunio.setBackground(new java.awt.Color(0, 102, 204));
        btnJunio.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnJunio.setForeground(new java.awt.Color(255, 255, 255));
        btnJunio.setLabel("junio");
        btnJunio.setPreferredSize(new java.awt.Dimension(97, 25));
        btnJunio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJunioActionPerformed(evt);
            }
        });
        jPanel5.add(btnJunio, new org.netbeans.lib.awtextra.AbsoluteConstraints(107, 112, -1, -1));

        btnJulio.setBackground(new java.awt.Color(0, 102, 204));
        btnJulio.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnJulio.setForeground(new java.awt.Color(255, 255, 255));
        btnJulio.setLabel("Julio");
        btnJulio.setPreferredSize(new java.awt.Dimension(97, 25));
        btnJulio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJulioActionPerformed(evt);
            }
        });
        jPanel5.add(btnJulio, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 112, -1, -1));

        btnNoviembre.setBackground(new java.awt.Color(0, 102, 204));
        btnNoviembre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnNoviembre.setForeground(new java.awt.Color(255, 255, 255));
        btnNoviembre.setLabel("Noviembre");
        btnNoviembre.setPreferredSize(new java.awt.Dimension(97, 25));
        btnNoviembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoviembreActionPerformed(evt);
            }
        });
        jPanel5.add(btnNoviembre, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 147, -1, -1));

        btnOctubre.setBackground(new java.awt.Color(0, 102, 204));
        btnOctubre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnOctubre.setForeground(new java.awt.Color(255, 255, 255));
        btnOctubre.setLabel("Octubre");
        btnOctubre.setPreferredSize(new java.awt.Dimension(97, 25));
        btnOctubre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOctubreActionPerformed(evt);
            }
        });
        jPanel5.add(btnOctubre, new org.netbeans.lib.awtextra.AbsoluteConstraints(107, 147, -1, -1));

        btnSeptiembre.setBackground(new java.awt.Color(0, 102, 204));
        btnSeptiembre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnSeptiembre.setForeground(new java.awt.Color(255, 255, 255));
        btnSeptiembre.setLabel("Septiembre");
        btnSeptiembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeptiembreActionPerformed(evt);
            }
        });
        jPanel5.add(btnSeptiembre, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 147, -1, -1));

        btnAbril.setBackground(new java.awt.Color(0, 102, 204));
        btnAbril.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnAbril.setForeground(new java.awt.Color(255, 255, 255));
        btnAbril.setLabel("Abril");
        btnAbril.setPreferredSize(new java.awt.Dimension(97, 25));
        btnAbril.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrilActionPerformed(evt);
            }
        });
        jPanel5.add(btnAbril, new org.netbeans.lib.awtextra.AbsoluteConstraints(321, 77, -1, -1));

        btnAgosto.setBackground(new java.awt.Color(0, 102, 204));
        btnAgosto.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnAgosto.setForeground(new java.awt.Color(255, 255, 255));
        btnAgosto.setLabel("Agosto");
        btnAgosto.setPreferredSize(new java.awt.Dimension(97, 25));
        btnAgosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgostoActionPerformed(evt);
            }
        });
        jPanel5.add(btnAgosto, new org.netbeans.lib.awtextra.AbsoluteConstraints(321, 112, -1, -1));

        btnDiciembre.setBackground(new java.awt.Color(0, 102, 204));
        btnDiciembre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnDiciembre.setForeground(new java.awt.Color(255, 255, 255));
        btnDiciembre.setLabel("Diciembre");
        btnDiciembre.setPreferredSize(new java.awt.Dimension(97, 25));
        btnDiciembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiciembreActionPerformed(evt);
            }
        });
        jPanel5.add(btnDiciembre, new org.netbeans.lib.awtextra.AbsoluteConstraints(321, 147, -1, -1));

        btnMarzo.setBackground(new java.awt.Color(0, 102, 204));
        btnMarzo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnMarzo.setForeground(new java.awt.Color(255, 255, 255));
        btnMarzo.setLabel("Marzo");
        btnMarzo.setPreferredSize(new java.awt.Dimension(97, 25));
        btnMarzo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarzoActionPerformed(evt);
            }
        });
        jPanel5.add(btnMarzo, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 77, -1, -1));

        jPanel6.setBackground(new java.awt.Color(0, 102, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 40));

        jPanel8.setBackground(new java.awt.Color(204, 255, 255));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 430, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 430, 180));

        jPanel7.setBackground(new java.awt.Color(204, 255, 255));
        jPanel7.setForeground(new java.awt.Color(204, 255, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 40, 490, 150));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        SincronizarEncuestasDesdeGoogle.sincronizar();
    
        JOptionPane.showMessageDialog(this, "Sincronización con Google Sheets completada exitosamente.");
        //se fuerza a actualizar el dashboard.
        actualizarDashboard();
    }//GEN-LAST:event_button1ActionPerformed

    private void button1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_button1MouseClicked

    private void btnFebreroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFebreroActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-02-01";
        fechaFinFiltro = anioSeleccionado + "-02-28"; 
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnFebreroActionPerformed

    private void btnMarzoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarzoActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-03-01";
        fechaFinFiltro = anioSeleccionado + "-03-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnMarzoActionPerformed

    private void btnJunioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJunioActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-06-01";
        fechaFinFiltro = anioSeleccionado + "-06-30";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnJunioActionPerformed

    private void btnJulioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJulioActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-07-01";
        fechaFinFiltro = anioSeleccionado + "-07-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnJulioActionPerformed

    private void btnNoviembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoviembreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-11-01";
        fechaFinFiltro = anioSeleccionado + "-11-30";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnNoviembreActionPerformed

    private void btnOctubreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOctubreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-10-01";
        fechaFinFiltro = anioSeleccionado + "-10-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnOctubreActionPerformed

    private void btnAbrilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrilActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-04-01";
        fechaFinFiltro = anioSeleccionado + "-04-30";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnAbrilActionPerformed

    private void btnAgostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgostoActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-08-01";
        fechaFinFiltro = anioSeleccionado + "-08-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnAgostoActionPerformed

    private void btnDiciembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiciembreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-12-01";
        fechaFinFiltro = anioSeleccionado + "-12-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnDiciembreActionPerformed

    private void btnEneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEneroActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-01-01";
        fechaFinFiltro = anioSeleccionado + "-01-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnEneroActionPerformed

    private void btnMayoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMayoActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-05-01";
        fechaFinFiltro = anioSeleccionado + "-05-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnMayoActionPerformed

    private void btnSeptiembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeptiembreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-09-01";
        fechaFinFiltro = anioSeleccionado + "-09-30";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnSeptiembreActionPerformed

    private void comboAnioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAnioActionPerformed
        anioSeleccionado = comboAnio.getSelectedItem().toString();
        // Al cambiar de año, por defecto mostramos el año completo hasta que presionen un mes
        fechaInicioFiltro = anioSeleccionado + "-01-01";
        fechaFinFiltro = anioSeleccionado + "-12-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_comboAnioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox<String> comboAnio;
    javax.swing.JLabel lblIngresosTotales;
    javax.swing.JLabel lblPromedioAtencion;
    javax.swing.JLabel lblPromedioCalidad;
    javax.swing.JLabel lblPromedioComida;
    javax.swing.JLabel lblTotalEncuestas;
    javax.swing.JPanel panelEstadoPedidos;
    javax.swing.JPanel panelGraficoBarras;
    javax.swing.JPanel panelGraficoLinea;
    javax.swing.JPanel panelIngresosLineas;
    javax.swing.JPanel panelMeseros;
    javax.swing.JPanel panelPlatillos;
    javax.swing.JTable tablaComentarios;
    // End of variables declaration//GEN-END:variables
}
