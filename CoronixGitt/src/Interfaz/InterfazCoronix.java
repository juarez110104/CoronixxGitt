package Interfaz;

import Clases.ClasePrincipal;
import Analizador.Token;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InterfazCoronix extends JFrame {

    JTextArea editor, consola;
    JTable tablaTokens, tablaSimbolos;

    DefaultTableModel mTokens, mSimbolos;

    public InterfazCoronix() {

        setTitle("🌐 Coronix IDE PRO");
        setSize(1100,650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        editor=new JTextArea();
        consola=new JTextArea();

        mTokens=new DefaultTableModel(new String[]{"Lexema","Tipo","Linea"},0);
        tablaTokens=new JTable(mTokens);

        mSimbolos=new DefaultTableModel(new String[]{"Var","Tipo","Valor"},0);
        tablaSimbolos=new JTable(mSimbolos);

        JButton run=new JButton("Ejecutar");

        run.addActionListener(e->ejecutar());

        JSplitPane abajo=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaSimbolos),
                new JScrollPane(consola));

        JSplitPane derecha=new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaTokens),
                abajo);

        JSplitPane main=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(editor),
                derecha);

        add(run,BorderLayout.NORTH);
        add(main);

        setVisible(true);
    }

    private void ejecutar(){

        mTokens.setRowCount(0);
        mSimbolos.setRowCount(0);
        consola.setText("");

        ClasePrincipal.tabla.limpiar();

        String[] lineas=editor.getText().split("\n");

        for(int i=0;i<lineas.length;i++){

            String linea=lineas[i].trim();
            if(linea.isEmpty()) continue;

            try{

                var tokens=ClasePrincipal.obtenerTokens(linea,i+1);

                for(Token t:tokens)
                    mTokens.addRow(new Object[]{t.lexema,t.tipo,t.linea});

                String res=ClasePrincipal.procesarLinea(linea);
                consola.append("✔ "+res+"\n");

            }catch(Exception ex){
                consola.append("❌ Linea "+(i+1)+": "+ex.getMessage()+"\n");
            }
        }

        var tabla=ClasePrincipal.tablaUI();

        for(String[] f:tabla)
            mSimbolos.addRow(f);
    }
}