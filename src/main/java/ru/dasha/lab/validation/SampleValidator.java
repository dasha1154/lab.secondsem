package ru.dasha.lab.validation;

import ru.dasha.lab.domain.Sample;

public class SampleValidator {

    public static void validate(Sample sample) {
        if (sample.getName() == null || sample.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: название образца не может быть пустым");
        }
        if (sample.getName().length() > 128) {
            throw new IllegalArgumentException("Ошибка: название слишком длинное (макс. 128 символов)");
        }

        if (sample.getType() == null || sample.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: тип образца не может быть пустым");
        }
        if (sample.getType().length() > 64) {
            throw new IllegalArgumentException("Ошибка: тип слишком длинный (макс. 64 символа)");
        }

        if (sample.getLocation() == null || sample.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: местоположение не может быть пустым");
        }
        if (sample.getLocation().length() > 64) {
            throw new IllegalArgumentException("Ошибка: местоположение слишком длинное (макс. 64 символа)");
        }

        if (sample.getStatus() == null) {
            throw new IllegalArgumentException("Ошибка: статус не может быть пустым");
        }

        if (sample.getOwnerUsername() == null || sample.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: владелец не может быть пустым");
        }
        if (sample.getOwnerUsername().length() > 64) {
            throw new IllegalArgumentException("Ошибка: имя владельца слишком длинное (макс. 64 символа)");
        }
    }
}