package ru.dasha.lab.validation;

import ru.dasha.lab.domain.Protocol;

public class ProtocolValidator {

    public static void validate(Protocol protocol) {
        if (protocol.getName() == null || protocol.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: название протокола не может быть пустым");
        }
        if (protocol.getName().length() > 128) {
            throw new IllegalArgumentException("Ошибка: название протокола слишком длинное (макс. 128 символов)");
        }

        if (protocol.getRequiredParams() == null || protocol.getRequiredParams().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: список обязательных параметров не может быть пустым");
        }

        if (protocol.getOwnerUsername() == null || protocol.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: владелец протокола не может быть пустым");
        }
        if (protocol.getOwnerUsername().length() > 64) {
            throw new IllegalArgumentException("Ошибка: имя владельца слишком длинное (макс. 64 символа)");
        }
    }
}
