import command.CommandLineParser;
import command.CommandOptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link CommandLineParser} class.
 */
public class CommandLineParserTest {

  /**
   * Tests that a valid email command with all required arguments
   * is parsed correctly.
   *
   * <p>This test verifies that:
   * <ul>
   *   <li>Email generation is enabled</li>
   *   <li>Letter generation is disabled</li>
   *   <li>No exception is thrown for valid input</li>
   * </ul>
   */
  @Test
  public void testValidEmailCommand() {
    Path projectRoot = Path.of(System.getProperty("user.dir"));
    Path csvPath = projectRoot.resolve("data/insurance-company-members.csv");
    Path emailTemplatePath = projectRoot.resolve("data/email-template.txt");

    String[] args = {
        "--email",
        "--email-template", emailTemplatePath.toString(),
        "--output-dir", "output",
        "--csv-file", csvPath.toString()
    };

    CommandLineParser parser = new CommandLineParser();
    CommandOptions options = parser.parse(args);

    assertTrue(options.shouldGenerateEmail());
    assertFalse(options.shouldGenerateLetter());
  }


  /**
   * Tests that an exception is thrown when the email option
   * is provided without an email template.
   */
  @Test
  public void testMissingEmailTemplate() {
    Path projectRoot = Path.of(System.getProperty("user.dir"));
    Path csvPath = projectRoot.resolve("data/insurance-company-members.csv");

    String[] args = {
        "--email",
        "--output-dir", "output",
        "--csv-file", csvPath.toString()
    };

    CommandLineParser parser = new CommandLineParser();

    assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
  }

  /**
   * Tests that an exception is thrown when neither email
   * nor letter generation is specified.
   */
  @Test
  public void testMissingCommunicationType() {
    Path projectRoot = Path.of(System.getProperty("user.dir"));
    Path csvPath = projectRoot.resolve("data/insurance-company-members.csv");

    String[] args = {
        "--output-dir", "output",
        "--csv-file", csvPath.toString()
    };

    CommandLineParser parser = new CommandLineParser();

    assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
  }

  /**
   * Tests that an exception is thrown when the CSV file path
   * does not exist.
   */
  @Test
  public void testInvalidCsvFile() {
    Path projectRoot = Path.of(System.getProperty("user.dir"));
    Path emailTemplatePath = projectRoot.resolve("data/email-template.txt");

    String[] args = {
        "--email",
        "--email-template", emailTemplatePath.toString(),
        "--output-dir", "output",
        "--csv-file", projectRoot.resolve("data/not-exist.csv").toString()
    };

    CommandLineParser parser = new CommandLineParser();

    assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
  }
}