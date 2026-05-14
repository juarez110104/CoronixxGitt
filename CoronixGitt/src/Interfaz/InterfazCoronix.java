package Interfaz;

import Clases.ClasePrincipal;
import Analizador.*;
import Clases.TablaSimbolos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InterfazCoronix extends JFrame {

    JTextArea editor  = new JTextArea();
    JTextArea consola = new JTextArea();

    DefaultTableModel t1 = new DefaultTableModel(
            new String[]{"Token", "Lexema", "Patrón", "Reservada"}, 0);
    DefaultTableModel t2 = new DefaultTableModel(
            new String[]{"Elemento", "Tipo"}, 0);

    // ─────────────────────────────────────────────────────────────
    // Panel de información de palabras reservadas (siempre visible)
    // Se actualiza al hacer clic en una fila de la tabla de tokens
    // ─────────────────────────────────────────────────────────────
    JPanel panelInfo = new JPanel(new BorderLayout());
    JLabel lblTitulo = new JLabel(" Haz clic en una palabra reservada para ver su información");
    JTextArea areaInfo = new JTextArea();

    // ─────────────────────────────────────────────────────────────
    // Mapa con información de cada palabra reservada
    // ─────────────────────────────────────────────────────────────
    private static final Map<String, String> INFO_PALABRAS = new HashMap<>();

    static {
        INFO_PALABRAS.put("cuarto",
            "PALABRA RESERVADA: cuarto\n" +
            "─────────────────────────────────────\n" +
            "  Equivalente Java : int\n" +
            "  Descripción      : Almacena números enteros sin decimales.\n" +
            "  Restricciones    : Máximo 10 dígitos. No acepta punto decimal.\n" +
            "  Ejemplo de uso   : cuarto edad => 25!!"
        );

        INFO_PALABRAS.put("media",
            "PALABRA RESERVADA: media\n" +
            "─────────────────────────────────────\n" +
            "  Equivalente Java : double\n" +
            "  Descripción      : Almacena números con punto decimal.\n" +
            "  Restricciones    : Parte entera máx. 10 dígitos, decimal máx. 8 dígitos.\n" +
            "                     No acepta enteros puros (usar 3.0 en vez de 3).\n" +
            "  Ejemplo de uso   : media precio => 19.99!!"
        );

        INFO_PALABRAS.put("mega",
            "?PALABRA RESERVADA: mega\n" +
            "─────────────────────────────────────\n" +
            "  Equivalente Java : String\n" +
            "  Descripción      : Almacena cadenas de texto entre comillas dobles.\n" +
            "  Restricciones    : Máximo 64 caracteres.\n" +
            "  Ejemplo de uso   : mega saludo => \"Hola mundo\"!!"
        );
    }

    public InterfazCoronix() {
        setTitle("Coronix IDE PRO");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ── Configuración del área de información ──────────────────
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaInfo.setBackground(new Color(245, 245, 245));
        areaInfo.setForeground(new Color(30, 30, 30));
        areaInfo.setMargin(new Insets(6, 10, 6, 10));
        areaInfo.setRows(7);

        // ── Título del panel ───────────────────────────────────────
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(70, 70, 70));
        lblTitulo.setBackground(new Color(220, 220, 220));
        lblTitulo.setOpaque(true);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        panelInfo.setBorder(BorderFactory.createTitledBorder("Información de Palabra Reservada"));
        panelInfo.add(lblTitulo, BorderLayout.NORTH);
        panelInfo.add(new JScrollPane(areaInfo), BorderLayout.CENTER);

        // ── Tabla de tokens con listener de selección ──────────────
        JTable tabla1 = new JTable(t1);

        // Al hacer clic en una fila, si el lexema es palabra reservada
        // se muestra su información en el panel inferior
        tabla1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int fila = tabla1.getSelectedRow();
                    if (fila >= 0) {
                        // Columna 1 = Lexema
                        Object lexema = tabla1.getValueAt(fila, 1);
                        if (lexema != null && INFO_PALABRAS.containsKey(lexema.toString())) {
                            // Es palabra reservada → mostrar su info
                            lblTitulo.setText("  ✅ Palabra reservada seleccionada: " + lexema);
                            areaInfo.setText(INFO_PALABRAS.get(lexema.toString()));
                        } else {
                            // No es palabra reservada → limpiar panel
                            lblTitulo.setText("  ℹ️ Este token no es una palabra reservada");
                            areaInfo.setText("");
                        }
                    }
                }
            }
        });

        JTable tabla2 = new JTable(t2);
        JButton run = new JButton("▶ Ejecutar");
        run.addActionListener(e -> ejecutar());

        // ── Layout: tabla1 arriba, panelInfo abajo, tabla2 al lado ─
        JSplitPane splitTokensInfo = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tabla1),
                panelInfo
        );
        splitTokensInfo.setResizeWeight(0.55); // 55% tabla, 45% panel info

        JSplitPane split2 = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                splitTokensInfo,
                new JScrollPane(tabla2)
        );
        split2.setResizeWeight(0.65);

        JSplitPane splitConsola = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                split2,
                new JScrollPane(consola)
        );
        splitConsola.setResizeWeight(0.75);

        JSplitPane main = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(editor),
                splitConsola
        );
        main.setResizeWeight(0.35);

        add(run, BorderLayout.NORTH);
        add(main, BorderLayout.CENTER);
        setVisible(true);
    }

    void ejecutar() {
        t1.setRowCount(0);
        t2.setRowCount(0);
        consola.setText("");

        // Limpiar panel de info al re-ejecutar
        lblTitulo.setText(" Haz clic en una palabra reservada para ver su información");
        areaInfo.setText("");

        // Reinicia la tabla en cada ejecución para no acumular variables entre runs
        ClasePrincipal.tabla = new TablaSimbolos();

        String[] lineas = editor.getText().split("\n");
        for (int i = 0; i < lineas.length; i++) {
            String l = lineas[i].trim();
            if (l.isEmpty()) continue;
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