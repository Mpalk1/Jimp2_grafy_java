import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MenuPanel extends JPanel {
    public interface FileSelectionCallback {
        void onFileSelected(File file, String fileType, int numParts, int margin);
    }

    private final FileSelectionCallback callback;
    private JButton graphLoader = new JButton("Graph File");
    private JButton subGraphLoader = new JButton("SubGraph File");
    private JLabel selectedFileLabel = new JLabel("null");
    private JButton resetButton = new JButton("Reset");
    private JButton drawGraphButton = new JButton("Draw");
    private File currentFile;
    private String currentFileType;
    private int numParts = 7;
    private int margin = 40;

    public MenuPanel(FileSelectionCallback callback) {
        this.callback = callback;
        initUI();
    }

    private void initUI() {
        this.setPreferredSize(new Dimension(800, 40));
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        drawGraphButton.setEnabled(false);

        selectedFileLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        selectedFileLabel.setFont(new Font("Arial", Font.BOLD, 12));
        resetButton.setBackground(new Color(220, 220, 220));
        resetButton.setMargin(new Insets(2, 10, 2, 10));
        drawGraphButton.setBackground(new Color(200, 200, 230));

        this.add(graphLoader);
        this.add(subGraphLoader);
        this.add(selectedFileLabel);
        this.add(resetButton);
        this.add(drawGraphButton);

        graphLoader.addActionListener(e -> {
            GraphSettingsDialog dialog = new GraphSettingsDialog((Frame)SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            if (dialog.isConfirmed() && dialog.getSelectedFile() != null) {
                currentFile = dialog.getSelectedFile();
                currentFileType = "Graph";
                selectedFileLabel.setText(currentFile.getName());
                subGraphLoader.setEnabled(false);
                drawGraphButton.setEnabled(true);
                numParts = dialog.getNumParts();
                margin = dialog.getMargin();
            }
        });

        subGraphLoader.addActionListener(e -> {
            File selectedFile = openFileDialog("Select SubGraph File");
            if (selectedFile != null) {
                currentFile = selectedFile;
                currentFileType = "SubGraph";
                selectedFileLabel.setText(selectedFile.getName());
                graphLoader.setEnabled(false);
                drawGraphButton.setEnabled(true);
            }
        });

        drawGraphButton.addActionListener(e -> {
            if (currentFile != null) {
                if ("Graph".equals(currentFileType)) {
                    callback.onFileSelected(currentFile, currentFileType, numParts, margin);
                } else {
                    callback.onFileSelected(currentFile, currentFileType, 0, 0);
                }
            }
        });

        resetButton.addActionListener(e -> {
            selectedFileLabel.setText("null");
            graphLoader.setEnabled(true);
            subGraphLoader.setEnabled(true);
            drawGraphButton.setEnabled(false);
            currentFile = null;
            currentFileType = null;
            callback.onFileSelected(null, "Reset", 0, 0);
        });
    }

    private File openFileDialog(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        return fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }
}