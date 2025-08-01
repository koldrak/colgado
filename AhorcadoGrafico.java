import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import javax.swing.JFileChooser;

public class AhorcadoGrafico extends JFrame {
    private List<String> palabrasDisponibles = new ArrayList<>();
    private String palabraSecreta;
    private char[] palabraAdivinada;
    private int intentosRestantes = 6;
    private Set<Character> letrasIngresadas = new HashSet<>();
    private JLabel lblPalabra;  // Declaración de la etiqueta de la palabra
    private JPanel panelPalabra;  // Panel donde se muestra la palabra a adivinar
    private JPanel panelLetrasUsadas;  // Panel donde se muestran las letras usadas
    private JTextField txtLetra;
    private JPanel panelDibujo;
    private int palabrasAcertadas = 0;
    private JLabel lblContadorPalabras;

    public AhorcadoGrafico() {
        setTitle("Juego del Ahorcado");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cargarPalabrasDesdeArchivo("palabras.txt");

        // Seleccionar palabra aleatoria sin repetir
        Random rand = new Random();
        if (!palabrasDisponibles.isEmpty()) {
            palabraSecreta = palabrasDisponibles.remove(rand.nextInt(palabrasDisponibles.size()));
        } else {
            palabraSecreta = "ERROR"; // Manejo de error si no hay palabras disponibles
        }

        
        // Inicializar palabraAdivinada
        palabraAdivinada = new char[palabraSecreta.length()];
        for (int i = 0; i < palabraAdivinada.length; i++) {
            palabraAdivinada[i] = '_';
        }

        // Configurar el layout principal
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 📌 1️⃣ Inicializar el panel del gráfico del ahorcado
        panelDibujo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarPantalla((Graphics2D) g);
            }
        };
        panelDibujo.setBackground(Color.WHITE);

        // 📌 2️⃣ Inicializar el panel de la palabra a adivinar
     // 📌 Panel de la palabra a adivinar (arriba derecha)
        panelPalabra = new JPanel();
        panelPalabra.setBackground(new Color(20, 100, 50));
        panelPalabra.setLayout(new GridBagLayout());

        lblPalabra = new JLabel(getPalabraAdivinada());
        lblPalabra.setForeground(Color.WHITE);
        lblPalabra.setHorizontalAlignment(SwingConstants.CENTER);
        panelPalabra.add(lblPalabra);

        // 📌 Ajustar tamaño de fuente al redimensionar
        panelPalabra.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ajustarTamanioFuente(lblPalabra, panelPalabra);
            }
        });

     // 📌 Panel de letras usadas (abajo derecha)
        panelLetrasUsadas = new JPanel();
        panelLetrasUsadas.setBackground(new Color(150, 50, 120));
        panelLetrasUsadas.setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0;
        gbc2.gridy = 0; // 📌 Primera fila dentro de panelLetrasUsadas

        // 📌 Agregar etiqueta de letras usadas
        JLabel lblLetras = new JLabel("Letras usadas: " + letrasIngresadas.toString());
        lblLetras.setFont(new Font("Arial", Font.PLAIN, 24));
        lblLetras.setForeground(Color.WHITE);
        panelLetrasUsadas.add(lblLetras, gbc2);

        // 📌 Agregar campo de entrada en una nueva fila
        gbc2.gridy = 1; // 📌 Segunda fila dentro de panelLetrasUsadas
        JPanel panelEntrada = new JPanel();
        panelEntrada.setBackground(new Color(150, 50, 120));

        txtLetra = new JTextField(5);
        JButton btnIntentar = new JButton("Intentar");

        // 📌 Permitir que "Enter" active el botón "Intentar"
        txtLetra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnIntentar.doClick(); // Simula un clic en el botón
            }
        });

        // Agregar componentes al panel
        panelEntrada.add(new JLabel("Ingresa una letra:"));
        panelEntrada.add(txtLetra);
        panelEntrada.add(btnIntentar);

        // 📌 Agregar panelEntrada a la segunda fila
        panelLetrasUsadas.add(panelEntrada, gbc2);

        // 📌 Agregar contador de palabras acertadas
        gbc2.gridy = 2;
        lblContadorPalabras = new JLabel("Palabras acertadas: " + palabrasAcertadas);
        lblContadorPalabras.setFont(new Font("Arial", Font.PLAIN, 24));
        lblContadorPalabras.setForeground(Color.WHITE);
        panelLetrasUsadas.add(lblContadorPalabras, gbc2);

        // 📌 4️⃣ Agregar los paneles al GridBagLayout en el orden correcto
        // 📌 Panel del gráfico del ahorcado (IZQUIERDA)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2; // Ocupa dos filas completas
        gbc.weightx = 2; // Más espacio horizontal
        gbc.weighty = 3; // Más espacio vertical para evitar que se aplaste
        add(panelDibujo, gbc);

        // 📌 Panel de la palabra a adivinar (ARRIBA DERECHA)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0; // Mantener altura fija
        add(panelPalabra, gbc);
        ajustarTamanioFuente(lblPalabra, panelPalabra);

        // 📌 Panel de letras usadas (ABAJO DERECHA)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1; // Mantiene un peso más bajo para no aplastar el ahorcado
        add(panelLetrasUsadas, gbc);

        // Acción del botón
        btnIntentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intentarLetra(txtLetra, lblLetras, lblPalabra);
            }
        });

        setVisible(true);
    }


    // Método para ajustar el tamaño de la fuente dinámicamente
    private void ajustarTamanioFuente(JLabel label, JPanel panel) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        if (panelWidth <= 0 || panelHeight <= 0) return; // Evitar cálculos innecesarios

        int fontSize = panelHeight / 2;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        FontMetrics fm = panel.getFontMetrics(font);
        int textWidth = fm.stringWidth(label.getText());

        // Aumentar tamaño de fuente mientras el texto sea más estrecho que el panel
        while (textWidth < panelWidth - 20 && fontSize < panelHeight) {
            fontSize++;
            font = font.deriveFont((float) fontSize);
            fm = panel.getFontMetrics(font);
            textWidth = fm.stringWidth(label.getText());
        }

        // Reducir tamaño de fuente si el texto es más ancho que el panel
        while (textWidth > panelWidth - 20 && fontSize > 10) {
            fontSize--;
            font = font.deriveFont((float) fontSize);
            fm = panel.getFontMetrics(font);
            textWidth = fm.stringWidth(label.getText());
        }

        label.setFont(font);
    }

    private void intentarLetra(JTextField txtLetra, JLabel lblLetras, JLabel lblPalabra) {
        String input = txtLetra.getText().toUpperCase();
        txtLetra.setText("");

        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa una sola letra válida.");
            return;
        }

        char letra = input.charAt(0);
        if (letrasIngresadas.contains(letra)) {
            JOptionPane.showMessageDialog(this, "Ya intentaste esta letra.");
            return;
        }

        letrasIngresadas.add(letra);
        boolean acierto = false;

        for (int i = 0; i < palabraSecreta.length(); i++) {
            if (palabraSecreta.charAt(i) == letra) {
                palabraAdivinada[i] = letra;
                acierto = true;
            }
        }

        if (!acierto) {
            intentosRestantes--;
        }

        panelDibujo.repaint();

        // Actualizar las etiquetas
        lblLetras.setText("Letras usadas: " + letrasIngresadas.toString());
        lblPalabra.setText(getPalabraAdivinada());
        ajustarTamanioFuente(lblPalabra, panelPalabra);

        if (String.valueOf(palabraAdivinada).equals(palabraSecreta)) {
            palabrasAcertadas++;
            lblContadorPalabras.setText("Palabras acertadas: " + palabrasAcertadas);

            if (palabrasDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "¡Correcto! La palabra era: " + palabraSecreta +
                                "\nNo hay más palabras disponibles.\nPalabras acertadas: " + palabrasAcertadas);
                System.exit(0);
            }

            JOptionPane.showMessageDialog(this, "¡Correcto! La palabra era: " + palabraSecreta + "\n¡Vamos por otra!");

            // Reiniciar para una nueva palabra
            Random rand = new Random();
            palabraSecreta = palabrasDisponibles.remove(rand.nextInt(palabrasDisponibles.size()));
            palabraAdivinada = new char[palabraSecreta.length()];
            for (int i = 0; i < palabraAdivinada.length; i++) {
                palabraAdivinada[i] = '_';
            }
            letrasIngresadas.clear();
            intentosRestantes = 6;

            // Actualizar interfaz
            lblLetras.setText("Letras usadas: " + letrasIngresadas.toString());
            lblPalabra.setText(getPalabraAdivinada());
            ajustarTamanioFuente(lblPalabra, panelPalabra);
            panelDibujo.repaint();
            return;
        }

        if (intentosRestantes == 0) {
            JOptionPane.showMessageDialog(this, "¡Perdiste! La palabra era: " + palabraSecreta);
            System.exit(0);
        }
    }

    private void dibujarPantalla(Graphics2D g) {
        int width = getWidth();
        int height = getHeight();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuja el ahorcado escalado (sin la palabra)
        dibujarAhorcado(g, width, height);

    }


    private void dibujarAhorcado(Graphics2D g, int width, int height) {
        int mitadAncho = width / 2; // Usamos la mitad de la pantalla
        int baseX = mitadAncho / 4; // Posicionamos el dibujo más a la izquierda
        int baseY = height - height / 4; // Bajamos un poco la base para más espacio arriba
        int tamaño = Math.min(mitadAncho / 2, height / 2); // Ajustamos el tamaño del ahorcado

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(5));

        // Base y estructura de la horca
        g.drawLine(baseX, baseY, baseX + tamaño, baseY); // Base de la horca
        g.drawLine(baseX + tamaño / 2, baseY, baseX + tamaño / 2, baseY - (int) (tamaño * 1.5)); // Poste vertical
        g.drawLine(baseX + tamaño / 2, baseY - (int) (tamaño * 1.5), baseX + tamaño, baseY - (int) (tamaño * 1.5)); // Brazo horizontal
        g.drawLine(baseX + tamaño, baseY - (int) (tamaño * 1.5), baseX + tamaño, baseY - (int) (tamaño * 1.2)); // Cuerda

        // Dibujar partes del ahorcado según intentos restantes
        int cabezaTam = tamaño / 5;
        if (intentosRestantes <= 5) {
            g.drawOval(baseX + tamaño - cabezaTam / 2, baseY - (int) (tamaño * 1.2), cabezaTam, cabezaTam); // Cabeza
        }
        if (intentosRestantes <= 4) {
            g.drawLine(baseX + tamaño, baseY - (int) (tamaño * 1.2) + cabezaTam, baseX + tamaño, baseY - (int) (tamaño * 0.8)); // Cuerpo
        }
        if (intentosRestantes <= 3) {
            g.drawLine(baseX + tamaño, baseY - (int) (tamaño * 1.0), baseX + tamaño - tamaño / 6, baseY - (int) (tamaño * 0.9)); // Brazo izquierdo
        }
        if (intentosRestantes <= 2) {
            g.drawLine(baseX + tamaño, baseY - (int) (tamaño * 1.0), baseX + tamaño + tamaño / 6, baseY - (int) (tamaño * 0.9)); // Brazo derecho
        }
        if (intentosRestantes <= 1) {
            g.drawLine(baseX + tamaño, baseY - (int) (tamaño * 0.8), baseX + tamaño - tamaño / 6, baseY - (int) (tamaño * 0.6)); // Pierna izquierda
        }
        if (intentosRestantes == 0) {
            g.drawLine(baseX + tamaño, baseY - (int) (tamaño * 0.8), baseX + tamaño + tamaño / 6, baseY - (int) (tamaño * 0.6)); // Pierna derecha
        }
    }

    private void dibujarPalabra(Graphics2D g, int width, int height) {
        g.setFont(new Font("Arial", Font.BOLD, width / 20));
        g.setColor(Color.BLUE);
        String palabraFormateada = getPalabraAdivinada();
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(palabraFormateada)) / 2;
        int y = height / 3;
        g.drawString(palabraFormateada, x, y);
    }

    private void dibujarLetrasUsadas(Graphics2D g, int width, int height) {
        g.setFont(new Font("Arial", Font.PLAIN, width / 35)); // Reducimos el tamaño de letra
        g.setColor(Color.RED);
        String letras = "Letras usadas: " + letrasIngresadas.toString();
        FontMetrics fm = g.getFontMetrics();

        int x = (width / 2) + 20; // Ubicarlo más a la derecha
        int y = height - 40; // Más cerca del borde inferior

        g.drawString(letras, x, y);
    }


    private String getPalabraAdivinada() {
        StringBuilder sb = new StringBuilder();
        for (char c : palabraAdivinada) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        new AhorcadoGrafico();
    }
    
    private void cargarPalabrasDesdeArchivo(String nombreArchivo) {
        int opcion = JOptionPane.showOptionDialog(this,
                "¿Cómo deseas cargar las palabras para el juego?",
                "Fuente de Palabras",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Por defecto", "Desde archivo"},
                "Por defecto");

        if (opcion == JOptionPane.YES_OPTION) {
            // Palabras por defecto
            palabrasDisponibles = new ArrayList<>(Arrays.asList(
                    "JAVA", "PROGRAMACION", "COMPUTADORA", "ALGORITMO", "CODIGO"));
            return;
        }

        // Intenta cargar desde archivo
        File archivo = new File(nombreArchivo);

        // 🔹 Si no existe, permitir selección manual
        if (!archivo.exists()) {
        	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        	fileChooser.setDialogTitle("Selecciona el archivo de palabras");
        	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivo = fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se seleccionó ningún archivo. Se usarán palabras por defecto.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
                palabrasDisponibles = new ArrayList<>(Arrays.asList(
                        "JAVA", "PROGRAMACION", "COMPUTADORA", "ALGORITMO", "CODIGO"));
                return;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim().toUpperCase();
                if (!linea.isEmpty()) {
                    palabrasDisponibles.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al leer el archivo seleccionado. Se usarán palabras por defecto.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            palabrasDisponibles = new ArrayList<>(Arrays.asList(
                    "MONTAÑA", "CIELO", "RÍO", "COMPUTADORA", "PELOTA",
                    "SOL", "LLUVIA", "ESPEJO", "CAMINATA", "AVENTURA",
                    "BICICLETA", "PAPEL", "LÁPIZ", "BOSQUE", "PUENTE",
                    "RELOJ", "LIBRO", "CIUDAD", "MUSEO", "CAFÉ",
                    "TÉ", "AVIÓN", "TREN", "COCHE", "PLAYA",
                    "OCÉANO", "UNIVERSO", "PLANETA", "ESTRELLA", "LUZ",
                    "SOMBRA", "SONRISA", "SILENCIO", "DIBUJO", "MÚSICA",
                    "BAILE", "PELÍCULA", "FOTOGRAFÍA", "COCINA", "JARDÍN",
                    "AMISTAD", "FAMILIA", "TRABAJO", "CREATIVIDAD", "PACIENCIA",
                    "ÉXITO", "APRENDIZAJE", "DESCANSO", "DIVERSIÓN", "VIAJE",
                    "NATURALEZA"));
        }
    }
}

