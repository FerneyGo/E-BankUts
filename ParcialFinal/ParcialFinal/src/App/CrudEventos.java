/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package App;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


/**
 *
 * @author nujif
 */
public class CrudEventos extends javax.swing.JPanel {
   private final String eventFilePath = "eventosRegistrados.txt";
   private final String filePath = "dutsPersonas.txt";

    /**
     * Creates new form Eventos
     */
    public CrudEventos() {
        initComponents();
        initEventHandlers(); 
    }
    
    private void initEventHandlers() {
        ButtonEventos.addActionListener(this::mostrarEventosDesdeBoton);
        ButtonRegistrarse.addActionListener(this::registrarEnEvento);
        ButtonUnregister.addActionListener(this::eliminarRegistroDeEvento);
        ButtonParticipar.addActionListener(this::participarEnEvento); 

}   
    
    private void participarEnEvento(ActionEvent e) {
    String CCInput = JOptionPane.showInputDialog(this, "Ingrese su CC:");

    if (CCInput != null && CCInput.matches("\\d+")) { // Validar que el CC sea un número
        int CC = Integer.parseInt(CCInput);

        // Verificar si la persona está registrada en algún evento
        if (!leerEventosDePersona(CC).isEmpty()) { // Si tiene eventos registrados
            boolean actualizado = sumarDuts(CC, 50); // Sumar 50 DUTS

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Has participado en el evento. Se te han sumado 50 DUTS.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar los DUTS.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No estás registrado en ningún evento.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número.");
    }
}
    
    private boolean sumarDuts(int CC, int cantidad) {
    boolean actualizado = false;
    List<String> registros = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] data = linea.split(",");
            int storedCC = Integer.parseInt(data[0]);

            if (storedCC == CC) {
                int dutsActuales = Integer.parseInt(data[2]);
                int nuevosDuts = dutsActuales + cantidad;

                // Actualizar la línea con el nuevo valor de DUTS
                data[2] = String.valueOf(nuevosDuts);
                linea = String.join(",", data);

                actualizado = true;
            }
            registros.add(linea);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al leer el archivo de personas.");
        return false;
    }

    // Reescribir el archivo con los datos actualizados
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        for (String registro : registros) {
            writer.write(registro);
            writer.newLine();
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al escribir en el archivo de personas.");
        return false;
    }

    return actualizado;
}
    
    
    private void eliminarRegistroDeEvento(ActionEvent e) {
    String CCInput = JOptionPane.showInputDialog(this, "Ingrese su CC:");

    if (CCInput != null && CCInput.matches("\\d+")) { // Validar que el CC sea un número
        int CC = Integer.parseInt(CCInput);

        // Verificar si la persona está registrada en algún evento
        List<String> eventos = leerEventosDePersona(CC);
        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay eventos registrados para este CC.");
        } else {
            String eventoAEliminar = JOptionPane.showInputDialog(this, "Ingrese el nombre del evento a eliminar:");

            if (eventoAEliminar != null && !eventoAEliminar.trim().isEmpty()) {
                boolean eliminado = eliminarEvento(CC, eventoAEliminar.trim());

                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Registro en el evento '" + eventoAEliminar + "' eliminado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el registro del evento '" + eventoAEliminar + "'.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "El nombre del evento no puede estar vacío.");
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número.");
    }
}
    
    private boolean eliminarEvento(int CC, String evento) {
    boolean eliminado = false;
    List<String> registros = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(eventFilePath))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] data = linea.split(",");

            // Comprobar si la línea es el registro que queremos eliminar
            if (data.length == 2) {
                int storedCC = Integer.parseInt(data[0]);
                String storedEvento = data[1].trim();

                if (storedCC == CC && storedEvento.equalsIgnoreCase(evento)) {
                    eliminado = true; // No agregar este registro a la lista de registros
                } else {
                    registros.add(linea); // Mantener el resto de los registros
                }
            }
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al leer el archivo de eventos: " + ex.getMessage());
        return false;
    }

    // Reescribir el archivo sin el registro eliminado
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(eventFilePath))) {
        for (String registro : registros) {
            writer.write(registro);
            writer.newLine();
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al escribir en el archivo de eventos: " + ex.getMessage());
        return false;
    }

    return eliminado;
}
    
    
    
    private void escribirEventoEnArchivo(String data) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(eventFilePath, true))) {
        writer.write(data);
        writer.newLine();
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al registrar el evento.");
    }
}
    
     private void registrarEnEvento(ActionEvent e) {
    String CCInput = JOptionPane.showInputDialog(this, "Ingrese su CC:");

    if (CCInput != null && CCInput.matches("\\d+")) { // Validar que el CC sea un número
        int CC = Integer.parseInt(CCInput);
        
        // Verificar si la persona está registrada
        if (devolverNombre(CC) != null) {
            String evento = JOptionPane.showInputDialog(this, "Ingrese el nombre del evento:");

            if (evento != null && !evento.trim().isEmpty()) {
                String registro = CC + "," + evento;

                // Escribir el registro en el archivo de eventos
                escribirEventoEnArchivo(registro);
                JOptionPane.showMessageDialog(this, "Registro en el evento '" + evento + "' realizado con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "El nombre del evento no puede estar vacío.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "CC no registrado. Por favor registre primero a la persona.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "CC inválido. Debe ser un número.");
    }
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
    
  
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WorkBenchEventos = new javax.swing.JPanel();
        Title = new javax.swing.JLabel();
        Title2 = new javax.swing.JLabel();
        ButtonEventos = new javax.swing.JButton();
        Text_Reto = new javax.swing.JLabel();
        Text_Hackaton = new javax.swing.JLabel();
        RetoPicture = new javax.swing.JLabel();
        HackathonPicture = new javax.swing.JLabel();
        ButtonRegistrarse = new javax.swing.JButton();
        ButtonUnregister = new javax.swing.JButton();
        ButtonParticipar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        WorkBenchEventos.setBackground(new java.awt.Color(51, 51, 51));
        WorkBenchEventos.setPreferredSize(new java.awt.Dimension(1280, 664));

        Title.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 70)); // NOI18N
        Title.setForeground(new java.awt.Color(194, 204, 9));
        Title.setText("E-BANK UTS");

        Title2.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 45)); // NOI18N
        Title2.setForeground(new java.awt.Color(194, 204, 9));
        Title2.setText("EVENTOS");

        ButtonEventos.setBackground(new java.awt.Color(194, 204, 9));
        ButtonEventos.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonEventos.setText("EVENTOS REGISTRADOS");
        ButtonEventos.setBorder(null);
        ButtonEventos.setBorderPainted(false);
        ButtonEventos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ButtonEventos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonEventosActionPerformed(evt);
            }
        });

        Text_Reto.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 24)); // NOI18N
        Text_Reto.setForeground(new java.awt.Color(194, 204, 9));
        Text_Reto.setText("RETO PROGRAMACION");

        Text_Hackaton.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 24)); // NOI18N
        Text_Hackaton.setForeground(new java.awt.Color(194, 204, 9));
        Text_Hackaton.setText("HACKATHON");

        RetoPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/RetoIcon.png"))); // NOI18N

        HackathonPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/HackathonIcon.png"))); // NOI18N

        ButtonRegistrarse.setBackground(new java.awt.Color(194, 204, 9));
        ButtonRegistrarse.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonRegistrarse.setText("REGISTRARSE");
        ButtonRegistrarse.setBorder(null);
        ButtonRegistrarse.setBorderPainted(false);
        ButtonRegistrarse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        ButtonUnregister.setBackground(new java.awt.Color(194, 204, 9));
        ButtonUnregister.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonUnregister.setText("ELIMINAR REGISTRO");
        ButtonUnregister.setBorder(null);
        ButtonUnregister.setBorderPainted(false);
        ButtonUnregister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        ButtonParticipar.setBackground(new java.awt.Color(194, 204, 9));
        ButtonParticipar.setFont(new java.awt.Font("Jumper PERSONAL USE ONLY Bold", 1, 18)); // NOI18N
        ButtonParticipar.setText("PARTICIPAR DEL EVENTO");
        ButtonParticipar.setBorder(null);
        ButtonParticipar.setBorderPainted(false);
        ButtonParticipar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/PrincipalImage.png"))); // NOI18N

        javax.swing.GroupLayout WorkBenchEventosLayout = new javax.swing.GroupLayout(WorkBenchEventos);
        WorkBenchEventos.setLayout(WorkBenchEventosLayout);
        WorkBenchEventosLayout.setHorizontalGroup(
            WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(RetoPicture))
                            .addComponent(Text_Reto)))
                    .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Text_Hackaton)
                            .addComponent(HackathonPicture))))
                .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(Title)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                        .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ButtonParticipar, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ButtonEventos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ButtonRegistrarse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ButtonUnregister, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(Title2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(127, 127, 127)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        WorkBenchEventosLayout.setVerticalGroup(
            WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(Title)
                .addGap(18, 18, 18)
                .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                        .addComponent(Text_Reto)
                        .addGroup(WorkBenchEventosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(RetoPicture)
                                .addGap(42, 42, 42)
                                .addComponent(Text_Hackaton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(HackathonPicture))
                            .addGroup(WorkBenchEventosLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(Title2)
                                .addGap(54, 54, 54)
                                .addComponent(ButtonRegistrarse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addComponent(ButtonUnregister, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(ButtonEventos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(ButtonParticipar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WorkBenchEventosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(WorkBenchEventos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(WorkBenchEventos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonEventosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonEventosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonEventosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonEventos;
    private javax.swing.JButton ButtonParticipar;
    private javax.swing.JButton ButtonRegistrarse;
    private javax.swing.JButton ButtonUnregister;
    private javax.swing.JLabel HackathonPicture;
    private javax.swing.JLabel RetoPicture;
    private javax.swing.JLabel Text_Hackaton;
    private javax.swing.JLabel Text_Reto;
    private javax.swing.JLabel Title;
    private javax.swing.JLabel Title2;
    private javax.swing.JPanel WorkBenchEventos;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
