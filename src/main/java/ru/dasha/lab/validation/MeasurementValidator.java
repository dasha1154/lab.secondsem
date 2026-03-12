package ru.dasha.lab.validation;

import ru.dasha.lab.domain.Measurement;
import ru.dasha.lab.domain.MeasurementParam;

public class MeasurementValidator {

    public static void validate(Measurement measurement) {
        if (measurement.getSampleId() <= 0) {
            throw new IllegalArgumentException("Ошибка: sampleId должен быть положительным числом");
        }

        if (measurement.getParam() == null) {
            throw new IllegalArgumentException("Ошибка: параметр измерения не может быть пустым");
        }

        if (Double.isNaN(measurement.getValue()) || Double.isInfinite(measurement.getValue())) {
            throw new IllegalArgumentException("Ошибка: значение должно быть корректным числом");
        }

        if (measurement.getUnit() == null || measurement.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: единицы измерения не могут быть пустыми");
        }
        if (measurement.getUnit().length() > 16) {
            throw new IllegalArgumentException("Ошибка: единицы измерения слишком длинные (макс. 16 символов)");
        }

        if (measurement.getMethod() == null || measurement.getMethod().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: метод измерения не может быть пустым");
        }
        if (measurement.getMethod().length() > 64) {
            throw new IllegalArgumentException("Ошибка: метод измерения слишком длинный (макс. 64 символа)");
        }

        if (measurement.getOwnerUsername() == null || measurement.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: владелец измерения не может быть пустым");
        }
        if (measurement.getOwnerUsername().length() > 64) {
            throw new IllegalArgumentException("Ошибка: имя владельца слишком длинное (макс. 64 символа)");
        }
    }
}