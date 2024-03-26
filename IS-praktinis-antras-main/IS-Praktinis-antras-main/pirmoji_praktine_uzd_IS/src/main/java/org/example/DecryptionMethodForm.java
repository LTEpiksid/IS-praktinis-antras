package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class DecryptionMethodForm {

    public DecryptionMethodForm() {
        JFrame frame = new JFrame("Choose Decryption Method");
        JPanel panel = new JPanel(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Logo.png")));
        JLabel imageLabel = new JLabel(imageIcon);

        JButton btnAESMethod = new JButton("Use AES Method");

        btnAESMethod.addActionListener(e -> new EncryptionDecryptionForm("AES"));

        imageLabel.setPreferredSize(new Dimension(200, 200));

        panel.add(imageLabel, BorderLayout.NORTH);
        panel.add(btnAESMethod, BorderLayout.CENTER);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DecryptionMethodForm::new);
    }
}
