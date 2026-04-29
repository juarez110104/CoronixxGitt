package Interfaz;

import Clases.ClasePrincipal;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InterfazCoronix extends JFrame {

    JTextArea editor, consola;
    JTable tablaTokens, tablaSimbolos;

    DefaultTableModel modeloTokens, modeloSimbolos;

    public InterfazCoronix() {

        setTitle("🌐 Coronix IDE PRO");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // EDITOR
        editor = new JTextArea();
        editor.setBackground(new Color(40,40,40));
        editor.setForeground(Color.WHITE);

        // CONSOLA
        consola = new JTextArea();
        consola.setBackground(Color.BLACK);
        consola.setForeground(Color.GREEN);
        consola.setEditable(false);

        // TOKENS
        modeloTokens = new DefaultTableModel(
                new String[]{"Lexema","Tipo","Línea"}, 0);
        tablaTokens = new JTable(modeloTokens);

        // SIMBOLOS
        modeloSimbolos = new DefaultTableModel(
                new String[]{"Variable","Tipo","Valor"}, 0);
        tablaSimbolos = new JTable(modeloSimbolos);

        // SPLITS
        JSplitPane abajo = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaSimbolos),
                new JScrollPane(consola)
        );

        JSplitPane derecha = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaTokens),
                abajo
        );

        JSplitPane principal = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(editor),
                derecha
        );

        // BOTÓN
        JButton run = new JButton("▶ Ejecutar");
        run.addActionListener(e -> ejecutar());

        add(run, BorderLayout.NORTH);
        add(principal, BorderLayout.CENTER);

        setVisible(true);
    }

    private void ejecutar() {

        modeloTokens.setRowCount(0);
        modeloSimbolos.setRowCount(0);
        consola.setText("");

        String[] lineas = editor.getText().split("\n");

        for (int i = 0; i < lineas.length; i++) {

            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;

            try {

                // TOKENS
                var tokens = ClasePrincipal.obtenerTokens(linea, i+1);
                for (String[] t : tokens) modeloTokens.addRow(t);

                // EJECUCIÓN
                String res = ClasePrincipal.procesarLinea(linea);
                consola.append("✔ " + res + "\n");

            } catch (Exception ex) {
                consola.setForeground(Color.RED);
                consola.append("❌ Línea " + (i+1) + ": " + ex.getMessage() + "\n");
            }
        }

        // TABLA DE SÍMBOLOS
        var tabla = ClasePrincipal.obtenerTabla();
        for (String[] fila : tabla) modeloSimbolos.addRow(fila);
    }
}