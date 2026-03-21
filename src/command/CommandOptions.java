package command;

import java.nio.file.Path;

/**
 * Represents validated command line options for the insurance communication program.
 *
 * <p>An object of this class stores all user-selected options, including whether
 * email and/or letter output should be generated, the corresponding template file
 * paths, the CSV input file path, and the output directory.</p>
 */
public class CommandOptions {
  private final boolean generateEmail;
  private final boolean generateLetter;
  private final Path emailTemplate;
  private final Path letterTemplate;
  private final Path outputDir;
  private final Path csvFile;

  /**
   * Constructs a {@code CommandOptions} object with all parsed values.
   *
   * @param generateEmail whether email messages should be generated
   * @param generateLetter whether letters should be generated
   * @param emailTemplate path to the email template file, or null if not used
   * @param letterTemplate path to the letter template file, or null if not used
   * @param outputDir path to the output directory
   * @param csvFile path to the CSV input file
   */
  public CommandOptions(boolean generateEmail, boolean generateLetter,
      Path emailTemplate, Path letterTemplate,
      Path outputDir, Path csvFile) {
    this.generateEmail = generateEmail;
    this.generateLetter = generateLetter;
    this.emailTemplate = emailTemplate;
    this.letterTemplate = letterTemplate;
    this.outputDir = outputDir;
    this.csvFile = csvFile;
  }

  /**
   * Returns whether email files should be generated.
   *
   * @return true if email generation is enabled
   */
  public boolean shouldGenerateEmail() {
    return generateEmail;
  }

  /**
   * Returns whether letter files should be generated.
   *
   * @return true if letter generation is enabled
   */
  public boolean shouldGenerateLetter() {
    return generateLetter;
  }

  /**
   * Returns the email template path.
   *
   * @return the email template path
   */
  public Path getEmailTemplate() {
    return emailTemplate;
  }

  /**
   * Returns the letter template path.
   *
   * @return the letter template path
   */
  public Path getLetterTemplate() {
    return letterTemplate;
  }

  /**
   * Returns the output directory path.
   *
   * @return the output directory path
   */
  public Path getOutputDir() {
    return outputDir;
  }

  /**
   * Returns the CSV file path.
   *
   * @return the CSV file path
   */
  public Path getCsvFile() {
    return csvFile;
  }
}