package modelo.entidades;

public class Paciente {
    private final String numeroSS;
    private final int edad;
    private final String nombre;
    private final Genero genero;

    public Paciente(String numeroSS, String nombre, int edad, Genero genero) {
        this.numeroSS = numeroSS;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
    }

    public String getNumeroSS() { return numeroSS; }
    public int getEdad() { return edad; }
    public String getNombre() { return nombre; }
    public Genero getGenero() { return genero; }
}
