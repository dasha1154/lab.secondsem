package ru.dasha.lab.cli;

import ru.dasha.lab.domain.*;
import ru.dasha.lab.service.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (Exception e) {
            // ignored
        }

        SampleManager sampleManager = new SampleManager();
        MeasurementManager measurementManager = new MeasurementManager(sampleManager);
        ProtocolManager protocolManager = new ProtocolManager();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в лабораторную систему. Введите help для списка команд.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            String[] parts = input.split(" ");
            String command = parts[0];

            switch (command) {
                case "exit":
                    System.out.println("Выход из программы.");
                    return;
                case "help":
                    printHelp();
                    break;
                case "sample_add":
                    addSample(scanner, sampleManager);
                    break;
                case "sample_list":
                    sampleList(sampleManager);
                    break;
                case "sample_update":
                    if (parts.length < 3) {
                        System.out.println("Ошибка: укажите id и поля для обновления (например sample_update 12 name=Новое)");
                        break;
                    }
                    try {
                        long id = Long.parseLong(parts[1]);
                        StringBuilder argsBuilder = new StringBuilder();
                        for (int i = 2; i < parts.length; i++) {
                            if (i > 2) argsBuilder.append(" ");
                            argsBuilder.append(parts[i]);
                        }
                        String argsString = argsBuilder.toString();
                        sampleUpdate(id, argsString, sampleManager);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: id должен быть числом");
                    }
                    break;
                case "sample_archive":
                    if (parts.length < 2) {
                        System.out.println("Ошибка: укажите id образца");
                        break;
                    }
                    try {
                        long id = Long.parseLong(parts[1]);
                        sampleArchive(id, sampleManager);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: id должен быть числом");
                    }
                    break;
                case "meas_add":
                    if (parts.length < 2) {
                        System.out.println("Ошибка: укажите sample_id");
                        break;
                    }
                    try {
                        long sampleId = Long.parseLong(parts[1]);
                        addMeasurement(scanner, sampleId, sampleManager, measurementManager);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: sample_id должен быть числом");
                    }
                    break;
                case "meas_list":
                    if (parts.length < 2) {
                        System.out.println("Ошибка: укажите sample_id");
                        break;
                    }
                    try {
                        long sampleId = Long.parseLong(parts[1]);
                        String paramStr = null;
                        int last = -1;
                        for (int i = 2; i < parts.length; i++) {
                            if (parts[i].equals("--param") && i+1 < parts.length) {
                                paramStr = parts[i+1].toUpperCase();
                                i++;
                            } else if (parts[i].equals("--last") && i+1 < parts.length) {
                                try {
                                    last = Integer.parseInt(parts[i+1]);
                                    i++;
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка: --last должно быть числом");
                                    return;
                                }
                            } else {
                                System.out.println("Ошибка: неизвестная опция " + parts[i]);
                                return;
                            }
                        }
                        measList(sampleId, paramStr, last, sampleManager, measurementManager);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: sample_id должен быть числом");
                    }
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите help для списка команд.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  sample_add - добавить образец");
        System.out.println("  sample_list - список образцов");
        System.out.println("  sample_show <id> - показать образец");
        System.out.println("  sample_update <id> field=value ... - обновить образец");
        System.out.println("  sample_archive <id> - архивировать образец");
        System.out.println("  meas_add <sample_id> - добавить измерение");
        System.out.println("  meas_list <sample_id> [--param PH|...] [--last N] - список измерений");
        System.out.println("  meas_stats <sample_id> <param> - статистика по параметру");
        System.out.println("  prot_create - создать протокол");
        System.out.println("  prot_apply <protocol_id> <sample_id> - проверить выполнение протокола");
        System.out.println("  help - показать эту справку");
        System.out.println("  exit - выход");
    }

    private static void addSample(Scanner scanner, SampleManager sampleManager) {
        System.out.print("Название: ");
        String name = scanner.nextLine().trim();
        System.out.print("Тип: ");
        String type = scanner.nextLine().trim();
        System.out.print("Место: ");
        String location = scanner.nextLine().trim();

        SampleStatus status = SampleStatus.ACTIVE;
        String owner = "SYSTEM";

        try {
            Sample sample = sampleManager.addSample(name, type, location, status, owner);
            System.out.println("OK sample_id=" + sample.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void sampleList(SampleManager sampleManager) {
        List<Sample> samples = sampleManager.getAllSamples();
        if (samples.isEmpty()) {
            System.out.println("Нет образцов");
            return;
        }
        System.out.printf("%-5s %-20s %-10s %-15s %s%n", "ID", "Name", "Type", "Location", "Status");
        for (Sample s : samples) {
            System.out.printf("%-5d %-20s %-10s %-15s %s%n",
                    s.getId(), s.getName(), s.getType(), s.getLocation(), s.getStatus());
        }
    }

    private static void sampleUpdate(long id, String argsString, SampleManager sampleManager) {
        Sample sample = sampleManager.getSampleById(id);
        if (sample == null) {
            System.out.println("Ошибка: образец с id=" + id + " не найден");
            return;
        }

        String[] pairs = argsString.split(" ");
        String newName = null;
        String newType = null;
        String newLocation = null;
        SampleStatus newStatus = null;

        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length != 2) {
                System.out.println("Ошибка: неправильный формат поля (должно быть field=value)");
                return;
            }
            String field = kv[0];
            String value = kv[1];

            switch (field) {
                case "name":
                    newName = value;
                    break;
                case "type":
                    newType = value;
                    break;
                case "location":
                    newLocation = value;
                    break;
                case "status":
                    try {
                        newStatus = SampleStatus.valueOf(value.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: статус должен быть ACTIVE или ARCHIVED");
                        return;
                    }
                    break;
                default:
                    System.out.println("Ошибка: нельзя менять поле '" + field + "'");
                    return;
            }
        }

        boolean updated = sampleManager.updateSample(id, newName, newType, newLocation, newStatus);
        if (updated) {
            System.out.println("OK");
        } else {
            System.out.println("Ошибка: не удалось обновить образец (возможно, образец не найден)");
        }
    }

    private static void sampleArchive(long id, SampleManager sampleManager) {
        Sample sample = sampleManager.getSampleById(id);
        if (sample == null) {
            System.out.println("Ошибка: образец с id=" + id + " не найден");
            return;
        }
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            System.out.println("Ошибка: образец уже ARCHIVED");
            return;
        }
        boolean archived = sampleManager.archiveSample(id);
        if (archived) {
            System.out.println("OK sample " + id + " archived");
        } else {
            System.out.println("Ошибка: не удалось архивировать образец");
        }
    }
    private static void addMeasurement(Scanner scanner, long sampleId,
                                       SampleManager sampleManager,
                                       MeasurementManager measurementManager) {
        Sample sample = sampleManager.getSampleById(sampleId);
        if (sample == null) {
            System.out.println("Ошибка: образец с id=" + sampleId + " не найден");
            return;
        }
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            System.out.println("Ошибка: нельзя добавлять измерения к ARCHIVED образцу");
            return;
        }

        System.out.print("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE): ");
        String paramStr = scanner.nextLine().trim().toUpperCase();
        MeasurementParam param;
        try {
            param = MeasurementParam.valueOf(paramStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: неизвестный параметр. Допустимые: PH, CONDUCTIVITY, TURBIDITY, NITRATE");
            return;
        }

        System.out.print("Значение: ");
        double value;
        try {
            value = Double.parseDouble(scanner.nextLine().trim());
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: значение должно быть числом");
            return;
        }

        System.out.print("Единицы: ");
        String unit = scanner.nextLine().trim();
        System.out.print("Метод: ");
        String method = scanner.nextLine().trim();

        String owner = "SYSTEM";

        try {
            Measurement measurement = measurementManager.addMeasurement(
                    sampleId, param, value, unit, method, owner);
            System.out.println("OK measurement_id=" + measurement.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    private static void measList(long sampleId, String paramStr, int last,
                                 SampleManager sampleManager,
                                 MeasurementManager measurementManager) {
        Sample sample = sampleManager.getSampleById(sampleId);
        if (sample == null) {
            System.out.println("Ошибка: образец с id=" + sampleId + " не найден");
            return;
        }

        List<Measurement> measurements;
        if (paramStr != null) {
            MeasurementParam param;
            try {
                param = MeasurementParam.valueOf(paramStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неизвестный параметр. Допустимые: PH, CONDUCTIVITY, TURBIDITY, NITRATE");
                return;
            }
            measurements = measurementManager.getMeasurementsBySampleIdAndParam(sampleId, param);
        } else {
            measurements = measurementManager.getMeasurementsBySampleId(sampleId);
        }


        if (last > 0) {
            measurements = measurementManager.getLastMeasurements(sampleId, last);
            if (paramStr != null) {
                measurements = measurementManager.getMeasurementsBySampleIdAndParam(sampleId, paramStr != null ? MeasurementParam.valueOf(paramStr) : null);
                if (last > 0 && measurements.size() > last) {
                    measurements = measurements.subList(0, last);
                }
            }
        }

        if (measurements.isEmpty()) {
            System.out.println("Нет измерений для данного образца" + (paramStr != null ? " с параметром " + paramStr : ""));
            return;
        }

        System.out.printf("%-5s %-10s %-10s %-12s %-10s %s%n", "ID", "Param", "Value", "Unit", "Method", "Time");
        for (Measurement m : measurements) {
            System.out.printf("%-5d %-10s %-10.2f %-12s %-10s %s%n",
                    m.getId(), m.getParam(), m.getValue(), m.getUnit(), m.getMethod(), m.getMeasuredAt());
        }
    }
}