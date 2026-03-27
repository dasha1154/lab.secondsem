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
}