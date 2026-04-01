package csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses a CSV file and returns each data row as a map.
 *
 * This parser assumes that each record is on one line, the first line is the
 * header row, and fields may be enclosed in double quotes. Commas inside quoted
 * fields are treated as part of the field value.
 */
public class CsvParser {

  /**
   * Parses the given CSV file.
   *
   * @param csvPath path to the CSV file
   * @return a list of row maps, where each map stores header-value pairs
   * @throws IOException if the file cannot be read
   * @throws IllegalArgumentException if a row has the wrong number of fields
   */
  public List<Map<String, String>> parse(Path csvPath) throws IOException {
    List<String> lines = Files.readAllLines(csvPath);
    List<Map<String, String>> result = new ArrayList<>();

    if (lines.isEmpty()) {
      return result;
    }

    // First line contains the column headers
    List<String> headers = parseLine(lines.get(0));

    // Process each remaining line as one data row
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i).trim();

      // Skip blank lines
      if (line.isEmpty()) {
        continue;
      }

      List<String> values = parseLine(line);

      if (values.size() != headers.size()) {
        throw new IllegalArgumentException(
            "Malformed CSV: row " + (i + 1) + " has " + values.size()
                + " fields, but expected " + headers.size() + "."
        );
      }

      Map<String, String> rowMap = new LinkedHashMap<>();
      for (int j = 0; j < headers.size(); j++) {
        rowMap.put(headers.get(j), values.get(j));
      }

      result.add(rowMap);
    }

    return result;
  }

  /**
   * Parses one line of CSV text into a list of field values.
   *
   * <p>This method handles commas inside quoted fields by tracking whether the
   * parser is currently inside double quotes.</p>
   *
   * @param line one line from the CSV file
   * @return the parsed field values from that line
   */
  private List<String> parseLine(String line) {
    List<String> fields = new ArrayList<>();
    StringBuilder currentField = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        // Enter or leave quoted mode
        inQuotes = !inQuotes;
      } else if (c == ',' && !inQuotes) {
        // Comma outside quotes ends the current field
        fields.add(currentField.toString());
        currentField.setLength(0);
      } else {
        // Regular character becomes part of the field
        currentField.append(c);
      }
    }

    // Add the last field
    fields.add(currentField.toString());

    return fields;
  }
}