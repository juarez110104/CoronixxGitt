package Interfaz;

import Clases.ClasePrincipal;
import Clases.TablaSimbolos;
import Analizador.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class InterfazCoronix extends JFrame {

    // ✅ Cambiamos JTextArea por JTextPane para poder colorear comentarios
    JTextPane editor  = new JTextPane();
    JTextArea consola = new JTextArea();

    DefaultTableModel t1 = new DefaultTableModel(
            new String[]{"Token", "Lexema", "Patrón", "Reservada"}, 0);
    DefaultTableModel t2 = new DefaultTableModel(
            new String[]{"Elemento", "Tipo"}, 0);

    public InterfazCoronix() {
        setTitle("Coronix IDE PRO");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ✅ Fuente monoespaciada para el editor
        editor.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consola.setEditable(false);

        JTable tabla1 = new JTable(t1);
        JTable tabla2 = new JTable(t2);

        JButton run = new JButton("▶ Ejecutar");
        run.addActionListener(e -> ejecutar());

        // ✅ Listener que colorea en tiempo real mientras se escribe
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { colorearComentarios(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { colorearComentarios(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tabla1), new JScrollPane(tabla2));
        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                split1, new JScrollPane(consola));
        JSplitPane main   = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(editor), split2);

        add(run,  BorderLayout.NORTH);
        add(main, BorderLayout.CENTER);
        setVisible(true);
    }

    // ✅ Colorea líneas que empiezan con && en gris opaco
    void colorearComentarios() {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = editor.getStyledDocument();
            String texto;
            try { texto = doc.getText(0, doc.getLength()); }
            catch (BadLocationException e) { return; }

            // Estilo normal (negro)
            Style normal = editor.addStyle("normal", null);
            StyleConstants.setForeground(normal, Color.BLACK);
            StyleConstants.setItalic(normal, false);
            doc.setCharacterAttributes(0, doc.getLength(), normal, true);

            // Estilo comentario (gris opaco + cursiva)
            Style comentario = editor.addStyle("comentario", null);
            StyleConstants.setForeground(comentario, new Color(150, 150, 150));
            StyleConstants.setItalic(comentario, true);

            // Aplica estilo línea por línea
            String[] lineas = texto.split("\n", -1);
            int offset = 0;
            for (String linea : lineas) {
                if (linea.trim().startsWith("&&")) {
                    doc.setCharacterAttributes(offset, linea.length(), comentario, true);
                }
                offset += linea.length() + 1; // +1 por el \n
            }
        });
    }

    void ejecutar() {
        t1.setRowCount(0);
        t2.setRowCount(0);
        consola.setText("");
        ClasePrincipal.tabla = new TablaSimbolos();

        String texto;
        try {
            texto = editor.getDocument().getText(0, editor.getDocument().getLength());
        } catch (BadLocationException e) {
            return;
        }

        String[] lineas = texto.split("\n");
        for (int i = 0; i < lineas.length; i++) {
            String l = lineas[i].trim();
            if (l.isEmpty()) continue;
            if (l.startsWith("&&")) continue; // ✅ Ignora comentarios

            try {
                for (Token tk : ClasePrincipal.obtenerTokens(l))
                    t1.addRow(new Object[]{tk.token, tk.lexema, tk.patron, tk.reservada});
                for (String[] c : ClasePrincipal.clasificacion(l))
                    t2.addRow(c);
                String r = ClasePrincipal.procesarLinea(l, i + 1);
                consola.append("✔ " + r + "\n");
            } catch (ErrorC e) {
                consola.append("❌ " + e + "\n");
            }
        }
    }
}