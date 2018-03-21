package Pandoc.Native;

import Pandoc.Types.Format;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import sun.misc.FormattedFloatingDecimal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Process;
import java.io.IOException;
import java.util.ArrayList;

public class Operations {
    private static String inputPath;
    private static String outputPath;
    private static String args;
    private static Format inputFormat;
    private static Format outputFormat;

    public static void setInputFormat(Format format) {
        inputFormat = format;
    }

    public static void setOutputFormat(Format format) {
        outputFormat = format;
    }

    public static boolean checkForPandoc() {
        try {
            String line;
            Process pandoc = new ProcessBuilder("pandoc", "-v").start();
            BufferedReader input = new BufferedReader(new InputStreamReader(pandoc.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setFileLocations(String input, String output) {
        inputPath = input;
        outputPath = output;
    }

    public static void executeCommand() {
        try {
            ArrayList<String> process = createCommand();
            Process pandoc = new ProcessBuilder(process).start();
            //int i = pandoc.exitValue();
            String line;
            BufferedReader output = new BufferedReader(new InputStreamReader(pandoc.getErrorStream()));
            while ((line = output.readLine()) != null) {
                System.out.println(line);
            }
            output.close();
        } catch (IOException e) {

        }
    }

    private static ArrayList<String> createCommand() {
        ArrayList<String> command = new ArrayList<String>();
        command.add("pandoc");
        command.add("-s");
        command.add(inputPath);
        command = addArgsToCommand(command);
        command.add("-o");
        command.add(outputPath);
        return command;
    }

    private static ArrayList<String> addArgsToCommand(ArrayList<String> command) {
        if (inputFormat == Format.MARKDOWN && outputFormat == Format.PDF) {
            command.add("-V");
            command.add("geometry:letterpaper");
            command.add("-V");
            command.add("geometry:margin=3cm");
            command.add("-V");
            command.add("fontsize=12pt");
        } else if (inputFormat == Format.WORD && outputFormat == Format.MARKDOWN) {
            command.add("--wrap=none");
        }
        return command;
    }
}
