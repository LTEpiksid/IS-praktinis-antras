package org.example;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptionDecryptionForm {
    private final JFrame frame;
    private final JTextField inputTextField;
    private final JTextField keyTextField;
    private final JTextField ivTextField;
    private final JTextArea resultTextArea;
    private final JComboBox<String> modeComboBox;
    private final JComboBox<Integer> keySizeComboBox;
    private final JComboBox<String> outputFormatComboBox;
    private static final Logger LOGGER = Logger.getLogger(EncryptionDecryptionForm.class.getName());

    public EncryptionDecryptionForm(String mode) {
        frame = new JFrame("Encryption/Decryption (AES Mode: " + mode + ")");
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel inputLabel = new JLabel("Input Text:");
        JLabel keyLabel = new JLabel("Encryption Key:");
        JLabel ivLabel = new JLabel("Initialization Vector:");
        JLabel modeLabel = new JLabel("AES Mode:");
        JLabel resultLabel = new JLabel("Result:");
        JLabel keySizeLabel = new JLabel("Key Size (bits):");
        JLabel outputFormatLabel = new JLabel("Output Format:");

        inputTextField = new JTextField(20);
        keyTextField = new JTextField(20);
        ivTextField = new JTextField(20);
        resultTextArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        String[] modes = {"ECB", "CBC"};
        modeComboBox = new JComboBox<>(modes);
        modeComboBox.setSelectedItem(mode);
        modeComboBox.addActionListener(this::onModeChange);

        Integer[] keySizes = {128, 192, 256};
        keySizeComboBox = new JComboBox<>(keySizes);
        keySizeComboBox.setSelectedItem(128); // Default key size
        keySizeComboBox.addActionListener(this::onKeySizeChange);

        String[] outputFormats = {"Base64", "Hex"};
        outputFormatComboBox = new JComboBox<>(outputFormats);
        outputFormatComboBox.setSelectedItem("Base64"); // Default output format

        inputPanel.add(inputLabel);
        inputPanel.add(inputTextField);
        inputPanel.add(keyLabel);
        inputPanel.add(keyTextField);
        inputPanel.add(modeLabel);
        inputPanel.add(modeComboBox);
        inputPanel.add(ivLabel);
        inputPanel.add(ivTextField);
        inputPanel.add(keySizeLabel);
        inputPanel.add(keySizeComboBox);
        inputPanel.add(outputFormatLabel);
        inputPanel.add(outputFormatComboBox);

        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(this::encrypt);

        JButton decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(this::decrypt);

        JButton saveButton = new JButton("Save to File");
        saveButton.addActionListener(this::saveToFile);

        JButton loadButton = new JButton("Load from File");
        loadButton.addActionListener(this::loadFromFile);

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setSize(500, 400); // Adjusted size
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void onModeChange(ActionEvent e) {
        String selectedMode = (String) modeComboBox.getSelectedItem();
        if ("CBC".equals(selectedMode)) {
            ivTextField.setEnabled(true);
        } else {
            ivTextField.setEnabled(false);
        }
    }

    private void onKeySizeChange(ActionEvent e) {
        // You can add logic here to handle key size change if needed
    }

    private void encrypt(ActionEvent e) {
        try {
            String inputText = inputTextField.getText();
            String keyText = keyTextField.getText();
            int keySize = (int) keySizeComboBox.getSelectedItem();

            // Validate key size
            if (keyText.length() != keySize / 8) {
                JOptionPane.showMessageDialog(frame, "Invalid key size. Please provide a key of " + keySize + " bits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert the key text to bytes
            byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            // Get the cipher instance for ECB mode
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt the input text
            byte[] encryptedBytes = cipher.doFinal(inputText.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes based on the selected output format
            String outputFormat = (String) outputFormatComboBox.getSelectedItem();
            String encryptedText;
            if ("Base64".equals(outputFormat)) {
                encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            } else { // Hex encoding
                encryptedText = bytesToHex(encryptedBytes);
            }

            // Display the result in the text area
            resultTextArea.setText(encryptedText);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Encryption failed", ex);
            JOptionPane.showMessageDialog(frame, "Encryption failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decrypt(ActionEvent e) {
        try {
            String inputText = inputTextField.getText();
            String keyText = keyTextField.getText();
            int keySize = (int) keySizeComboBox.getSelectedItem();

            // Validate key size
            if (keyText.length() != keySize / 8) {
                JOptionPane.showMessageDialog(frame, "Invalid key size. Please provide a key of " + keySize + " bits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert the key text to bytes
            byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            // Get the cipher instance for ECB mode
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decode the input text based on the selected output format
            byte[] inputBytes;
            if ("Base64".equals(outputFormatComboBox.getSelectedItem())) {
                inputBytes = Base64.getDecoder().decode(inputText);
            } else { // Hex encoding
                inputBytes = hexToBytes(inputText);
            }

            // Decrypt the input bytes
            byte[] decryptedBytes = cipher.doFinal(inputBytes);

            // Display the result in the text area
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            resultTextArea.setText(decryptedText);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Decryption failed", ex);
            JOptionPane.showMessageDialog(frame, "Decryption failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void saveToFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(resultTextArea.getText());
                JOptionPane.showMessageDialog(frame, "Text saved to file successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to save text to file", ex);
                JOptionPane.showMessageDialog(frame, "Failed to save text to file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFromFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }
                inputTextField.setText(text.toString().trim());
                JOptionPane.showMessageDialog(frame, "Text loaded from file successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to load text from file", ex);
                JOptionPane.showMessageDialog(frame, "Failed to load text from file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EncryptionDecryptionForm("ECB"));
    }
}
