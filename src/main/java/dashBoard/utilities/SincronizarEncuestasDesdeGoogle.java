
package dashBoard.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class SincronizarEncuestasDesdeGoogle {
    // 2. Variable de la clase (Atributo)
    private static final String URL_CSV = "https://docs.google.com/spreadsheets/d/e/2PACX-1vQEtPqaTG8IdWIQFf2IeEy9GDj_rD6twXRxiKaYJsLxn3kGj9ALZyRmoG77x3_hZlbRjbslSuAZaY0i/pub?output=csv";

    // 3. TODO EL CÓDIGO DEBE ESTAR DENTRO DE ESTE MÉTODO
    public static void sincronizar() {
        try {
            System.out.println("Iniciando descarga de respuestas desde Google Sheets...");
            URL url = new URL(URL_CSV);
            BufferedReader lector = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String linea;
            
            MongoDatabase database = ConexionMongo.getDatabase(); 
            MongoCollection<Document> coleccion = database.getCollection("encuestas");
            
            // Omitir la fila de cabeceras
            lector.readLine(); 
            
            int registrosNuevos = 0;

            while ((linea = lector.readLine()) != null) {
                // Expresión regular para separar por comas respetando los textos con comillas
                String[] columnas = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // Verificamos que tenga al menos las columnas de calificaciones (A hasta J -> mínimo 10 columnas)
                if (columnas.length < 10) continue; 

                // Usamos la marca temporal como identificador único para evitar duplicados
                String marcaTemporal = columnas[0].replace("\"", "").trim();
                
                Document existe = coleccion.find(new Document("marca_temporal", marcaTemporal)).first();
                if (existe != null) {
                    continue; // Si ya existe este registro temporal, lo saltamos
                }

                // Parsear calificaciones de Atención (B, C, D -> Índices 1, 2, 3)
                Document atencion = new Document()
                        .append("amabilidad_cortesia", Integer.parseInt(columnas[1].trim()))
                        .append("tiempo_espera_aceptable", Integer.parseInt(columnas[2].trim()))
                        .append("atencion_necesidades", Integer.parseInt(columnas[3].trim()));

                // Parsear calificaciones de Infraestructura (E, F, G -> Índices 4, 5, 6)
                Document infraestructura = new Document()
                        .append("higiene_banos", Integer.parseInt(columnas[4].trim()))
                        .append("ambiente_fresco", Integer.parseInt(columnas[5].trim()))
                        .append("vajilla_impecable", Integer.parseInt(columnas[6].trim()));

                // Parsear calificaciones de Calidad (H, I, J -> Índices 7, 8, 9)
                Document calidad = new Document()
                        .append("frescura_mariscos", Integer.parseInt(columnas[7].trim()))
                        .append("sabor_sazon_balance", Integer.parseInt(columnas[8].trim()))
                        .append("relacion_calidad_precio", Integer.parseInt(columnas[9].trim()));

                // El comentario es opcional (Columna K -> Índice 10)
                String comentario = "";
                if (columnas.length > 10) {
                    comentario = columnas[10].replace("\"", "").trim();
                }

                // Construimos el documento tal como lo definiste en tu flujo
                Document encuesta = new Document()
                        .append("restaurante", "La Mar")
                        .append("fecha_formulario", new Date()) 
                        .append("marca_temporal", marcaTemporal)
                        .append("comentario", comentario)
                        .append("atencion", atencion)
                        .append("infraestructura", infraestructura)
                        .append("calidad", calidad);

                coleccion.insertOne(encuesta);
                registrosNuevos++;
            }
            
            lector.close();
            System.out.println("Sincronización finalizada con éxito. Registros nuevos: " + registrosNuevos);
            
        } catch (Exception e) {
            System.err.println("Error durante la sincronización: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
