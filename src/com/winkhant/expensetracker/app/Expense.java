package com.winkhant.expensetracker.app;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Expense {
    private final String FILE_PATH = "D:\\ExpenseRecord\\expense.txt";
    public int id;
    public String description;
    public double amount;
    public String date;
    Scanner sc = new Scanner(System.in);

        private boolean addExpense(){
            id = getLastId() + 1;
            return askUserInputs();
        }

        public boolean saveExpense(){
            if (addExpense()){
                try {
                    PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH,true));
                    pw.println(id+","+description+","+amount+","+date);
                    pw.close();
                } catch (IOException e) {
                    System.out.println("save expense: " + e.getMessage());
                }
                return true;

            } else {
                System.out.println("Please Enter valid inputs!");
                return false;
            }
        }

        public boolean deleteExpense() {
            boolean result = false;
            System.out.println("Enter the ID of the expense you want to delete: ");
            String tempPath = "D:\\ExpenseRecord\\Temp.txt";
            File inputFile = new File(FILE_PATH);
            File tempFile = new File(tempPath);
            int id = sc.nextInt();

            try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

                String dataLine;
                while ((dataLine = br.readLine()) != null) {
                    String[] data = dataLine.split(",");
                    if (data[0].equals(String.valueOf(id))) {
                        continue;
                    }
                    bw.write(dataLine);
                    bw.write(System.lineSeparator());
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error reading or writing the file: " + e.getMessage());
            }

            // Delete the originFileal file and rename the temp file
            if (!inputFile.delete()) {
                System.out.println("Error while deleting the originFileal file!");
            } else if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error while renaming the temp file!");
            }
            result = true;
            return result;
        }

        private int getLastId(){
        int id = 0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
                String dataLine = br.readLine();
                while (dataLine != null){
                    String data[] = dataLine.split(",");
                    id = Integer.parseInt(data[0]);
                    dataLine = br.readLine();
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return id;
        }

        private boolean idExist(int enteredId) {
            try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))){
                String dataLine = br.readLine();
                while (dataLine != null){
                    String data[] = dataLine.split(",");
                    if (enteredId == Integer.parseInt(data[0])) return true;
                    dataLine = br.readLine();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        private boolean askUserInputs(){

            System.out.println("Enter description!");
            description = sc.nextLine();
            if(description.isBlank()){
                System.out.println("Please enter valid input!!");
                return false;
            }

            while (true){
                try {
                    System.out.println("Enter Amount!");
                    amount = sc.nextDouble();

                    if (amount < 0) {
                        System.out.println("Amount cannot be negative!");
                    } else {
                        break;
                    }
                } catch (InputMismatchException e){
                    System.out.println("Invalid input. Please enter a valid number.");
                    sc.next(); // clear the invalid input
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate today = LocalDate.now();
            date = today.format(formatter);
            return true;
        }

        public boolean updateExpense() {

            File originFile = new File(FILE_PATH);
            File updateFile = new File("D:\\ExpenseRecord\\updateFile.txt");
            try(BufferedReader br = new BufferedReader(new FileReader(originFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(updateFile))){
                System.out.println("Enter id of expense that you want to update: ");
                String id = sc.nextLine();

                while (true){
                    if (!idExist(Integer.parseInt(id))){
                        System.out.println("ID " + id + " doesn't exist!. Please enter valid id: ");
                        id = sc.nextLine();
                    } else {
                        break;
                    }
                }
                askUserInputs();
                sc.nextLine();
                System.out.println("Enter date of expense that you want to update(Enter date input with format 'MM-dd-yyyy:  '): ");
                String date = sc.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                try{
                    LocalDate.parse(date,formatter);
                } catch (DateTimeException e){
                    System.out.println("Invalid date format!. Please enter date with format 'MM-dd-yyyy'");
                }
                String dataline =br.readLine();
                while(dataline != null) {
                    String[] data = dataline.split(",");
                    if(data[0].equals(id)){
                        bw.write(id+","+description+","+amount+","+date);
                    } else {
                        bw.write(dataline);
                    }
                    bw.write(System.lineSeparator());
                    dataline = br.readLine();
                }
                bw.flush();

            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.out.println("Error reading or writing the file: " + e.getMessage());
                return false;
            }

            if(!originFile.delete()){
                System.out.println("Error while deleting file.");
                return false;
            }
            if(!updateFile.renameTo(originFile)) {
                System.out.println("Error while renaming file");
                return false;
            }
            return true;
        }

        public boolean viewExpenseByMonth(int month) {
            double totalExpense = 0;
            int currentMonthValue = 0;
            try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                System.out.println(String.format("%-5s %-40s %-10s %-10s","ID","Description","Amount","Date"));
                String dataLine = br.readLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                if (month == 0){
                    LocalDate currentDate = LocalDate.now();
                    currentMonthValue = currentDate.getMonthValue();
                } else {

                    currentMonthValue = month;
                }
                while(dataLine != null){
                    String[] data = dataLine.split(",");
                    LocalDate dateOfExpense = LocalDate.parse(data[3],formatter);
                    int monthOfExpense = dateOfExpense.getMonthValue();
                    if(monthOfExpense == currentMonthValue){
                        System.out.println(String.format("%-5s %-40s %-10s %-10s",data[0],data[1],data[2],data[3]));
                        totalExpense += Double.parseDouble(data[2]);
                    }
                    dataLine = br.readLine();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Total expenses of this month is " + totalExpense + " mmks");
            return true;
        }

        public boolean viewAllExpenses(){
            double totalAmount = 0;
            try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));){
                System.out.println(String.format("%-5s %-40s %-10s %-10s","ID","Description","Amount","Date"));
                String dataLine = br.readLine();
                while (dataLine != null){
                    String[] data = dataLine.split(",");
                    System.out.println(String.format("%-5s %-40s %-10s %-10s",data[0],data[1],data[2],data[3]));
                    double amount = Double.parseDouble(data[2]);
                    totalAmount += amount;
                    dataLine = br.readLine();
                }
                System.out.println("Total amount of expenses is " + totalAmount + " mmks");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        public boolean exportToExcel(){
            System.out.println("Entered.");
            Path filePath = Paths.get(FILE_PATH);
            String directory = filePath.getParent().toString();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter the Excel file name (without extension): ");
            String fileName = scanner.nextLine();
            String excelFilePath = directory + "\\" + fileName + ".xlsx";

            try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
            Workbook workbook = new XSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(excelFilePath)){
                Sheet worksheet = workbook.createSheet("Expenses");
                Row header = worksheet.createRow(0);
                header.createCell(0).setCellValue("ID");
                header.createCell(1).setCellValue("Description");
                header.createCell(2).setCellValue("Amount");
                header.createCell(3).setCellValue("Date");
                String dataLine = br.readLine();
                int rowCount = 1;
                while ((dataLine != null)){
                    Row row = worksheet.createRow(rowCount++);
                    String[] column = dataLine.split(",");
                    for (int i = 0; i < column.length; i++){
                        Cell cell = row.createCell(i);
                        cell.setCellValue(column[i]);
                    }
                    dataLine = br.readLine();
                }
                workbook.write(outputStream);
                System.out.println("Data exported successfully to " + excelFilePath);


            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
    }


