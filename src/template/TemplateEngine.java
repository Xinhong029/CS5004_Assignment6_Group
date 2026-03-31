package template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides template loading and placeholder replacement functionality.
 *
 * <p>Placeholders in a template are written using the form {@code [[header_name]]}.
 * During rendering, each placeholder is replaced with the corresponding value from
 * a row of CSV data.</p>
 */
public class TemplateEngine {

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");

  /**
   * Reads the full contents of a template file as a string.
   *
   * @param templatePath path to the template file
   * @return the template text
   * @throws IOException if the file cannot be read
   */
  public String readTemplate(Path templatePath) throws IOException {
    return Files.readString(templatePath);
  }

  /**
   * Renders a template string using the provided row data.
   *
   * <p>Each occurrence of {@code [[column_name]]} in the template is replaced with
   * the value mapped to that column name in the row data. If a placeholder key is
   * not found, it is replaced with an empty string.</p>
   *
   * @param template the template text
   * @param rowData one row of CSV data
   * @return the rendered text
   */
  public String render(String template, Map<String, String> rowData) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
    StringBuilder result = new StringBuilder();

    while (matcher.find()) {
      String key = matcher.group(1);
      String replacement = rowData.getOrDefault(key, "");
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }

    matcher.appendTail(result);
    return result.toString();
  }
}