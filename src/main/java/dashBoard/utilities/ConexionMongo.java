
package dashBoard.utilities;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongo {
    // Tu cadena de conexión real a MongoDB Atlas
    private static final String CONNECTION_STRING = "mongodb+srv://eliashuaringa244_db_user:daXgsII4Fy9ICjaj@cluster0.tswulag.mongodb.net";
    private static final String DATABASE_NAME = "restauranteBD";
    
    // Instancias estáticas para el Patrón Singleton
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    // 1. Constructor privado para evitar que se creen instancias con "new"
    private ConexionMongo() {}

    // 2. Método global para obtener la base de datos de manera segura
    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                System.out.println("Intentando conectar a MongoDB Atlas...");
                // Crea el cliente de MongoDB solo la primera vez que se llama
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("¡Conexión exitosa a la base de datos: " + DATABASE_NAME + "!");
            } catch (Exception e) {
                System.err.println("Error crítico al conectar a MongoDB: " + e.getMessage());
            }
        }
        return database;
    }

    // 3. Método para cerrar la conexión limpiamente al salir de la aplicación
    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                database = null;
                mongoClient = null;
                System.out.println("Conexión a MongoDB cerrada correctamente.");
            } catch (Exception e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
