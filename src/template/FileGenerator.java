package template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Generates output files from templates and CSV row data.
 *
 * <p>This class creates one output file per CSV row. Output filenames are based
 * on the customer's first and last name together with the output type
 * (for example, email or letter).</p>
 */
public class FileGenerator {

  /**
   * Generates one output file per record using the provided template.
   *
   * @param records the CSV records to process
   * @param template the template text
   * @param outputDir the output directory
   * @param type the output type, such as "email" or "letter"
   * @param templateEngine the template engine used for rendering
   * @throws IOException if a file cannot be written
   */
  public void generateFiles(List<Map<String, String>> records,
      String template,
      Path outputDir,
      String type,
      TemplateEngine templateEngine) throws IOException {
    Files.createDirectories(outputDir);

    for (int i = 0; i < records.size(); i++) {
      Map<String, String> row = records.get(i);
      String rendered = templateEngine.render(template, row);
      String fileName = buildFileName(row, type, i + 1);
      Path outputFile = outputDir.resolve(fileName);
      Files.writeString(outputFile, rendered);
    }
  }

  /**
   * Builds a safe output filename for a given record.
   *
   * <p>The filename format is based on the customer's first and last name and the
   * requested output type. If either name is missing, a fallback record number is used.</p>
   *
   * @param row one row of CSV data
   * @param type the output type
   * @param recordNumber the 1-based record number
   * @return a filename ending in {@code .txt}
   */
  private String buildFileName(Map<String, String> row, String type, int recordNumber) {
    String firstName = sanitize(row.getOrDefault("first_name", ""));
    String lastName = sanitize(row.getOrDefault("last_name", ""));

    if (!firstName.isEmpty() && !lastName.isEmpty()) {
      return firstName + "_" + lastName + "_" + type + ".txt";
    } else if (!firstName.isEmpty()) {
      return firstName + "_" + type + ".txt";
    } else if (!lastName.isEmpty()) {
      return lastName + "_" + type + ".txt";
    } else {
      return "record_" + recordNumber + "_" + type + ".txt";
    }
  }

  /**
   * Sanitizes a string for safe use in a filename.
   *
   * <p>Characters that are not letters, digits, underscores, or hyphens are replaced
   * with underscores. Leading and trailing whitespace is removed.</p>
   *
   * @param value the input string
   * @return a sanitized filename fragment
   */
  private String sanitize(String value) {
    return value.trim().replaceAll("[^a-zA-Z0-9_-]+", "_");
  }
}
