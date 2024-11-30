/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package App;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CRUD extends javax.swing.JPanel {

   private final String filePath = "dutsPersonas.txt";
   private final String eventFilePath = "eventosRegistrados.txt";
   private final LocalDate fechaInicial = LocalDate.of(2023, 1, 1);
   
    public CRUD() {
        initComponents();
        initEventHandlers();
    }
    
     private void initEventHandlers() {
    ButtonCrear.addActionListener(this::crearPersona);
    ButtonEliminar.addActionListener(this::eliminarPersona);
    ButtonLeer.addActionListener(this::guardarPersona);
    ButtonSalir.addActionListener(e -> System.exit(0));
    ButtonMostrar.addActionListener(this::mostrarInformacion);
    ButtonIngresar.addActionListener(this::ingresarPersona);
    ButtonDutsPromedios.addActionListener(this::mostrarPromediosDuts);
    ButtonEnviar.addActionListener(this::transferirDuts);

}
     
private void transferirDuts(ActionEvent e) {
    String remitenteCCInput = JOptionPane.showInputDialog(this, "Ingrese su CC (remitente):");
    String destinatarioCCInput = JOptionPane.showInputDialog(this, "Ingrese el CC del destinatario:");
    String dutsTransferenciaInput = JOptionPane.showInputDialog(this, "Ingrese la cantidad de DUTS a transferir:");

    if (remitenteCCInput != null && destinatarioCCInput != null && dutsTransferenciaInput != null) {
        if (remitenteCCInput.matches("\\d+") && destinatarioCCInput.matches("\\d+") && dutsTransferenciaInput.matches("\\d+")) {
            int remitenteCC = Integer.parseInt(remitenteCCInput);
            int destinatarioCC = Integer.parseInt(destinatarioCCInput);
            int dutsTransferencia = Integer.parseInt(dutsTransferenciaInput);

            if (remitenteCC == destinatarioCC) {
                JOptionPane.showMessageDialog(this, "No puedes transferir DUTS a tu misma cuenta.");
                return;
            }

            List<String> personas = leerArchivo();
            boolean transferenciaExitosa = realizarTransferencia(remitenteCC, destinatarioCC, dutsTransferencia, personas);

            if (transferenciaExitosa) {
                JOptionPane.showMessageDialog(this, "Transferencia de " + dutsTransferencia + " DUTS realizada con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo realizar la transferencia.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ingrese valores válidos para CC y cantidad de DUTS.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Operación cancelada.");
    }
}

private boolean realizarTransferencia(int remitenteCC, int destinatarioCC, int dutsTransferencia, List<String> personas) {
    boolean remitenteEncontrado = false;
    boolean destinatarioEncontrado = false;
    int saldoRemitente = 0;
    int saldoDestinatario = 0;
    List<String> nuevasLineas = new ArrayList<>();

    for (String persona : personas) {
        String[] data = persona.split(",");
        int storedCC = Integer.parseInt(data[0]);
        int dutsActuales = Integer.parseInt(data[2]);

        if (storedCC == remitenteCC) {
            saldoRemitente = dutsActuales;
            if (saldoRemitente >= dutsTransferencia) {
                remitenteEncontrado = true;
                dutsActuales -= dutsTransferencia; // Restar DUTS al remitente
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente para realizar la transferencia.");
                return false;
            }
        }

        if (storedCC == destinatarioCC) {
            destinatarioEncontrado = true;
            dutsActuales += dutsTransferencia; // Sumar DUTS al destinatario
        }

        // Actualizar la línea en el archivo
        nuevasLineas.add(data[0] + "," + data[1] + "," + dutsActuales + "," + data[3]);
    }

    if (remitenteEncontrado && destinatarioEncontrado) {
        // Escribir las nuevas líneas en el archivo
        actualizarArchivo(nuevasLineas);
        return true;
    } else {
        if (!remitenteEncontrado) {
            JOptionPane.showMessageDialog(this, "No se encontró el CC del remitente.");
        }
        if (!destinatarioEncontrado) {
            JOptionPane.showMessageDialog(this, "No se encontró el CC del destinatario.");
        }
        return false;
    }
}

private void actualizarArchivo(List<String> lineas) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        for (String linea : lineas) {
            writer.write(linea);
            writer.newLine();
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al actualizar el archivo.");
    }
}


     
     private void mostrarPromediosDuts(ActionEvent e) {
        String CCInput = JOptionPane.showInputDialog(this, "Ingrese su CC para calcular promedios:");

        if (CCInput != null && CCInput.matches("\\d+")) { // Validar CC
            int CC = Integer.parseInt(CCInput);
            String valorActualStr = devolverValor(CC); // Obtener los DUTS actuales

            if (valorActualStr != null) {
                int dutsActuales = Integer.parseInt(valorActualStr);
                LocalDate fechaActual = LocalDate.now();

                // Calcular promedios
                double promedioSemana = calcularPromedio(dutsActuales, fechaActual, ChronoUnit.WEEKS);
                double promedioMes = calcularPromedio(dutsActuales, fechaActual, ChronoUnit.MONTHS);
                double promedioSemestre = calcularPromedio(dutsActuales, fechaActual, ChronoUnit.MONTHS) / 2;
                double promedioAnio = calcularPromedio(dutsActuales, fechaActual, ChronoUnit.YEARS);

                // Mostrar los promedios al usuario
                String mensaje = String.format(
                    "DUTS Actuales: %d\n" +
                    "Promedio semanal: %.2f\n" +
                    "Promedio mensual: %.2f\n" +
                    "Promedio semestral: %.2f\n" +
                    "Promedio anual: %.2f\n",
                    dutsActuales, promedioSemana, promedioMes, promedioSemestre, promedioAnio
                );
                JOptionPane.showMessageDialog(this, mensaje);
            } else {
                JOptionPane.showMessageDialog(this, "CC no encontrado en el sistema.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número.");
        }
    }
     
     private double calcularPromedio(int dutsActuales, LocalDate fechaActual, ChronoUnit unidad) {
        long tiempoTranscurrido = unidad.between(fechaInicial, fechaActual);
        if (tiempoTranscurrido <= 0) tiempoTranscurrido = 1; // Evitar división por 0
        return (double) dutsActuales / tiempoTranscurrido;
    }
     
     
    private String devolverValor(int cc) {
        List<String> personas = leerArchivo();

        for (String persona : personas) {
            String[] data = persona.split(",");
            int storedCC = Integer.parseInt(data[0]);

            // Verificar si el CC coincide
            if (storedCC == cc) {
                return data[2]; // Devuelve el valor o "duts" de la persona
            }
        }
        return null; // Si no se encuentra la persona, devuelve null
    }
     
     
      private void ingresarPersona(ActionEvent e) {
        String CCInput = JOptionPane.showInputDialog(this, "Ingrese su CC:");
        String contrasenaInput = JOptionPane.showInputDialog(this, "Ingrese su Contraseña:");

        if (CCInput != null && contrasenaInput != null) {
            try {
                int CC = Integer.parseInt(CCInput); // Convertir CC a entero

                // Verificar las credenciales
                if (verificarLogin(CC, contrasenaInput)) {
                    String nombre = devolverNombre(CC); // Obtener el nombre de la persona
                    String valor = devolverValor(CC); // Obtener el valor o "duts" de la persona

                    if (nombre != null && valor != null) {
                        JOptionPane.showMessageDialog(this, "Login exitoso. Bienvenido " + nombre + ". Tienes " + valor + " duts.");
                        mostrarEventosDePersona(CC);
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró el nombre o valor para el CC.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "CC o Contraseña incorrectos.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El CC debe ser un número válido.");
            }
        }
    }

// Método para verificar login
public boolean verificarLogin(int cc, String contrasena) {
        List<String> personas = leerArchivo();

        for (String persona : personas) {
            String[] data = persona.split(",");
            int storedCC = Integer.parseInt(data[0]);
            String storedContraseña = data[3];

            // Verificar si el CC y la contraseña coinciden
            if (storedCC == cc && storedContraseña.equals(contrasena)) {
                return true;  // Login exitoso
            }
        }
        return false; // CC o Contraseña incorrectos
    }


 public String devolverNombre(int cc) {
        List<String> personas = leerArchivo();

        for (String persona : personas) {
            String[] data = persona.split(",");
            int storedCC = Integer.parseInt(data[0]);

            // Verificar si el CC coincide
            if (storedCC == cc) {
                return data[1]; // Devuelve el nombre de la persona
            }
        }
        return null; // Si no se encuentra la persona, devuelve null
    }



private void mostrarEventosDesdeBoton(ActionEvent e) {
    String CCInput = JOptionPane.showInputDialog(this, "Ingrese el CC para mostrar los eventos:");
    
    if (CCInput != null && CCInput.matches("\\d+")) { // Validar que sea un número
        int CC = Integer.parseInt(CCInput);
        mostrarEventosDePersona(CC); // Llamar al método existente para mostrar eventos
    } else {
        JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número.");
    }
}
    
 private void mostrarEventosDePersona(int CC) {
        List<String> eventos = leerEventosDePersona(CC);
        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay eventos registrados para este CC.");
        } else {
            StringBuilder sb = new StringBuilder("Eventos registrados:\n");
            for (String evento : eventos) {
                sb.append(evento).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        }
    }
 
 private List<String> leerEventosDePersona(int CC) {
        List<String> eventos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(eventFilePath))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] data = linea.split(",");
                int storedCC = Integer.parseInt(data[0]);

                if (storedCC == CC) {
                    eventos.add(data[1]); // Agrega el nombre del evento
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo de eventos.");
        }
        return eventos;
    }

    // Método para "Crear Persona"
     private void crearPersona(ActionEvent e) {
        String CCInput = JOptionPane.showInputDialog(this, "Ingrese CC (máximo 10 dígitos):");

        if (CCInput != null && CCInput.matches("\\d{1,10}")) {
            int CC = Integer.parseInt(CCInput);
            String nombre = JOptionPane.showInputDialog(this, "Ingrese Nombre:");
            String valor = JOptionPane.showInputDialog(this, "Ingrese Valor:");
            String contraseña = JOptionPane.showInputDialog(this, "Ingrese Contraseña:");

            if (nombre != null && valor != null && contraseña != null) {
                String nuevaPersona = CC + "," + nombre + "," + valor + "," + contraseña;
                escribirEnArchivo(nuevaPersona);
                JOptionPane.showMessageDialog(this, "Persona creada con éxito.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número de hasta 10 dígitos.");
        }
    }


    // Método para "Eliminar Persona"
    private void eliminarPersona(ActionEvent e) {
        String CCEliminarInput = JOptionPane.showInputDialog(this, "Ingrese el CC de la persona a eliminar:");
        if (CCEliminarInput != null && CCEliminarInput.matches("\\d{1,10}")) {
            int CCEliminar = Integer.parseInt(CCEliminarInput);
            boolean eliminado = eliminarLineaPorId(String.valueOf(CCEliminar)); // Convertir CC a String
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Persona eliminada con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "Persona no encontrada.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número de hasta 10 dígitos.");
        }
    }

    // Método para "Guardar Persona" (leer archivo y mostrar en JOptionPane)
    private void guardarPersona(ActionEvent e) {
    List<String> personas = leerArchivo();
    if (personas.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay personas registradas.");
    } else {
        StringBuilder sb = new StringBuilder("Personas Registradas:\n");
        for (String persona : personas) {
            sb.append(persona).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}


    // Método para "Mostrar Información"
    private void mostrarInformacion(ActionEvent e) {
        List<String> personas = leerArchivo();
        if (personas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay información almacenada.");
        } else {
            StringBuilder sb = new StringBuilder("Información Registrada:\n");
            for (String persona : personas) {
                String[] data = persona.split(",");
                sb.append("CC: ").append(data[0]).append(", Nombre: ").append(data[1])
                        .append(", Valor: ").append(data[2]).append(", Contraseña: ").append(data[3]).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        }
    }


    // Método para escribir en el archivo
    private void escribirEnArchivo(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al escribir en el archivo.");
        }
    }

    // Método para leer todo el archivo
    private List<String> leerArchivo() {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo.");
        }
        return lineas;
    }

    // Método para eliminar una línea del archivo por CC
    private boolean eliminarLineaPorId(String CC) {
        List<String> lineas = leerArchivo();
        boolean encontrado = false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String linea : lineas) {
                if (!linea.startsWith(CC + ",")) { // Validación basada en el CC
                    writer.write(linea);
                    writer.newLine();
                } else {
                    encontrado = true;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar el archivo.");
        }
        return encontrado;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WorkBenchDuts = new javax.swing.JPanel();
        Title = new javax.swing.JLabel();
        ButtonCrear = new javax.swing.JButton();
        ButtonEliminar = new javax.swing.JButton();
        ButtonLeer = new javax.swing.JButton();
        ButtonSalir = new javax.swing.JButton();
        PersonaPicture = new javax.swing.JLabel();
        EliminarPicture = new javax.swing.JLabel();
        GuardarPicture = new javax.swing.JLabel();
        ButtonMostrar = new javax.swing.JButton();
        MostrarPicture = new javax.swing.JLabel();
        ButtonIngresar = new javax.swing.JButton();
        LoginPicture = new javax.swing.JLabel();
        ButtonDutsPromedios = new javax.swing.JButton();
        DutsPicture = new javax.swing.JLabel();
        ButtonEnviar = new javax.swing.JButton();
        EnviarPicture = new javax.swing.JLabel();

        WorkBenchDuts.setBackground(new java.awt.Color(51, 51, 51));
        WorkBenchDuts.setPreferredSize(new java.awt.Dimension(1280, 664));

        Title.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 70)); // NOI18N
        Title.setForeground(new java.awt.Color(194, 204, 9));
        Title.setText("E-BANK UTS");

        ButtonCrear.setBackground(new java.awt.Color(194, 204, 9));
        ButtonCrear.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonCrear.setText("CREAR PERSONA");
        ButtonCrear.setBorder(null);
        ButtonCrear.setBorderPainted(false);
        ButtonCrear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        ButtonEliminar.setBackground(new java.awt.Color(194, 204, 9));
        ButtonEliminar.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonEliminar.setText("ELIMINAR PERSONA");
        ButtonEliminar.setBorder(null);
        ButtonEliminar.setBorderPainted(false);
        ButtonEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ButtonEliminar.setPreferredSize(new java.awt.Dimension(158, 23));

        ButtonLeer.setBackground(new java.awt.Color(194, 204, 9));
        ButtonLeer.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonLeer.setText("GUARDAR PERSONA");
        ButtonLeer.setBorder(null);
        ButtonLeer.setBorderPainted(false);
        ButtonLeer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ButtonLeer.setPreferredSize(new java.awt.Dimension(158, 23));

        ButtonSalir.setBackground(new java.awt.Color(194, 204, 9));
        ButtonSalir.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonSalir.setText("SALIR");
        ButtonSalir.setBorder(null);
        ButtonSalir.setBorderPainted(false);
        ButtonSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        PersonaPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/PersonaIcon.png"))); // NOI18N

        EliminarPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/EliminarIcon.png"))); // NOI18N

        GuardarPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/GuardarIcon.png"))); // NOI18N

        ButtonMostrar.setBackground(new java.awt.Color(194, 204, 9));
        ButtonMostrar.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonMostrar.setText("MOSTRAR PERSONA");
        ButtonMostrar.setBorder(null);
        ButtonMostrar.setBorderPainted(false);
        ButtonMostrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        MostrarPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/MostrarIcon.png"))); // NOI18N

        ButtonIngresar.setBackground(new java.awt.Color(194, 204, 9));
        ButtonIngresar.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonIngresar.setText("INGRESAR");
        ButtonIngresar.setBorder(null);
        ButtonIngresar.setBorderPainted(false);
        ButtonIngresar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        LoginPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/LoginIcon.png"))); // NOI18N

        ButtonDutsPromedios.setBackground(new java.awt.Color(194, 204, 9));
        ButtonDutsPromedios.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonDutsPromedios.setText("MOSTRAR DUTS");
        ButtonDutsPromedios.setBorder(null);
        ButtonDutsPromedios.setBorderPainted(false);
        ButtonDutsPromedios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        DutsPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/DutsIcon.png"))); // NOI18N

        ButtonEnviar.setBackground(new java.awt.Color(194, 204, 9));
        ButtonEnviar.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonEnviar.setText("ENVIAR DUTS");
        ButtonEnviar.setBorder(null);
        ButtonEnviar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        EnviarPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/EnviarIcon.png"))); // NOI18N

        javax.swing.GroupLayout WorkBenchDutsLayout = new javax.swing.GroupLayout(WorkBenchDuts);
        WorkBenchDuts.setLayout(WorkBenchDutsLayout);
        WorkBenchDutsLayout.setHorizontalGroup(
            WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                .addGap(404, 404, 404)
                .addComponent(Title)
                .addContainerGap(422, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchDutsLayout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(MostrarPicture))
                    .addComponent(ButtonMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(ButtonCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(PersonaPicture)))
                .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ButtonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ButtonLeer, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                                .addGap(153, 153, 153)
                                .addComponent(GuardarPicture)))
                        .addGap(100, 100, 100))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchDutsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarPicture)
                        .addGap(151, 151, 151)))
                .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ButtonDutsPromedios, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DutsPicture))
                        .addGap(76, 76, 76)
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ButtonIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LoginPicture))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchDutsLayout.createSequentialGroup()
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                                .addComponent(EnviarPicture)
                                .addGap(18, 18, 18))
                            .addComponent(ButtonEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(242, 242, 242))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchDutsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ButtonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59))
        );
        WorkBenchDutsLayout.setVerticalGroup(
            WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(Title)
                .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, WorkBenchDutsLayout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PersonaPicture)
                            .addComponent(EliminarPicture))
                        .addGap(0, 0, 0)
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchDutsLayout.createSequentialGroup()
                                .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ButtonCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ButtonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(59, 59, 59)
                                .addComponent(MostrarPicture)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ButtonMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchDutsLayout.createSequentialGroup()
                                .addComponent(GuardarPicture)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ButtonLeer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(137, Short.MAX_VALUE))
                    .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(WorkBenchDutsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                                .addComponent(DutsPicture)
                                .addGap(18, 18, 18)
                                .addComponent(ButtonDutsPromedios, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WorkBenchDutsLayout.createSequentialGroup()
                                .addComponent(LoginPicture)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ButtonIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(EnviarPicture)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ButtonEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ButtonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WorkBenchDuts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(WorkBenchDuts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonCrear;
    private javax.swing.JButton ButtonDutsPromedios;
    private javax.swing.JButton ButtonEliminar;
    private javax.swing.JButton ButtonEnviar;
    private javax.swing.JButton ButtonIngresar;
    private javax.swing.JButton ButtonLeer;
    private javax.swing.JButton ButtonMostrar;
    private javax.swing.JButton ButtonSalir;
    private javax.swing.JLabel DutsPicture;
    private javax.swing.JLabel EliminarPicture;
    private javax.swing.JLabel EnviarPicture;
    private javax.swing.JLabel GuardarPicture;
    private javax.swing.JLabel LoginPicture;
    private javax.swing.JLabel MostrarPicture;
    private javax.swing.JLabel PersonaPicture;
    private javax.swing.JLabel Title;
    private javax.swing.JPanel WorkBenchDuts;
    // End of variables declaration//GEN-END:variables
}
