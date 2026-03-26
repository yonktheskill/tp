package seedu.address.storage;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson-friendly representation of the alias registry.
 */
class JsonSerializableAliasRegistry {

    @JsonProperty("aliases")
    private final Map<String, String> aliases;

    @JsonCreator
    JsonSerializableAliasRegistry(@JsonProperty("aliases") Map<String, String> aliases) {
        this.aliases = (aliases != null) ? new HashMap<>(aliases) : new HashMap<>();
    }

    Map<String, String> toAliasMap() {
        return new HashMap<>(aliases);
    }

}
