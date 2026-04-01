package csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CsvParser
 *
 * This test class verifies that the CSV parser correctly reads a CSV file,
 * uses the first row as headers, converts later rows into maps, and handles
 * important edge cases such as quoted commas, blank lines, empty files,
 * malformed row lengths, and unmatched quotes.
 */
public class CsvParserTest {

  /**
   * Creates a temporary CSV file with the given content for use in a test.
   *
   * This helper method allows each test to define a small CSV example as a
   * string, write it into a temporary file, and then pass that file to
   * CsvParser.
   *
   * @param content the CSV text to write into the temporary file
   * @return the path to the created temporary CSV file
   * @throws IOException if the temporary file cannot be created or written
   */
  private Path createTempCsv(String content) throws IOException {
    Path tempFile = Files.createTempFile("csv-parser-test", ".csv");
    Files.writeString(tempFile, content);
    tempFile.toFile().deleteOnExit();
    return tempFile;
  }

  /**
   * Tests that the parser correctly reads a CSV file containing one data row.
   *
   * This test checks the most basic successful case:
   * the parser should read the header row, parse one customer row,
   * and return a list containing exactly one map.
   *
   * It also verifies that each header is correctly matched to its
   * corresponding value.
   *
   * @throws IOException if the temporary test file cannot be created
   */
  @Test
  public void testParseSingleRow() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\"Art\",\"Venere\",\"art@venere.org\"\n";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(1, result.size());
    assertEquals("Art", result.get(0).get("first_name"));
    assertEquals("Venere", result.get(0).get("last_name"));
    assertEquals("art@venere.org", result.get(0).get("email"));
  }

  /**
   * Tests that the parser correctly reads multiple data rows.
   *
   * This test verifies that the parser does not stop after the first row,
   * and that it correctly creates one map per customer record in the file.
   *
   * It also checks that values from different rows are stored separately
   * and matched to the correct headers.
   *
   * @throws IOException if the temporary test file cannot be created
   */
  @Test
  public void testParseMultipleRows() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\"Art\",\"Venere\",\"art@venere.org\"\n"
        + "\"James\",\"Butt\",\"james@gmail.com\"\n";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(2, result.size());
    assertEquals("Art", result.get(0).get("first_name"));
    assertEquals("Venere", result.get(0).get("last_name"));
    assertEquals("James", result.get(1).get("first_name"));
    assertEquals("Butt", result.get(1).get("last_name"));
  }

  /**
   * Tests that the parser correctly handles a field containing a comma
   * inside double quotes.
   *
   * A normal approach would incorrectly break the company
   * name into two fields. This test confirms that the parser treats commas
   * inside quoted text as part of the field value rather than as separators.
   *
   * @throws IOException if the temporary test file cannot be created
   */
  @Test
  public void testParseQuotedCommaField() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"company_name\"\n"
        + "\"Art\",\"Venere\",\"Chemel, James L Cpa\"\n";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(1, result.size());
    assertEquals("Chemel, James L Cpa", result.get(0).get("company_name"));
  }

  /**
   * Tests that blank lines in the CSV file are ignored.
   *
   * This test checks that empty lines do not become fake records in the
   * parsed result. Only non-blank data rows should be converted into maps.
   *
   * @throws IOException if the temporary test file cannot be created
   */
  @Test
  public void testSkipBlankLines() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\n"
        + "\"Art\",\"Venere\",\"art@venere.org\"\n"
        + "\n";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(1, result.size());
    assertEquals("Art", result.get(0).get("first_name"));
  }

  /**
   * Tests that an empty CSV file produces an empty result list.
   *
   * <p>This test verifies that the parser handles an empty file safely and
   * does not throw an exception when there is nothing to parse.</p>
   *
   * @throws IOException if the temporary test file cannot be created
   */
  @Test
  public void testEmptyFileReturnsEmptyList() throws IOException {
    String csv = "";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(0, result.size());
  }

  /**
   * Tests that the parser throws an exception when a data row has fewer
   * fields than the header row.
   *
   * This test verifies row validation logic. Since each data row is expected
   * to match the number of headers, a row with missing fields should be treated
   * as malformed CSV and should cause an IllegalArgumentException.
   */
  @Test
  public void testWrongNumberOfFieldsThrowsException() {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\"Art\",\"Venere\"\n";

    CsvParser parser = new CsvParser();

    assertThrows(IllegalArgumentException.class, () ->
        parser.parse(createTempCsv(csv)));
  }

  /**
   * Tests that the parser throws an exception when a quoted field is not
   * properly closed.
   *
   * This test checks malformed CSV input where a double quote is opened
   * but never closed by the end of the line. A well-behaved parser should
   * reject such input instead of silently returning incorrect results.
   *
   * This test assumes the parser implementation includes validation for
   * unmatched quotes.
   */
  @Test
  public void testUnmatchedQuoteThrowsException() {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\"Art\",\"Venere,\"art@venere.org\"\n";

    CsvParser parser = new CsvParser();

    assertThrows(IllegalArgumentException.class, () ->
        parser.parse(createTempCsv(csv)));
  }
}