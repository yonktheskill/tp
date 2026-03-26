package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;

public class JsonAliasStorageTest {

    @TempDir
    public Path testFolder;

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void readAliases_missingFile_returnsEmptyOptional() throws DataLoadingException {
        Path nonExistent = getTempFilePath("nonexistent.json");
        JsonAliasStorage storage = new JsonAliasStorage(nonExistent);
        assertFalse(storage.readAliases().isPresent());
    }

    @Test
    public void saveAndReadAliases_validAliases_successfulRoundTrip() throws IOException, DataLoadingException {
        Path filePath = getTempFilePath("aliases.json");
        JsonAliasStorage storage = new JsonAliasStorage(filePath);

        Map<String, String> aliases = new HashMap<>();
        aliases.put("ls", "list");
        aliases.put("a", "add");

        storage.saveAliases(aliases);
        Optional<Map<String, String>> loaded = storage.readAliases();

        assertTrue(loaded.isPresent());
        assertEquals(aliases, loaded.get());
    }

    @Test
    public void saveAndReadAliases_emptyMap_roundTripsCorrectly() throws IOException, DataLoadingException {
        Path filePath = getTempFilePath("empty_aliases.json");
        JsonAliasStorage storage = new JsonAliasStorage(filePath);

        Map<String, String> empty = new HashMap<>();
        storage.saveAliases(empty);

        Optional<Map<String, String>> loaded = storage.readAliases();
        assertTrue(loaded.isPresent());
        assertEquals(empty, loaded.get());
    }

    @Test
    public void readAliases_jsonWithNoAliasesField_returnsEmptyMap() throws IOException, DataLoadingException {
        Path filePath = getTempFilePath("no_field.json");
        Files.writeString(filePath, "{}");
        JsonAliasStorage storage = new JsonAliasStorage(filePath);

        Optional<Map<String, String>> loaded = storage.readAliases();
        assertTrue(loaded.isPresent());
        assertEquals(new HashMap<>(), loaded.get());
    }

    @Test
    public void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonAliasStorage(null));
    }

    @Test
    public void saveAliases_nullMap_throwsNullPointerException() {
        Path filePath = getTempFilePath("any.json");
        JsonAliasStorage storage = new JsonAliasStorage(filePath);
        assertThrows(NullPointerException.class, () -> storage.saveAliases(null));
    }

    @Test
    public void readAliases_malformedJson_throwsDataLoadingException() throws IOException {
        Path filePath = getTempFilePath("malformed.json");
        Files.writeString(filePath, "{ invalid json }");
        JsonAliasStorage storage = new JsonAliasStorage(filePath);
        assertThrows(DataLoadingException.class, () -> storage.readAliases());
    }

    @Test
    public void saveAliases_createsParentDirectories() throws IOException, DataLoadingException {
        Path nested = testFolder.resolve("subdir").resolve("aliases.json");
        JsonAliasStorage storage = new JsonAliasStorage(nested);

        Map<String, String> aliases = new HashMap<>();
        aliases.put("d", "delete");
        storage.saveAliases(aliases);

        assertTrue(Files.exists(nested));
        Optional<Map<String, String>> loaded = storage.readAliases();
        assertTrue(loaded.isPresent());
        assertEquals(aliases, loaded.get());
    }

}
