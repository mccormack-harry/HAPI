package me.hazedev.hapi.enchanting.toggle;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public interface ToggleableEnchantment {

    Set<UUID> disabled = new HashSet<>();

    default boolean isEnabled(UUID uniqueId) {
        return !disabled.contains(uniqueId);
    }

    default void toggle(UUID uniqueId) {
        if (isEnabled(uniqueId)) {
            setDisabled(uniqueId);
        } else {
            setEnabled(uniqueId);
        }
    }

    default void setEnabled(UUID uniqueId) {
        disabled.add(uniqueId);
    }

    default void setDisabled(UUID uniqueId) {
        disabled.remove(uniqueId);
    }

    default void onToggle(UUID uniqueId) {};

}
