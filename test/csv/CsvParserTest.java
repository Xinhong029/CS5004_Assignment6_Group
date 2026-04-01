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
 * Tests for CsvParser.
 */
public class CsvParserTest {

  /**
   * Creates a temporary CSV file with the given content.
   *
   * @param content CSV text content
   * @return path to the temporary file
   * @throws IOException if the file cannot be created
   */
  private Path createTempCsv(String content) throws IOException {
    Path tempFile = Files.createTempFile("csv-parser-test", ".csv");
    Files.writeString(tempFile, content);
    tempFile.toFile().deleteOnExit();
    return tempFile;
  }

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

  @Test
  public void testParseQuotedCommaField() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"company_name\"\n"
        + "\"Art\",\"Venere\",\"Chemel, James L Cpa\"\n";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(1, result.size());
    assertEquals("Chemel, James L Cpa", result.get(0).get("company_name"));
  }

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

  @Test
  public void testEmptyFileReturnsEmptyList() throws IOException {
    String csv = "";

    CsvParser parser = new CsvParser();
    List<Map<String, String>> result = parser.parse(createTempCsv(csv));

    assertEquals(0, result.size());
  }

  @Test
  public void testWrongNumberOfFieldsThrowsException() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\"Art\",\"Venere\"\n";

    CsvParser parser = new CsvParser();

    assertThrows(IllegalArgumentException.class, () ->
        parser.parse(createTempCsv(csv)));
  }

  @Test
  public void testUnmatchedQuoteThrowsException() throws IOException {
    String csv = "\"first_name\",\"last_name\",\"email\"\n"
        + "\"Art\",\"Venere,\"art@venere.org\"\n";

    CsvParser parser = new CsvParser();

    assertThrows(IllegalArgumentException.class, () ->
        parser.parse(createTempCsv(csv)));
  }
}