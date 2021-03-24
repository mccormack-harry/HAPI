package me.hazedev.hapi.enchanting.toggle;

import java.util.UUID;

public interface ToggleableEnchantment {

    default void toggle(UUID uniqueId) {
        if (isEnabled(uniqueId)) {
            setDisabled(uniqueId);
        } else {
            setEnabled(uniqueId);
        }
    }

    boolean isEnabled(UUID uniqueId);

    void setEnabled(UUID uniqueId);

    void setDisabled(UUID uniqueId);

}
