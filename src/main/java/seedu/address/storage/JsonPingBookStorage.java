package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Stores both the address book (persons) and aliases in a single JSON file.
 * Implements both {@link AddressBookStorage} and {@link AliasStorage}.
 */
public class JsonPingBookStorage implements AddressBookStorage, AliasStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonPingBookStorage.class);

    private final Path filePath;

    /**
     * Creates a {@code JsonPingBookStorage} backed by the given file path.
     */
    public JsonPingBookStorage(Path filePath) {
        requireNonNull(filePath);
        this.filePath = filePath;
    }

    // ==================== AddressBookStorage ====================

    @Override
    public Path getAddressBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);
        Optional<JsonSerializablePingBook> jsonPingBook =
                JsonUtil.readJsonFile(filePath, JsonSerializablePingBook.class);
        if (!jsonPingBook.isPresent()) {
            return Optional.empty();
        }
        try {
            return Optional.of(jsonPingBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);
        Map<String, String> existingAliases = loadExistingAliases(filePath);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializablePingBook(addressBook, existingAliases), filePath);
    }

    // ==================== AliasStorage ====================

    @Override
    public Path getAliasesFilePath() {
        return filePath;
    }

    @Override
    public Optional<Map<String, String>> readAliases() throws DataLoadingException {
        return readAliases(filePath);
    }

    private Optional<Map<String, String>> readAliases(Path filePath) throws DataLoadingException {
        Optional<JsonSerializablePingBook> jsonPingBook =
                JsonUtil.readJsonFile(filePath, JsonSerializablePingBook.class);
        return jsonPingBook.map(JsonSerializablePingBook::getAliases);
    }

    @Override
    public void saveAliases(Map<String, String> aliases) throws IOException {
        requireNonNull(aliases);
        Optional<ReadOnlyAddressBook> existingAb;
        try {
            existingAb = readAddressBook(filePath);
        } catch (DataLoadingException e) {
            existingAb = Optional.empty();
        }
        ReadOnlyAddressBook ab = existingAb.orElseGet(seedu.address.model.AddressBook::new);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializablePingBook(ab, aliases), filePath);
    }

    // ==================== Combined save ====================

    /**
     * Saves both address book and aliases atomically in a single write.
     */
    public void saveAll(ReadOnlyAddressBook addressBook, Map<String, String> aliases) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(aliases);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializablePingBook(addressBook, aliases), filePath);
    }

    private Map<String, String> loadExistingAliases(Path filePath) {
        try {
            Optional<JsonSerializablePingBook> existing =
                    JsonUtil.readJsonFile(filePath, JsonSerializablePingBook.class);
            return existing.map(JsonSerializablePingBook::getAliases).orElseGet(HashMap::new);
        } catch (DataLoadingException e) {
            return new HashMap<>();
        }
    }

}
