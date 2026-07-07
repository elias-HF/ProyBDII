
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionSQLServer {
    Connection conexion = null;
    String usuario = "sqlUsuario";
    String contraseña = "root";
    String db = "RestauranteBD";
    String ip = "localhost";
    String puerto = "1433";
    
    public static Connection obtenerConexion(){
        String conexionURL = "jdbc:sqlserver://localhost:1433;" + // Si se quiere cambiar a otra maquina se deberá cambiar el localhost por el IP de la misma.
                "database=RestauranteBD;" + 
                "user = sqlUsuario;" + 
                "password=root;" + 
                "loginTimeout=30;" + // Por si la conexion es muy lenta se establece un tiempo límite para la conexión. 
                "trustServerCertificate=true;"; //agregado por si acaso
        try{
            Connection con = DriverManager.getConnection(conexionURL);
            JOptionPane.showMessageDialog(null, "Conexion exitosa");
            return con;
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.toString());
            return null;
        }
    }
}