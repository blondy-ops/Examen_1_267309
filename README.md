# Registrar cita médica (MVC + Observer)

## Estructura
src/
├── app/Main.java                       Arma modelo, control y vistas; suscribe las vistas
├── modelo/                             MODELO
│   ├── ModeloCitas.java                «Subject»: IModel + IObservable (reglas de horario)
│   ├── IModel / IObservable / IObservador / EstadoRegistro
│   └── entidades/                      Paciente, Medico, Cita (30 min), HorarioConsulta, enums
├── controlador/                        CONTROL
└── vista/                              VISTA («boundary», IObservador)
    ├── BuscadorNSS.java                Login
    └── ApartarCita.java                Médicos + selector de fecha (LGoodDatePicker) + horas libres

## Dependencia
LGoodDatePicker 11.2.1 (Maven Central: com.github.lgooddatepicker:LGoodDatePicker).

### Opción A: Maven (recomendada; IntelliJ / NetBeans / Eclipse abren el pom.xml directamente)
    mvn compile exec:java

### Opción B: sin Maven (descargando el jar)
Descarga el jar desde Maven Central y colócalo en lib/:
    https://repo1.maven.org/maven2/com/github/lgooddatepicker/LGoodDatePicker/11.2.1/LGoodDatePicker-11.2.1.jar

Linux / macOS:
    javac -encoding UTF-8 -cp lib/LGoodDatePicker-11.2.1.jar -d out $(find src -name "*.java")
    java -cp out:lib/LGoodDatePicker-11.2.1.jar app.Main

Windows (PowerShell):
    javac -encoding UTF-8 -cp lib\LGoodDatePicker-11.2.1.jar -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
    java -cp "out;lib\LGoodDatePicker-11.2.1.jar" app.Main

## Datos de prueba
NSS válidos: 12345678901 (Juan Perez), 98765432109 (Maria Lopez)
Médicos: Carlos Ramirez (Lun/Mié/Vie 9-13), Ana Torres (Mar/Jue 10-14),
         Luis Mendoza (Lun-Vie 15-19), Sofia Herrera (Sáb 8-12, Mar 16-20)
