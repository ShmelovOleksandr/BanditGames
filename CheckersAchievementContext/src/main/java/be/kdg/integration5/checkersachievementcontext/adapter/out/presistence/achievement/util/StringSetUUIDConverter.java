package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.achievement.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Converter
public class StringSetUUIDConverter implements AttributeConverter<Set<UUID>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(Set<UUID> uuids) {
        return uuids != null ? String.join(SPLIT_CHAR, uuids.stream().map(UUID::toString).toList()) : null;
    }

    @Override
    public Set<UUID> convertToEntityAttribute(String string) {
        return string != null && !string.isEmpty() ? Arrays.stream(string.split(SPLIT_CHAR)).map(UUID::fromString).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
