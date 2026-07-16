
package formularios;

import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
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


public class DashboardRealTime extends javax.swing.JFrame {
    // Fechas por defecto ejm: todo el año 2026
    private String fechaInicioFiltro = "2026-01-01";
    private String fechaFinFiltro = "2026-12-31";
    private String anioSeleccionado = "2026";
    private int contadorEncuestas = 0;

    public DashboardRealTime() {
        // LLAMAR SIEMPRE PRIMERO al inicializador de NetBeans
        initComponents(); 

        //  Configuraciones de la ventana 
        setTitle("Dashboard de Satisfacción");
        this.setResizable(false);

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

    
    
    
    
    private void iniciarEscuchaTiempoReal() {
        // Thread para que la ventana (JFrame) no se congele mientras escucha la BD
        new Thread(() -> {
        // cadena de conexión de MongoDB Atlas 
        String connectionString = "mongodb+srv://eliashuaringa244_db_user:daXgsII4Fy9ICjaj@cluster0.tswulag.mongodb.net/?retryWrites=true&w=majority";
        
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("restauranteBD");
            MongoCollection<Document> collection = database.getCollection("encuestas");

            System.out.println("Escuchando cambios en tiempo real en MongoDB Atlas...");

            try (var cursor = collection.watch().iterator()) {
                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> changeDocument = cursor.next();

                    if (changeDocument.getOperationType().toString().equals("INSERT")) {
                        SwingUtilities.invokeLater(() -> {
                            actualizarDashboard();
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el hilo de escucha en tiempo real: " + e.getMessage());
            e.printStackTrace();
        }
    }).start();
    }
    
    private void actualizarDashboard() {
        new Thread(() -> {
        String connectionString = "mongodb+srv://eliashuaringa244_db_user:daXgsII4Fy9ICjaj@cluster0.tswulag.mongodb.net/?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("restauranteBD");
            MongoCollection<Document> collection = database.getCollection("encuestas");

            // Obtenemos la cantidad real de documentos directamente desde Atlas
            long total = collection.countDocuments();
            
            // Actualizamos la GUI de forma segura
            SwingUtilities.invokeLater(() -> {
                lblTotalEncuestas.setText("Total Encuestas: " + total);
                
                mostrarGraficoLinea(panelGraficoLinea);
                mostrarGraficoBarras(panelGraficoBarras);
                mostrarTablaComentarios(tablaComentarios);
                actualizarCardsPromedios(lblPromedioAtencion, lblPromedioComida, lblPromedioCalidad);
                
                System.out.println("Dashboard y graficos refrescados  Total real en Atlas: " + total);
            });
            
        } catch (Exception e) {
            System.err.println("Error al obtener conteo del Dashboard: " + e.getMessage());
        }
        }).start();
    }
    
    public void aplicarFiltrosYActualizar() {
    // 1. Componentes de MongoDB (Pasamos las fechas para filtrar las encuestas si lo deseas)
        mostrarGraficoLinea(panelGraficoLinea); 
        mostrarGraficoBarras(panelGraficoBarras);
        mostrarTablaComentarios(tablaComentarios);
        actualizarCardsPromedios(lblPromedioAtencion, lblPromedioComida, lblPromedioCalidad);

        // 2Componentes de SQL 
        actualizarCardIngresos(lblIngresosTotales);
        mostrarRendimientoMeseros(panelMeseros);
        mostrarPlatillosMasVendidos(panelPlatillos);
        mostrarEstadoPedidos(panelEstadoPedidos);
        mostrarIngresosTemporales(panelIngresosLineas);
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

        String connectionString = "mongodb+srv://eliashuaringa244_db_user:daXgsII4Fy9ICjaj@cluster0.tswulag.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("restauranteBD");
            MongoCollection<Document> collection = database.getCollection("encuestas");
            //campo calidad - frescura
            List<Bson> pipeline = Arrays.asList(
                Aggregates.group("$marca_temporal", Accumulators.avg("promedioCalidad", "$calidad.frescura_mariscos")),
                Aggregates.sort(Sorts.ascending("_id"))
            );

            int datosCargados = 0;
            for (Document doc : collection.aggregate(pipeline)) {
                String fechaCompleta = doc.getString("_id"); 
                Object promedioObj = doc.get("promedioCalidad");

                if (fechaCompleta != null && promedioObj != null) {
                    Double promedio = ((Number) promedioObj).doubleValue();

                    // Limpiamos la fecha para mostrar solo "DD/MM" en el eje X
                    String fechaCorta = fechaCompleta.split(" ")[0]; 
                    if (fechaCorta.length() >= 5) {
                        fechaCorta = fechaCorta.substring(0, 5); 
                    }

                    dataset.addValue(promedio, "Frescura y Textura", fechaCorta);
                    datosCargados++;
                }
            }

            System.out.println("Puntos de datos cargados con éxito (Frescura): " + datosCargados);

        } catch (Exception e) {
            System.err.println("Error al cargar datos del gráfico: " + e.getMessage());
            e.printStackTrace();
        }


        JFreeChart chart = ChartFactory.createLineChart(
                "Frescura y Textura de Pescados/Mariscos", // Título 
                "Fecha",                       
                "Calificación Promedio",           
                dataset, 
                PlotOrientation.VERTICAL,
                false, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        // Ajuste dinámico de tamaño para que se vea grande en tu JPanel de NetBeans
        chartPanel.setPreferredSize(new java.awt.Dimension(panelContenedor.getWidth(), panelContenedor.getHeight()));
        chartPanel.setSize(panelContenedor.getWidth(), panelContenedor.getHeight());

        panelContenedor.removeAll();
        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelContenedor.add(chartPanel, java.awt.BorderLayout.CENTER);

        panelContenedor.revalidate();
        panelContenedor.repaint();
}
    public void mostrarGraficoBarras(javax.swing.JPanel panelContenedor) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String connectionString = "mongodb+srv://eliashuaringa244_db_user:daXgsII4Fy9ICjaj@cluster0.tswulag.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("restauranteBD");
            MongoCollection<Document> collection = database.getCollection("encuestas");

            // Agrupamos todas las encuestas y calculamos el promedio de los tres aspectos de infraestructura
            List<Bson> pipeline = Arrays.asList(
                Aggregates.group(null, 
                    Accumulators.avg("promedioBanos", "$infraestructura.higiene_banos"),
                    Accumulators.avg("promedioAmbiente", "$infraestructura.ambiente_fresco"),
                    Accumulators.avg("promedioVajilla", "$infraestructura.vajilla_impecable")
                )
            );

            Document resultado = collection.aggregate(pipeline).first();

            if (resultado != null) {
                // Obtenemos los promedios calculados de forma segura
                Double promedioBanos = resultado.get("promedioBanos") != null ? ((Number) resultado.get("promedioBanos")).doubleValue() : 0.0;
                Double promedioAmbiente = resultado.get("promedioAmbiente") != null ? ((Number) resultado.get("promedioAmbiente")).doubleValue() : 0.0;
                Double promedioVajilla = resultado.get("promedioVajilla") != null ? ((Number) resultado.get("promedioVajilla")).doubleValue() : 0.0;

                // Agregamos los datos al dataset del gráfico (Valor, Serie/Leyenda, Categoría en Eje X)
                dataset.addValue(promedioBanos, "Higiene", "Servicios Higiénicos");
                dataset.addValue(promedioAmbiente, "Ambiente", "Ambiente Fresco");
                dataset.addValue(promedioVajilla, "Presentación", "Vajilla y Cubiertos");
            } else {
                // Datos de respaldo por si la base de datos está vacía
                dataset.addValue(0.0, "Higiene", "Servicios Higiénicos");
                dataset.addValue(0.0, "Ambiente", "Ambiente Fresco");
                dataset.addValue(0.0, "Presentación", "Vajilla y Cubiertos");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar datos del gráfico de barras: " + e.getMessage());
            e.printStackTrace();
        }

        // --- RENDERIZADO DEL GRÁFICO DE BARRAS ---
        JFreeChart chart = ChartFactory.createBarChart(
                "Puntuación de Infraestructura y Limpieza", // Título del gráfico
                "Áreas Evaluadas",                          // Etiqueta del eje X
                "Calificación Promedio",                    // Etiqueta del eje Y
                dataset,                                    // Conjunto de datos
                PlotOrientation.VERTICAL,                   // Orientación de las barras
                true,                                       // ¿Mostrar leyenda de series?
                true,                                       // Tooltips
                false                                       // URLs
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        // Forzamos el ajuste del tamaño para que ocupe todo el panel asignado en la GUI
        chartPanel.setPreferredSize(new java.awt.Dimension(panelContenedor.getWidth(), panelContenedor.getHeight()));
        chartPanel.setSize(panelContenedor.getWidth(), panelContenedor.getHeight());

        panelContenedor.removeAll();
        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelContenedor.add(chartPanel, java.awt.BorderLayout.CENTER);

        panelContenedor.revalidate();
        panelContenedor.repaint();
    }
    public void mostrarTablaComentarios(javax.swing.JTable tabla) {
        // 1. Obtener el modelo de la tabla y limpiarlo por completo
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); 

        try {
            // 2. Conectar a MongoDB (Ajusta si usas otra forma de obtener la DB)
            MongoDatabase db = dashBoard.utilities.ConexionMongo.getDatabase(); 
            MongoCollection<Document> coleccion = db.getCollection("encuestas"); // Nombre de tu colección

            // 3. Crear el filtro de rango de fechas para MongoDB
            // Convertimos los strings "YYYY-MM-DD" a Instantes UTC para que coincidan con la BD
            Instant inicio = Instant.parse(fechaInicioFiltro + "T00:00:00Z");
            Instant fin = Instant.parse(fechaFinFiltro + "T23:59:59Z");

            Bson filtroFechasYComentarios = Filters.and(
                Filters.gte("fecha_formulario", Date.from(inicio)),
                Filters.lte("fecha_formulario", Date.from(fin)),
                Filters.ne("comentario", "") // Que el comentario no esté vacío
            );

            // 4. Consultar y ordenar de manera descendente (los más recientes primero)
            for (Document doc : coleccion.find(filtroFechasYComentarios).sort(Sorts.descending("fecha_formulario"))) {

                // Extraer fecha del formulario
                Date fechaForm = doc.getDate("fecha_formulario");
                String fechaStr = (fechaForm != null) ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaForm) : "";

                // Extraer las puntuaciones embebidas (ejemplo: atencion.amabilidad_cortesia)
                Document atencion = (Document) doc.get("atencion");
                int amabilidad = (atencion != null) ? atencion.getInteger("amabilidad_cortesia", 0) : 0;

                Document calidad = (Document) doc.get("calidad");
                int calidadComida = (calidad != null) ? calidad.getInteger("sabor_sazon_balance", 0) : 0;

                // Extraer el comentario
                String comentario = doc.getString("comentario");

                // Agregar la fila al modelo de tu JTable
                modelo.addRow(new Object[]{fechaStr, amabilidad, calidadComida, comentario});
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardRealTime().setVisible(true);
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button1 = new java.awt.Button();
        lblTotalEncuestas = new javax.swing.JLabel();
        panelGraficoLinea = new javax.swing.JPanel();
        panelGraficoBarras = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaComentarios = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblPromedioComida = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblPromedioAtencion = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblPromedioCalidad = new javax.swing.JLabel();
        btnEnero = new java.awt.Button();
        btnFebrero = new java.awt.Button();
        btnMarzo = new java.awt.Button();
        btnMayo = new java.awt.Button();
        btnJunio = new java.awt.Button();
        btnJulio = new java.awt.Button();
        btnNoviembre = new java.awt.Button();
        btnOctubre = new java.awt.Button();
        btnSeptiembre = new java.awt.Button();
        btnAbril = new java.awt.Button();
        btnAgosto = new java.awt.Button();
        btnDiciembre = new java.awt.Button();
        comboAnio = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblIngresosTotales = new javax.swing.JLabel();
        panelMeseros = new javax.swing.JPanel();
        panelPlatillos = new javax.swing.JPanel();
        panelEstadoPedidos = new javax.swing.JPanel();
        panelIngresosLineas = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DashBoard");

        button1.setActionCommand("button1");
        button1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
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

        lblTotalEncuestas.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTotalEncuestas.setText("Total Encuestas:");

        panelGraficoLinea.setBackground(new java.awt.Color(255, 255, 255));
        panelGraficoLinea.setMaximumSize(new java.awt.Dimension(400, 400));

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

        panelGraficoBarras.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelGraficoBarrasLayout = new javax.swing.GroupLayout(panelGraficoBarras);
        panelGraficoBarras.setLayout(panelGraficoBarrasLayout);
        panelGraficoBarrasLayout.setHorizontalGroup(
            panelGraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 525, Short.MAX_VALUE)
        );
        panelGraficoBarrasLayout.setVerticalGroup(
            panelGraficoBarrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 211, Short.MAX_VALUE)
        );

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
        jScrollPane1.setViewportView(tablaComentarios);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jPanel1.setForeground(new java.awt.Color(153, 255, 255));

        lblPromedioComida.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblPromedioComida.setText("Comida");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPromedioComida)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPromedioComida)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255)));

        lblPromedioAtencion.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblPromedioAtencion.setText("Atencion");

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
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPromedioAtencion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 255)));

        lblPromedioCalidad.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblPromedioCalidad.setText("Calidad A... ");

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

        btnEnero.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnEnero.setLabel("Enero");
        btnEnero.setMinimumSize(new java.awt.Dimension(97, 25));
        btnEnero.setPreferredSize(new java.awt.Dimension(97, 25));
        btnEnero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEneroActionPerformed(evt);
            }
        });

        btnFebrero.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnFebrero.setLabel("Febrero");
        btnFebrero.setPreferredSize(new java.awt.Dimension(97, 25));
        btnFebrero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFebreroActionPerformed(evt);
            }
        });

        btnMarzo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnMarzo.setLabel("Marzo");
        btnMarzo.setPreferredSize(new java.awt.Dimension(97, 25));
        btnMarzo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarzoActionPerformed(evt);
            }
        });

        btnMayo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnMayo.setLabel("Mayo");
        btnMayo.setPreferredSize(new java.awt.Dimension(97, 25));
        btnMayo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMayoActionPerformed(evt);
            }
        });

        btnJunio.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnJunio.setLabel("junio");
        btnJunio.setPreferredSize(new java.awt.Dimension(97, 25));
        btnJunio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJunioActionPerformed(evt);
            }
        });

        btnJulio.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnJulio.setLabel("Julio");
        btnJulio.setPreferredSize(new java.awt.Dimension(97, 25));
        btnJulio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJulioActionPerformed(evt);
            }
        });

        btnNoviembre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnNoviembre.setLabel("Noviembre");
        btnNoviembre.setPreferredSize(new java.awt.Dimension(97, 25));
        btnNoviembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoviembreActionPerformed(evt);
            }
        });

        btnOctubre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnOctubre.setLabel("Octubre");
        btnOctubre.setPreferredSize(new java.awt.Dimension(97, 25));
        btnOctubre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOctubreActionPerformed(evt);
            }
        });

        btnSeptiembre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnSeptiembre.setLabel("Septiembre");
        btnSeptiembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeptiembreActionPerformed(evt);
            }
        });

        btnAbril.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnAbril.setLabel("Abril");
        btnAbril.setPreferredSize(new java.awt.Dimension(97, 25));
        btnAbril.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrilActionPerformed(evt);
            }
        });

        btnAgosto.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnAgosto.setLabel("Agosto");
        btnAgosto.setPreferredSize(new java.awt.Dimension(97, 25));
        btnAgosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgostoActionPerformed(evt);
            }
        });

        btnDiciembre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnDiciembre.setLabel("Diciembre");
        btnDiciembre.setPreferredSize(new java.awt.Dimension(97, 25));
        btnDiciembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiciembreActionPerformed(evt);
            }
        });

        comboAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026", "2027", "2028" }));
        comboAnio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAnioActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Selector de Año");

        lblIngresosTotales.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblIngresosTotales.setText("Ingresos");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIngresosTotales)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIngresosTotales)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelMeseros.setBackground(new java.awt.Color(255, 255, 255));
        panelMeseros.setMaximumSize(new java.awt.Dimension(382, 100));
        panelMeseros.setMinimumSize(new java.awt.Dimension(382, 100));
        panelMeseros.setPreferredSize(new java.awt.Dimension(382, 100));
        panelMeseros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        panelEstadoPedidos.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelEstadoPedidosLayout = new javax.swing.GroupLayout(panelEstadoPedidos);
        panelEstadoPedidos.setLayout(panelEstadoPedidosLayout);
        panelEstadoPedidosLayout.setHorizontalGroup(
            panelEstadoPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
        );
        panelEstadoPedidosLayout.setVerticalGroup(
            panelEstadoPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        panelIngresosLineas.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelIngresosLineasLayout = new javax.swing.GroupLayout(panelIngresosLineas);
        panelIngresosLineas.setLayout(panelIngresosLineasLayout);
        panelIngresosLineasLayout.setHorizontalGroup(
            panelIngresosLineasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelIngresosLineasLayout.setVerticalGroup(
            panelIngresosLineasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 125, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalEncuestas)
                            .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnMayo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnJunio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnJulio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnAgosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(btnSeptiembre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnOctubre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnNoviembre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnDiciembre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnEnero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnFebrero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnMarzo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnAbril, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelMeseros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelPlatillos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(comboAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(panelEstadoPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(panelIngresosLineas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(12, 12, 12)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelGraficoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelGraficoLinea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalEncuestas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEnero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFebrero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMarzo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAbril, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMayo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnJunio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnJulio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSeptiembre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnOctubre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNoviembre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDiciembre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addComponent(panelEstadoPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelMeseros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(15, 15, 15)))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelPlatillos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(panelIngresosLineas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panelGraficoLinea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGraficoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        button1.getAccessibleContext().setAccessibleDescription("");

        pack();
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
    }//GEN-LAST:event_btnJunioActionPerformed

    private void btnJulioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJulioActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-07-01";
        fechaFinFiltro = anioSeleccionado + "-07-31";
    }//GEN-LAST:event_btnJulioActionPerformed

    private void btnNoviembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoviembreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-11-01";
        fechaFinFiltro = anioSeleccionado + "-11-30";
    }//GEN-LAST:event_btnNoviembreActionPerformed

    private void btnOctubreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOctubreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-10-01";
        fechaFinFiltro = anioSeleccionado + "-10-31";
    }//GEN-LAST:event_btnOctubreActionPerformed

    private void btnAbrilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrilActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-04-01";
        fechaFinFiltro = anioSeleccionado + "-04-30";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnAbrilActionPerformed

    private void btnAgostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgostoActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-08-01";
        fechaFinFiltro = anioSeleccionado + "-08-31";
    }//GEN-LAST:event_btnAgostoActionPerformed

    private void btnDiciembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiciembreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-11-01";
        fechaFinFiltro = anioSeleccionado + "-11-31";
    }//GEN-LAST:event_btnDiciembreActionPerformed

    private void btnEneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEneroActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-01-01";
        fechaFinFiltro = anioSeleccionado + "-01-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnEneroActionPerformed

    private void btnMayoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMayoActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-05-01";
        fechaFinFiltro = anioSeleccionado + "-03-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_btnMayoActionPerformed

    private void btnSeptiembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeptiembreActionPerformed
        fechaInicioFiltro = anioSeleccionado + "-09-01";
        fechaFinFiltro = anioSeleccionado + "-09-30";
    }//GEN-LAST:event_btnSeptiembreActionPerformed

    private void comboAnioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAnioActionPerformed
        anioSeleccionado = comboAnio.getSelectedItem().toString();
        // Al cambiar de año, por defecto mostramos el año completo hasta que presionen un mes
        fechaInicioFiltro = anioSeleccionado + "-01-01";
        fechaFinFiltro = anioSeleccionado + "-12-31";
        aplicarFiltrosYActualizar();
    }//GEN-LAST:event_comboAnioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button btnAbril;
    private java.awt.Button btnAgosto;
    private java.awt.Button btnDiciembre;
    private java.awt.Button btnEnero;
    private java.awt.Button btnFebrero;
    private java.awt.Button btnJulio;
    private java.awt.Button btnJunio;
    private java.awt.Button btnMarzo;
    private java.awt.Button btnMayo;
    private java.awt.Button btnNoviembre;
    private java.awt.Button btnOctubre;
    private java.awt.Button btnSeptiembre;
    private java.awt.Button button1;
    private javax.swing.JComboBox<String> comboAnio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblIngresosTotales;
    private javax.swing.JLabel lblPromedioAtencion;
    private javax.swing.JLabel lblPromedioCalidad;
    private javax.swing.JLabel lblPromedioComida;
    private javax.swing.JLabel lblTotalEncuestas;
    private javax.swing.JPanel panelEstadoPedidos;
    private javax.swing.JPanel panelGraficoBarras;
    private javax.swing.JPanel panelGraficoLinea;
    private javax.swing.JPanel panelIngresosLineas;
    private javax.swing.JPanel panelMeseros;
    private javax.swing.JPanel panelPlatillos;
    private javax.swing.JTable tablaComentarios;
    // End of variables declaration//GEN-END:variables
}
