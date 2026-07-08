
package Utilidades;

public class ItemCombo {
    private int id;
    private String nombre;

    public ItemCombo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {return id;}

    @Override
    public String toString() {return nombre;}
}