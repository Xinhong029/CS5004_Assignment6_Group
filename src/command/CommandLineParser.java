package command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parses and validates command line arguments for the insurance communication program.
 *
 * <p>This parser supports the following options in any order:</p>
 * <ul>
 *   <li>{@code --email}</li>
 *   <li>{@code --email-template <path>}</li>
 *   <li>{@code --letter}</li>
 *   <li>{@code --letter-template <path>}</li>
 *   <li>{@code --output-dir <path>}</li>
 *   <li>{@code --csv-file <path>}</li>
 * </ul>
 *
 * <p>It validates required dependencies between options and returns a
 * {@link CommandOptions} object if all inputs are legal.</p>
 */
public class CommandLineParser {

  /**
   * Parses the given command line arguments into a validated {@link CommandOptions} object.
   *
   * @param args command line arguments
   * @return validated command options
   * @throws IllegalArgumentException if the arguments are invalid or incomplete
   */
  public CommandOptions parse(String[] args) {
    boolean generateEmail = false;
    boolean generateLetter = false;
    Path emailTemplate = null;
    Path letterTemplate = null;
    Path outputDir = null;
    Path csvFile = null;

    int i = 0;
    while (i < args.length) {
      String arg = args[i];

      switch (arg) {
        case "--email":
          generateEmail = true;
          i++;
          break;

        case "--letter":
          generateLetter = true;
          i++;
          break;

        case "--email-template":
          emailTemplate = readRequiredPathArgument(args, i, "--email-template");
          i += 2;
          break;

        case "--letter-template":
          letterTemplate = readRequiredPathArgument(args, i, "--letter-template");
          i += 2;
          break;

        case "--output-dir":
          outputDir = readRequiredPathArgument(args, i, "--output-dir");
          i += 2;
          break;

        case "--csv-file":
          csvFile = readRequiredPathArgument(args, i, "--csv-file");
          i += 2;
          break;

        default:
          throw new IllegalArgumentException("Unknown option: " + arg);
      }
    }

    validate(generateEmail, generateLetter, emailTemplate, letterTemplate, outputDir, csvFile);

    return new CommandOptions(
        generateEmail,
        generateLetter,
        emailTemplate,
        letterTemplate,
        outputDir,
        csvFile
    );
  }

  /**
   * Reads the required path argument immediately following an option.
   *
   * @param args all command line arguments
   * @param currentIndex the index of the option
   * @param optionName the option name
   * @return the parsed path
   * @throws IllegalArgumentException if the argument is missing or is another option
   */
  private Path readRequiredPathArgument(String[] args, int currentIndex, String optionName) {
    if (currentIndex + 1 >= args.length) {
      throw new IllegalArgumentException("Missing argument for option: " + optionName);
    }

    String value = args[currentIndex + 1];
    if (value.startsWith("--")) {
      throw new IllegalArgumentException("Missing argument for option: " + optionName);
    }

    return Paths.get(value);
  }

  /**
   * Validates the parsed options according to the assignment rules.
   *
   * @param generateEmail whether email generation was requested
   * @param generateLetter whether letter generation was requested
   * @param emailTemplate path to email template
   * @param letterTemplate path to letter template
   * @param outputDir path to output directory
   * @param csvFile path to CSV file
   * @throws IllegalArgumentException if any validation rule fails
   */
  private void validate(boolean generateEmail, boolean generateLetter,
      Path emailTemplate, Path letterTemplate,
      Path outputDir, Path csvFile) {

    if (!generateEmail && !generateLetter) {
      throw new IllegalArgumentException(
          "You must specify at least one of --email or --letter."
      );
    }

    if (generateEmail && emailTemplate == null) {
      throw new IllegalArgumentException(
          "--email requires --email-template to also be provided."
      );
    }

    if (generateLetter && letterTemplate == null) {
      throw new IllegalArgumentException(
          "--letter requires --letter-template to also be provided."
      );
    }

    if (outputDir == null) {
      throw new IllegalArgumentException("--output-dir is required.");
    }

    if (csvFile == null) {
      throw new IllegalArgumentException("--csv-file is required.");
    }

    if (!Files.exists(csvFile) || !Files.isRegularFile(csvFile)) {
      throw new IllegalArgumentException("CSV file does not exist: " + csvFile);
    }

    if (emailTemplate != null && (!Files.exists(emailTemplate) || !Files.isRegularFile(emailTemplate))) {
      throw new IllegalArgumentException("Email template file does not exist: " + emailTemplate);
    }

    if (letterTemplate != null && (!Files.exists(letterTemplate) || !Files.isRegularFile(letterTemplate))) {
      throw new IllegalArgumentException("Letter template file does not exist: " + letterTemplate);
    }
  }

}