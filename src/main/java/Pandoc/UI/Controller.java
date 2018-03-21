package Pandoc.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.concurrent.Task;

import Pandoc.Native.Operations;
import Pandoc.Types.Format;

public class Controller {

    /* Ui Components */
    @FXML
    private TextField inputFile;
    @FXML
    private TextField outputFile;
    @FXML
    private ComboBox<? extends String> outputFormat;
    @FXML
    private Button convertButton;
    @FXML
    private ProgressIndicator processIndicator;

    /* Controller Fields */
    final FileChooser fileChooser = new FileChooser();
    private boolean inputSet = false;
    private boolean outputSet = false;
    private boolean formatSelected = false;
    private String extensionDescription;
    private String extensionType;

    @FXML
    public void initialize() {
        if (Main.arguments.length > 0) {
            inputFile.setText(Main.arguments[0]);
        }
        if (Main.arguments.length > 1) {
            outputFile.setText(Main.arguments[1]);
        }
    }

    @FXML
    protected void setInputFile(ActionEvent event) {
        configureFileChooserInput(fileChooser);
        File file = fileChooser.showOpenDialog(Main.stage);
        if (checkIfFileExists(file)) {
            setFilePath(false, file.getPath());
            setConvertVisible(false);
            setInputType(file.getPath());
        }
    }

    @FXML
    protected void setOutputFile(ActionEvent event) {
        configureFileChooserSaveFormat(fileChooser);
        File file = fileChooser.showSaveDialog(Main.stage);
        if (checkIfFileExists(file)) {
            setFilePath(true, file.getPath());
            setConvertVisible(true);
        }
    }

    @FXML
    protected void formatSelected() {
        setOutputSaveDialogOptions(outputFormat.getValue());
        setOutputIfNotCustom();
    }

    @FXML
    protected void convertDocument(ActionEvent event) {
        processIndicator.setVisible(true);

        Task task = new Task<Void>() {
            @Override public Void call() {
                Operations.setFileLocations(inputFile.getText(), outputFile.getText());
                Operations.executeCommand();
                processIndicator.setVisible(false);
                return null;
            }
        };
        new Thread(task).start();
    }

    private void configureFileChooserSaveFormat(final FileChooser fileChooser) {
        String filePath = outputFile.getText();
        String outputFolder;
        int index;
        String fileName;

        fileChooser.setTitle("Save output");
        try {
            index = filePath.lastIndexOf("\\");
            outputFolder = filePath.substring(0, index);
            fileName = filePath.substring(index + 1);
        } catch (Exception e) {
            index = filePath.lastIndexOf("/");
            outputFolder = filePath.substring(0, index);
            fileName = filePath.substring(index + 1);
        }
        fileChooser.setInitialDirectory(new File(outputFolder));
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter(extensionDescription, extensionType)
        );
    }

    private static void configureFileChooserInput(final FileChooser fileChooser) {
        fileChooser.setTitle("Input file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Any File", "*.*")
        );
    }

    private boolean checkIfFileExists(File file) {
        return file != null;
    }

    private void setFilePath(boolean isOutput, String path) {
        if (isOutput) {
            outputFile.setText(path);
            outputSet = true;
        } else {
            inputFile.setText(path);
            inputSet = true;
        }
    }

    private void setConvertVisible(boolean isOutput) {
        if (inputSet && outputSet) {
            convertButton.setVisible(true);
        }
    }

    private void setOutputSaveDialogOptions(String format) {
        if (format.equals("Pdf")) {
            extensionDescription = "PDF (*.pdf)";
            extensionType = "*.pdf";
            Operations.setOutputFormat(Format.PDF);
        } else if (format.equals("Word")) {
            extensionDescription = "Word Document (*.docx)";
            extensionType = "*.docx";
            Operations.setOutputFormat(Format.WORD);
        } else if (format.equals("Html")) {
            extensionDescription = "Html file (*.html)";
            extensionType = "*.html";
            Operations.setOutputFormat(Format.HTML);
        } else if (format.equals("Markdown")) {
            extensionDescription = "Markdown (*.md)";
            extensionType = "*.md";
            Operations.setOutputFormat(Format.MARKDOWN);
        }
    }

    private void setOutputIfNotCustom() {
        if (inputSet) {
            String inputPath = inputFile.getText();
            String output = inputPath.substring(0, inputPath.lastIndexOf("."));
            output += extensionType.substring(1);
            setFilePath(true, output);
            setConvertVisible(true);
        }
    }

    private void setInputType(String path) {
        String inputExtension = path.substring(path.lastIndexOf(".") + 1);
        if (inputExtension.equalsIgnoreCase("md")) {
            Operations.setInputFormat(Format.MARKDOWN);
        } else if (inputExtension.equalsIgnoreCase("docx")) {
            Operations.setInputFormat(Format.WORD);
        }
    }
}
