import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private static JTextField dataInputField;
    private static JComboBox<String> bitSizeSelector;
    private static JTextArea outputArea;
    private static JPanel memoryPanel;

    private static String currentHammingCode = "";
    private static int lastErrorPosition = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hamming SEC-DED Simülatörü");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLayout(new BorderLayout());

            
            JPanel topPanel = new JPanel(new FlowLayout());

            bitSizeSelector = new JComboBox<>(new String[]{"8", "16", "32"});
            topPanel.add(new JLabel("Bit Sayısı Seçin:"));
            topPanel.add(bitSizeSelector);

            dataInputField = new JTextField(32);
            topPanel.add(new JLabel("Veri Girişi (0-1):"));
            topPanel.add(dataInputField);

            JButton encodeButton = new JButton("Kodla");
            topPanel.add(encodeButton);

            JButton detectButton = new JButton("Hata Tespit Et ve Düzelt");
            topPanel.add(detectButton);

            frame.add(topPanel, BorderLayout.NORTH);

           
            memoryPanel = new JPanel();
            memoryPanel.setLayout(new FlowLayout());
            JScrollPane scrollPane = new JScrollPane(memoryPanel);
            scrollPane.setPreferredSize(new Dimension(850, 150));
            frame.add(scrollPane, BorderLayout.CENTER);

            
            outputArea = new JTextArea(10, 70);
            outputArea.setEditable(false);
            frame.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

            
            bitSizeSelector.addActionListener(e -> updateInputFieldLength());
            encodeButton.addActionListener(e -> encodeData());
            detectButton.addActionListener(e -> detectAndCorrectError());

            updateInputFieldLength();
            frame.setVisible(true);
        });
    }

    private static void updateInputFieldLength() {
        int bitLength = Integer.parseInt((String) bitSizeSelector.getSelectedItem());
        dataInputField.setText("0".repeat(bitLength));
    }

    private static void encodeData() {
        String input = dataInputField.getText().trim();
        if (!input.matches("[01]+")) {
            outputArea.setText("Hata: Sadece 0 ve 1 girilebilir.");
            return;
        }

        int bitLength = Integer.parseInt((String) bitSizeSelector.getSelectedItem());
        if (input.length() != bitLength) {
            outputArea.setText("Hata: Girdi uzunluğu " + bitLength + " bit olmalıdır.");
            return;
        }

        currentHammingCode = HammingEncoder.encode(input);
        lastErrorPosition = 0;
        outputArea.setText("Girdi: " + input + "\nHamming Kodu: " + currentHammingCode);
        renderMemoryBits();
    }

    private static void renderMemoryBits() {
        memoryPanel.removeAll();

        for (int i = 0; i < currentHammingCode.length(); i++) {
            int bitIndex = i;
            char bit = currentHammingCode.charAt(i);
            JButton bitButton = new JButton(String.valueOf(bit));
            bitButton.setPreferredSize(new Dimension(40, 40));

            String tooltip = "Pozisyon: " + (i + 1);
            Color bgColor;

            if (lastErrorPosition == i + 1) {
                bgColor = Color.RED;
                tooltip += " (Hatalı bit)";
            } else if (lastErrorPosition == -(i + 1)) {
                bgColor = Color.GREEN;
                tooltip += " (Düzeltilmiş bit)";
            } else if (isParityPosition(i + 1)) {
                bgColor = Color.CYAN;
                tooltip += " (Parite biti)";
            } else {
                bgColor = Color.LIGHT_GRAY;
                tooltip += " (Veri biti)";
            }

            bitButton.setBackground(bgColor);
            bitButton.setToolTipText(tooltip);

            bitButton.addActionListener(e -> {
                char[] bits = currentHammingCode.toCharArray();
                bits[bitIndex] = bits[bitIndex] == '0' ? '1' : '0';
                currentHammingCode = new String(bits);
                lastErrorPosition = 0;
                bitButton.setText(String.valueOf(bits[bitIndex]));
                outputArea.setText("Bellek Güncellendi (Hata Simülasyonu):\n" + currentHammingCode);
                renderMemoryBits();
            });

            memoryPanel.add(bitButton);
        }

        memoryPanel.revalidate();
        memoryPanel.repaint();
    }

    private static boolean isParityPosition(int position) {
        return (position & (position - 1)) == 0;
    }

    private static void detectAndCorrectError() {
        if (currentHammingCode.isEmpty()) {
            outputArea.setText("Önce bir Hamming kodu üretmelisiniz.");
            return;
        }

        int errorPos = HammingDecoder.detectErrorPosition(currentHammingCode);
        StringBuilder result = new StringBuilder();

        if (errorPos == 0) {
            result.append("Hata tespit edilmedi.\n");
        } else {
            result.append("Hatalı bit pozisyonu: ").append(errorPos).append("\n");
            lastErrorPosition = errorPos;

            currentHammingCode = HammingDecoder.correctCode(currentHammingCode, errorPos);
            result.append("Düzeltilmiş Hamming Kodu: ").append(currentHammingCode).append("\n");

           
            lastErrorPosition = -errorPos;
        }

        String extractedData = HammingDecoder.extractDataBits(currentHammingCode);
        result.append("Çözümlenen Veri: ").append(extractedData);
        outputArea.setText(result.toString());
        renderMemoryBits();
    }
}


