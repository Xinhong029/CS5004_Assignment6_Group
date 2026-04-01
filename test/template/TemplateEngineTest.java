package template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TemplateEngineTest {

  @TempDir
  Path tempDir;

  @Test
  void testReadTemplate() throws IOException {
    Path templateFile = tempDir.resolve("template.txt");
    String expected = "Hello [[first_name]] [[last_name]]!";
    Files.writeString(templateFile, expected);

    TemplateEngine engine = new TemplateEngine();
    String actual = engine.readTemplate(templateFile);

    assertEquals(expected, actual);
  }

  @Test
  void testRenderReplacesSinglePlaceholder() {
    TemplateEngine engine = new TemplateEngine();
    String template = "Hello [[first_name]]!";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("first_name", "Alice");

    String result = engine.render(template, rowData);

    assertEquals("Hello Alice!", result);
  }

  @Test
  void testRenderReplacesMultiplePlaceholders() {
    TemplateEngine engine = new TemplateEngine();
    String template = "Dear [[first_name]] [[last_name]], your email is [[email]].";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("first_name", "Art");
    rowData.put("last_name", "Venere");
    rowData.put("email", "art@venere.org");

    String result = engine.render(template, rowData);

    assertEquals("Dear Art Venere, your email is art@venere.org.", result);
  }

  @Test
  void testRenderMissingPlaceholderUsesEmptyString() {
    TemplateEngine engine = new TemplateEngine();
    String template = "Hello [[first_name]] [[last_name]]!";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("first_name", "Alice");

    String result = engine.render(template, rowData);

    assertEquals("Hello Alice !", result);
  }

  @Test
  void testRenderRepeatedPlaceholder() {
    TemplateEngine engine = new TemplateEngine();
    String template = "[[first_name]] likes Java. [[first_name]] also likes testing.";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("first_name", "Bob");

    String result = engine.render(template, rowData);

    assertEquals("Bob likes Java. Bob also likes testing.", result);
  }

  @Test
  void testRenderTemplateWithoutPlaceholders() {
    TemplateEngine engine = new TemplateEngine();
    String template = "This template has no placeholders.";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("first_name", "Alice");

    String result = engine.render(template, rowData);

    assertEquals("This template has no placeholders.", result);
  }

  @Test
  void testRenderReplacementContainsSpecialCharacters() {
    TemplateEngine engine = new TemplateEngine();
    String template = "Company: [[company_name]]";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("company_name", "A&B Co. $100");

    String result = engine.render(template, rowData);

    assertEquals("Company: A&B Co. $100", result);
  }

  @Test
  void testRenderEmptyTemplate() {
    TemplateEngine engine = new TemplateEngine();
    String template = "";

    Map<String, String> rowData = new HashMap<>();
    rowData.put("first_name", "Alice");

    String result = engine.render(template, rowData);

    assertTrue(result.isEmpty());
  }
}