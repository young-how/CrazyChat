package org.DUT.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class ConfigEditor extends JFrame {
    private Properties properties;
    private JPanel panel;
    private JButton saveButton;

    public ConfigEditor() {
        setTitle("Config Properties Editor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        properties = new Properties();
        panel = new JPanel(new GridBagLayout());
        saveButton = new JButton("Save");

        loadProperties();
        createUI();

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProperties();
            }
        });

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Sorry, unable to find config.properties", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;
        for (String key : properties.stringPropertyNames()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel(key), gbc);

            gbc.gridx = 1;
            JTextField valueField = new JTextField(properties.getProperty(key), 20);
            valueField.setName(key);
            panel.add(valueField, gbc);

            row++;
        }
    }

    private void saveProperties() {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                String key = field.getName();
                String value = field.getText();
                properties.setProperty(key, value);
            }
        }

        try (OutputStream output = new FileOutputStream(getClass().getClassLoader().getResource("config.properties").getPath())) {
            properties.store(output, null);
            JOptionPane.showMessageDialog(this, "Properties saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving properties.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConfigEditor().setVisible(true);
            }
        });
    }
}