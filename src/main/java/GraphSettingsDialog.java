import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class GraphSettingsDialog extends JDialog {
    private final JTextField partsField;
    private final JTextField marginField;
    private File selectedFile;
    private boolean confirmed = false;

    public GraphSettingsDialog(Frame parent) {
        super(parent, "Graph Division Settings", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel partsLabel = new JLabel("Number of parts:");
        partsField = new JTextField("7");
        JLabel marginLabel = new JLabel("Margin (%):");
        marginField = new JTextField("40");

        JButton fileButton = new JButton("Select Graph File");
        JLabel fileLabel = new JLabel("No file selected");

        JButton okButton = new JButton("Divide");
        JButton cancelButton = new JButton("Cancel");

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Graph File");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileLabel.setText(selectedFile.getName());
            }
        });

        okButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        add(partsLabel);
        add(partsField);
        add(marginLabel);
        add(marginField);
        add(fileButton);
        add(fileLabel);
        add(okButton);
        add(cancelButton);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public int getNumParts() {
        try {
            int parts = Integer.parseInt(partsField.getText());
            return parts > 0 ? parts : 7;
        } catch (NumberFormatException e) {
            return 7;
        }
    }

    public int getMargin() {
        try {
            int margin = Integer.parseInt(marginField.getText());
            return (margin >= 0 && margin <= 100) ? margin : 40;
        } catch (NumberFormatException e) {
            return 40;
        }
    }
}