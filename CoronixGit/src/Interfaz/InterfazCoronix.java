// ============================================================
// InterfazCoronix.java
// Ventana principal del IDE Coronix.
// Contiene el editor de código, la consola de resultados,
// y el analizador léxico/sintáctico/semántico integrado
// que procesa el lenguaje Coronix línea por línea.
//
// El lenguaje Coronix tiene la siguiente sintaxis:
//   tipo variable => valor!!
//
// Tipos soportados:
//   cuarto  → entero    (ej. cuarto edad => 20!!)
//   media   → decimal   (ej. media pi   => 3.14!!)
//   mega    → cadena    (ej. mega nombre => "Hola"!!)
// ============================================================

package Interfaz;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class InterfazCoronix extends JFrame {

    // ── Componentes de la interfaz ────────────────────────────

    /** Área de texto donde el usuario escribe su código Coronix */
    private final JTextArea areaCoronix;

    /** Área de texto donde se muestran los resultados del análisis */
    private final JTextArea areaConsola;

    /** Etiqueta que muestra la línea y columna actual del cursor */
    private final JLabel lblLinea;

    // ── Paleta de colores del IDE ─────────────────────────────

    /** Color de fondo general de la ventana */
    private static final Color BG_DARK    = new Color(18, 18, 30);

    /** Color de fondo del editor de código */
    private static final Color BG_EDITOR  = new Color(26, 26, 46);

    /** Color de fondo de la consola de resultados */
    private static final Color BG_CONSOLE = new Color(10, 10, 20);

    /** Color de acento (azul claro) para botones y cursor */
    private static final Color ACCENT     = new Color(99, 179, 237);

    /** Color verde para mensajes de éxito */
    private static final Color SUCCESS    = new Color(72, 199, 142);

    /** Color rojo para mensajes de error */
    private static final Color ERROR_COL  = new Color(252, 95, 95);

    /** Color principal del texto en editor y consola */
    private static final Color FG_MAIN    = new Color(220, 220, 235);

    /** Color secundario para texto decorativo (línea/columna) */
    private static final Color FG_DIM     = new Color(120, 120, 150);

    // ── Constructor ───────────────────────────────────────────

    /**
     * Construye y muestra la ventana principal del IDE Coronix.
     * Configura el editor, la consola, la barra de herramientas
     * y carga el ejemplo inicial de código.
     */
    public InterfazCoronix() {
        super("🌐 Coronix IDE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // Cierra la app al cerrar la ventana
        setSize(1000, 680);                        // Tamaño inicial de la ventana
        setLocationRelativeTo(null);               // Centra la ventana en la pantalla

        // ── Configurar el editor de código ────────────────────
        areaCoronix = crearArea(BG_EDITOR, FG_MAIN,
                new Font("JetBrains Mono", Font.PLAIN, 14));
        areaCoronix.setText(ejemploInicial()); // Carga el código de ejemplo al iniciar

        // ── Configurar la consola de resultados ───────────────
        areaConsola = crearArea(BG_CONSOLE, FG_MAIN,
                new Font("Courier New", Font.PLAIN, 13));
        areaConsola.setEditable(false); // El usuario no puede escribir en la consola

        // ── Indicador de línea y columna ──────────────────────
        lblLinea = new JLabel("Línea: 1  Col: 1");
        lblLinea.setForeground(FG_DIM);
        lblLinea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLinea.setBorder(new EmptyBorder(2, 8, 2, 8));

        // Actualiza el indicador cada vez que el cursor se mueve en el editor
        areaCoronix.addCaretListener(e -> actualizarPosicion());

        // ── Botón Ejecutar ────────────────────────────────────
        // Dispara el análisis del código al hacer clic
        JButton btnEjecutar = new JButton("▶ Ejecutar");
        btnEjecutar.setBackground(ACCENT);
        btnEjecutar.setForeground(BG_DARK);
        btnEjecutar.addActionListener(e -> ejecutar());

        // ── Botón Limpiar ─────────────────────────────────────
        // Borra el contenido del editor y de la consola
        JButton btnLimpiar = new JButton("🗑 Limpiar");
        btnLimpiar.addActionListener(e -> {
            areaCoronix.setText("");
            areaConsola.setText("");
        });

        // ── Barra de herramientas (toolbar) ───────────────────
        // Contiene los botones y el indicador de posición
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(22, 22, 40));
        toolbar.add(btnEjecutar);
        toolbar.add(btnLimpiar);
        toolbar.add(lblLinea);

        // ── Panel dividido: editor | consola ──────────────────
        JScrollPane scrollEditor  = new JScrollPane(areaCoronix);
        JScrollPane scrollConsola = new JScrollPane(areaConsola);

        // Divide la ventana horizontalmente en dos paneles
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollEditor,
                scrollConsola
        );

        // ── Ensamblado de la ventana ──────────────────────────
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);   // Toolbar arriba
        add(split,   BorderLayout.CENTER);  // Editor y consola en el centro

        setVisible(true); // Hace visible la ventana
    }

    // ── ANALIZADOR CORONIX ────────────────────────────────────

    /**
     * Método principal del analizador. Se ejecuta al presionar "▶ Ejecutar".
     * Procesa el código del editor línea por línea aplicando tres fases:
     *
     * 1. Análisis léxico:    verifica que termine con "!!"
     * 2. Análisis sintáctico: verifica el formato "tipo variable => valor"
     * 3. Análisis semántico: valida que el valor corresponda al tipo declarado
     *
     * Muestra en la consola ✔ por cada línea correcta o ❌ con el error.
     */
    private void ejecutar() {

        // Limpia la consola antes de cada ejecución
        areaConsola.setText("");

        // Obtiene el código escrito en el editor
        String codigo = areaCoronix.getText().trim();

        // Verifica que haya código para analizar
        if (codigo.isEmpty()) {
            consola("⚠ El editor está vacío.\n", ERROR_COL);
            return;
        }

        // Divide el código en líneas individuales para procesarlas una a una
        String[] lineas = codigo.split("\n");
        boolean hayErrores = false; // Bandera para saber si hubo algún error

        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();

            // Ignora líneas vacías y comentarios (líneas que empiezan con //)
            if (linea.isEmpty() || linea.startsWith("//")) continue;

            try {
                // ── ANÁLISIS LÉXICO: verificar terminador "!!" ────
                // En Coronix, toda sentencia debe terminar con "!!"
                if (!linea.endsWith("!!")) {
                    throw new Exception("Falta '!!' al final");
                }

                // Elimina el terminador "!!" para procesar el resto
                linea = linea.substring(0, linea.length() - 2).trim();

                // ── ANÁLISIS SINTÁCTICO: verificar formato "=>" ───
                // La sentencia debe tener exactamente una flecha "=>"
                // que separa la declaración del valor
                String[] partes = linea.split("=>");
                if (partes.length != 2) {
                    throw new Exception("Formato inválido, se esperaba '=>'");
                }

                String izquierda = partes[0].trim(); // "tipo variable"
                String derecha   = partes[1].trim(); // "valor"

                // La parte izquierda debe tener exactamente dos palabras: tipo y nombre
                String[] datos = izquierda.split(" ");
                if (datos.length != 2) {
                    throw new Exception("Declaración incorrecta");
                }

                String tipo     = datos[0]; // El tipo de dato (cuarto, media, mega)
                String variable = datos[1]; // El nombre de la variable

                // ── ANÁLISIS SEMÁNTICO: validar valor según tipo ──
                switch (tipo) {

                    case "cuarto":
                        // Para "cuarto", el valor debe ser un número entero válido
                        Integer.parseInt(derecha);
                        consola("✔ Entero: " + variable + " = " + derecha + "\n", SUCCESS);
                        break;

                    case "media":
                        // Para "media", el valor debe ser un número decimal válido
                        Double.parseDouble(derecha);
                        consola("✔ Decimal: " + variable + " = " + derecha + "\n", SUCCESS);
                        break;

                    case "mega":
                        // Para "mega", el valor debe ser una cadena entre comillas dobles
                        if (!(derecha.startsWith("\"") && derecha.endsWith("\""))) {
                            throw new Exception("Cadena debe ir entre comillas");
                        }
                        consola("✔ Cadena: " + variable + " = " + derecha + "\n", SUCCESS);
                        break;

                    default:
                        // Si el tipo no es ninguno de los anteriores, es un error léxico
                        throw new Exception("Tipo desconocido: " + tipo);
                }

            } catch (Exception ex) {
                // Muestra el error con el número de línea correspondiente
                consola("❌ Línea " + (i + 1) + ": " + ex.getMessage() + "\n", ERROR_COL);
                hayErrores = true;
            }
        }

        // Si no hubo ningún error, muestra mensaje de éxito final
        if (!hayErrores) {
            consola("\n✅ Análisis completado sin errores.\n", SUCCESS);
        }
    }

    // ── Métodos auxiliares de la interfaz ─────────────────────

    /**
     * Crea y configura un área de texto con el estilo visual del IDE.
     *
     * @param bg Color de fondo del área
     * @param fg Color del texto del área
     * @param f  Fuente a usar en el área
     * @return El JTextArea configurado
     */
    private JTextArea crearArea(Color bg, Color fg, Font f) {
        JTextArea a = new JTextArea();
        a.setBackground(bg);
        a.setForeground(fg);
        a.setCaretColor(ACCENT);                       // Color del cursor parpadeante
        a.setFont(f);
        a.setBorder(new EmptyBorder(10, 10, 10, 10));  // Margen interno
        return a;
    }

    /**
     * Agrega texto a la consola con un color específico.
     * Se usa para mostrar resultados de éxito (verde) o error (rojo).
     *
     * @param txt El texto a mostrar en la consola
     * @param c   El color con el que se mostrará el texto
     */
    private void consola(String txt, Color c) {
        areaConsola.setForeground(c);
        areaConsola.append(txt);
    }

    /**
     * Actualiza el indicador de línea y columna en la barra de herramientas.
     * Se llama automáticamente cada vez que el cursor se mueve en el editor.
     */
    private void actualizarPosicion() {
        try {
            int pos = areaCoronix.getCaretPosition();                        // Posición absoluta del cursor
            int lin = areaCoronix.getLineOfOffset(pos) + 1;                  // Número de línea (base 1)
            int col = pos - areaCoronix.getLineStartOffset(lin - 1) + 1;     // Número de columna (base 1)
            lblLinea.setText("Línea: " + lin + "  Col: " + col);
        } catch (Exception ignored) {
            // Ignorar excepciones al mover el cursor fuera de rango
        }
    }

    /**
     * Devuelve el código de ejemplo que aparece en el editor al iniciar el IDE.
     * Muestra el uso de los tres tipos de datos disponibles en Coronix.
     *
     * @return String con el código de ejemplo formateado
     */
    private String ejemploInicial() {
        return """
               // Ejemplo Coronix
               cuarto a => 10!!
               cuarto b => 5!!
               media pi => 3.14!!
               mega saludo => "hola mundo"!!
               """;
    }

    // ── Punto de entrada alternativo ─────────────────────────

    /**
     * Método main alternativo dentro de la interfaz.
     * Permite ejecutar el IDE directamente desde esta clase
     * además de hacerlo desde CoronixGit.java.
     *
     * @param args Argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazCoronix::new);
    }
}
