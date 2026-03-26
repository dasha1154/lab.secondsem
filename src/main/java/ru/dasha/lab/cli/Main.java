package ru.dasha.lab.cli;

import ru.dasha.lab.service.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (Exception e) {
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
                default:
                    System.out.println("Неизвестная команда. Введите help для списка команд.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  sample_add - добавить образец");
        System.out.println("  sample_list [--status ACTIVE|ARCHIVED] - список образцов");
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
}