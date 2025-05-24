import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MenuPanel extends JPanel {
    public interface FileSelectionCallback {
        void onFileSelected(File file, String fileType);
    }

    private final FileSelectionCallback callback;
    private JButton graphLoader = new JButton("Graph File");
    private JButton subGraphLoader = new JButton("SubGraph File");
    private JLabel selectedFileLabel = new JLabel("null");
    private JButton resetButton = new JButton("Reset");
    private JButton divideGraphButton = new JButton("Divide");
    private JButton drawGraphButton = new JButton("Draw");

    public MenuPanel(FileSelectionCallback callback) {
        this.callback = callback;
        initUI();
    }

    private void initUI() {
        this.setPreferredSize(new Dimension(800, 40));
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        divideGraphButton.setEnabled(false);
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
            File selectedFile = openFileDialog("Select Graph File");
            if (selectedFile != null) {
                Main.GraphFile = selectedFile;
                selectedFileLabel.setText(selectedFile.getName());
                subGraphLoader.setEnabled(false);
                drawGraphButton.setEnabled(true);
            }
        });
        
        subGraphLoader.addActionListener(e -> {
            File selectedFile = openFileDialog("Select SubGraph File");
            if (selectedFile != null) {
                Main.SubGraphFile = selectedFile;
                selectedFileLabel.setText(selectedFile.getName());
                graphLoader.setEnabled(false);
                drawGraphButton.setEnabled(true);
            }
        });

        drawGraphButton.addActionListener(e -> {
            if (Main.SubGraphFile != null) {
                callback.onFileSelected(Main.SubGraphFile, "SubGraph");
            } else if (Main.GraphFile != null) {
                callback.onFileSelected(Main.GraphFile, "Graph");
            }
        });

        resetButton.addActionListener(e -> {
            selectedFileLabel.setText("null");
            graphLoader.setEnabled(true);
            subGraphLoader.setEnabled(true);
            drawGraphButton.setEnabled(false);
            Main.SubGraphFile = null;
        });
    }

    private File openFileDialog(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        return fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }
}