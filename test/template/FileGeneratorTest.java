package template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileGeneratorTest {

  @TempDir
  Path tempDir;

  @Test
  void testGenerateFilesCreatesOneFilePerRecord() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("first_name", "Art", "last_name", "Venere", "email", "art@venere.org"),
        Map.of("first_name", "James", "last_name", "Benton", "email", "james@gmail.com")
    );

    String template = "To:[[email]]";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "email", engine);

    assertTrue(Files.exists(outputDir.resolve("Art_Venere_email.txt")));
    assertTrue(Files.exists(outputDir.resolve("James_Benton_email.txt")));
  }

  @Test
  void testGenerateFilesWritesRenderedContent() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("first_name", "Art", "last_name", "Venere", "email", "art@venere.org")
    );

    String template = "Dear [[first_name]] [[last_name]], email=[[email]]";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "email", engine);

    Path outputFile = outputDir.resolve("Art_Venere_email.txt");
    String content = Files.readString(outputFile);

    assertEquals("Dear Art Venere, email=art@venere.org", content);
  }

  @Test
  void testGenerateFilesUsesFirstNameOnlyWhenLastNameMissing() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("first_name", "Alice", "email", "alice@example.com")
    );

    String template = "Hello [[first_name]]";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "letter", engine);

    assertTrue(Files.exists(outputDir.resolve("Alice_letter.txt")));
  }

  @Test
  void testGenerateFilesUsesLastNameOnlyWhenFirstNameMissing() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("last_name", "Smith", "email", "smith@example.com")
    );

    String template = "Hello [[last_name]]";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "letter", engine);

    assertTrue(Files.exists(outputDir.resolve("Smith_letter.txt")));
  }

  @Test
  void testGenerateFilesUsesFallbackNameWhenBothNamesMissing() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("email", "unknown@example.com")
    );

    String template = "Hello customer";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "email", engine);

    assertTrue(Files.exists(outputDir.resolve("record_1_email.txt")));
  }

  @Test
  void testGenerateFilesSanitizesNamesInFilename() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("first_name", "Mary Jane", "last_name", "O'Connor", "email", "mary@example.com")
    );

    String template = "Hello [[first_name]] [[last_name]]";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "email", engine);

    assertTrue(Files.exists(outputDir.resolve("Mary_Jane_O_Connor_email.txt")));
  }

  @Test
  void testGenerateFilesCreatesOutputDirectoryIfNotExists() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("first_name", "Art", "last_name", "Venere")
    );

    String template = "Hello [[first_name]]";
    Path outputDir = tempDir.resolve("nested").resolve("output");

    generator.generateFiles(records, template, outputDir, "email", engine);

    assertTrue(Files.exists(outputDir));
    assertTrue(Files.isDirectory(outputDir));
    assertTrue(Files.exists(outputDir.resolve("Art_Venere_email.txt")));
  }

  @Test
  void testGenerateFilesMultipleRecordsWithFallbackNames() throws IOException {
    FileGenerator generator = new FileGenerator();
    TemplateEngine engine = new TemplateEngine();

    List<Map<String, String>> records = List.of(
        Map.of("email", "a@example.com"),
        Map.of("email", "b@example.com")
    );

    String template = "Notice";
    Path outputDir = tempDir.resolve("output");

    generator.generateFiles(records, template, outputDir, "letter", engine);

    assertTrue(Files.exists(outputDir.resolve("record_1_letter.txt")));
    assertTrue(Files.exists(outputDir.resolve("record_2_letter.txt")));
  }
}