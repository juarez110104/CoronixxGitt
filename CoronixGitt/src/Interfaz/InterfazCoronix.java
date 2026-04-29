package Interfaz;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class InterfazCoronix extends JFrame {

    // ── Componentes ──────────────────────────────────────────
    private final JTextArea areaCoronix;
    private final JTextArea areaConsola;
    private final JLabel lblLinea;

    // ── Colores ──────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(18, 18, 30);
    private static final Color BG_EDITOR  = new Color(26, 26, 46);
    private static final Color BG_CONSOLE = new Color(10, 10, 20);
    private static final Color ACCENT     = new Color(99, 179, 237);
    private static final Color SUCCESS    = new Color(72, 199, 142);
    private static final Color ERROR_COL  = new Color(252, 95, 95);
    private static final Color FG_MAIN    = new Color(220, 220, 235);
    private static final Color FG_DIM     = new Color(120, 120, 150);

    public InterfazCoronix() {
        super("🌐 Coronix IDE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        // Editor
        areaCoronix = crearArea(BG_EDITOR, FG_MAIN,
                new Font("JetBrains Mono", Font.PLAIN, 14));
        areaCoronix.setText(ejemploInicial());

        // Consola
        areaConsola = crearArea(BG_CONSOLE, FG_MAIN,
                new Font("Courier New", Font.PLAIN, 13));
        areaConsola.setEditable(false);

        // Línea
        lblLinea = new JLabel("Línea: 1  Col: 1");
        lblLinea.setForeground(FG_DIM);
        lblLinea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLinea.setBorder(new EmptyBorder(2, 8, 2, 8));

        areaCoronix.addCaretListener(e -> actualizarPosicion());

        // Botones
        JButton btnEjecutar = new JButton("▶ Ejecutar");
        btnEjecutar.setBackground(ACCENT);
        btnEjecutar.setForeground(BG_DARK);
        btnEjecutar.addActionListener(e -> ejecutar());

        JButton btnLimpiar = new JButton("🗑 Limpiar");
        btnLimpiar.addActionListener(e -> {
            areaCoronix.setText("");
            areaConsola.setText("");
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(22, 22, 40));
        toolbar.add(btnEjecutar);
        toolbar.add(btnLimpiar);
        toolbar.add(lblLinea);

        JScrollPane scrollEditor = new JScrollPane(areaCoronix);
        JScrollPane scrollConsola = new JScrollPane(areaConsola);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollEditor,
                scrollConsola
        );

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        setVisible(true);
    }

    // ── ANALIZADOR SIN JFLEX/CUP ─────────────────────────────
    private void ejecutar() {
        areaConsola.setText("");
        String codigo = areaCoronix.getText().trim();

        if (codigo.isEmpty()) {
            consola("⚠ El editor está vacío.\n", ERROR_COL);
            return;
        }

        String[] lineas = codigo.split("\n");
        boolean hayErrores = false;

        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();

            if (linea.isEmpty() || linea.startsWith("//")) continue;

            try {
                if (!linea.endsWith("!!")) {
                    throw new Exception("Falta '!!' al final");
                }

                linea = linea.substring(0, linea.length() - 2).trim();

                String[] partes = linea.split("=>");
                if (partes.length != 2) {
                    throw new Exception("Formato inválido, se esperaba '=>'");
                }

                String izquierda = partes[0].trim();
                String derecha = partes[1].trim();

                String[] datos = izquierda.split(" ");
                if (datos.length != 2) {
                    throw new Exception("Declaración incorrecta");
                }

                String tipo = datos[0];
                String variable = datos[1];

                switch (tipo) {
                    case "cuarto":
                        Integer.parseInt(derecha);
                        consola("✔ Entero: " + variable + " = " + derecha + "\n", SUCCESS);
                        break;

                    case "media":
                        Double.parseDouble(derecha);
                        consola("✔ Decimal: " + variable + " = " + derecha + "\n", SUCCESS);
                        break;

                    case "mega":
                        if (!(derecha.startsWith("\"") && derecha.endsWith("\""))) {
                            throw new Exception("Cadena debe ir entre comillas");
                        }
                        consola("✔ Cadena: " + variable + " = " + derecha + "\n", SUCCESS);
                        break;

                    default:
                        throw new Exception("Tipo desconocido: " + tipo);
                }

            } catch (Exception ex) {
                consola("❌ Línea " + (i + 1) + ": " + ex.getMessage() + "\n", ERROR_COL);
                hayErrores = true;
            }
        }

        if (!hayErrores) {
            consola("\n✅ Análisis completado sin errores.\n", SUCCESS);
        }
    }

    // ── UI helpers ───────────────────────────────────────────
    private JTextArea crearArea(Color bg, Color fg, Font f) {
        JTextArea a = new JTextArea();
        a.setBackground(bg);
        a.setForeground(fg);
        a.setCaretColor(ACCENT);
        a.setFont(f);
        a.setBorder(new EmptyBorder(10, 10, 10, 10));
        return a;
    }

    private void consola(String txt, Color c) {
        areaConsola.setForeground(c);
        areaConsola.append(txt);
    }

    private void actualizarPosicion() {
        try {
            int pos = areaCoronix.getCaretPosition();
            int lin = areaCoronix.getLineOfOffset(pos) + 1;
            int col = pos - areaCoronix.getLineStartOffset(lin - 1) + 1;
            lblLinea.setText("Línea: " + lin + "  Col: " + col);
        } catch (Exception ignored) {}
    }

    private String ejemploInicial() {
        return """
               // Ejemplo Coronix
               cuarto a => 10!!
               cuarto b => 5!!
               media pi => 3.14!!
               mega saludo => "hola mundo"!!
               """;
    }

    // ── MAIN ────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazCoronix::new);
    }
}