import command.CommandLineParser;
import command.CommandOptions;
import csv.CsvParser;
import template.FileGenerator;
import template.TemplateEngine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the insurance communication program.
 * This class coordinates command parsing, CSV reading,
 * template processing, and file generation.
 */
public class Main {

  /**
   * Main method that runs the program.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {

    CommandLineParser parser = new CommandLineParser();

    try {

      CommandOptions options = parser.parse(args);

      CsvParser csvParser = new CsvParser();
      List<Map<String, String>> records =
          csvParser.parse(options.getCsvFile());

      TemplateEngine templateEngine = new TemplateEngine();
      FileGenerator fileGenerator = new FileGenerator();

      if (options.shouldGenerateEmail()) {

        String emailTemplate =
            templateEngine.readTemplate(options.getEmailTemplate());

        fileGenerator.generateFiles(
            records,
            emailTemplate,
            options.getOutputDir(),
            "email",
            templateEngine
        );
      }

      if (options.shouldGenerateLetter()) {

        String letterTemplate =
            templateEngine.readTemplate(options.getLetterTemplate());

        fileGenerator.generateFiles(
            records,
            letterTemplate,
            options.getOutputDir(),
            "letter",
            templateEngine
        );
      }

      System.out.println("Finished.");

    } catch (IllegalArgumentException e) {

      System.err.println(e.getMessage());
      System.exit(1);

    } catch (IOException e) {

      System.err.println(e.getMessage());
      System.exit(1);

    }
  }
}