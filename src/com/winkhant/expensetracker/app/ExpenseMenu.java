package com.winkhant.expensetracker.app;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ExpenseMenu {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Expense expense = new Expense();
        boolean userChoice = true;
        while(userChoice){
            System.out.println("1. Add an expense. \n2. Update an expense. \n3. Delete an expense. \n4. View expenses of this month. \n5. View expenses by month. \n6. View all expenses. \n7.Export to excel file.");
            int choose;
            while (true){
                try{
                    System.out.println("Choose a number: ");
                    choose = sc.nextInt();
                    if (choose > 1 && choose <= 7){
                        break;
                    } else {
                        System.out.println("Enter number from 1 to 7");
                        choose = sc.nextInt();
                    }
                } catch (InputMismatchException e){
                    System.out.println("Enter number only!");
                    sc.next();
                }
            }
            switch (choose){

                case 1:
                    if (expense.saveExpense()) System.out.println("Saved!! ID: " +  expense.id);
                    else System.out.println("Error while saving expense!.");
                    break;

                case 2:
                    if(expense.updateExpense()) System.out.println("Update success...");
                    else System.out.println("Update fail!");
                    break;

                case 3:
                    if (expense.deleteExpense())System.out.println("Delete success.");
                    else System.out.println("Error while deleting...");
                    break;

                case 4:
                    if (expense.viewExpenseByMonth(0))System.out.println("Print success.");
                    else System.out.println("Error printing expenses!");
                    break;

                case 5:
                    System.out.println("Enter the month that you want to check (values from 1 to 12): ");
                    int month = sc.nextInt();
                    while(true){
                        if (month > 0 && month <= 12) break;
                        else {
                            System.out.println("Please enter number from 1 to 12 for months: ");
                            month = sc.nextInt();
                        }
                    }
                    if (!expense.viewExpenseByMonth(month)){
                        System.out.println("Error printing records!");
                    } else {
                        System.out.println("Print success.");
                    }
                    break;

                case 6:
                    if (expense.viewAllExpenses()) System.out.println("Print success.");
                    else System.out.println("Error printing expenses!");
                    break;

                case 7:
                        if (expense.exportToExcel()) System.out.println("Exported!!.");
                        else System.out.println("Error exporting.");
                    break;

                default: System.out.println("Please enter number from 1 to 6!.");

            }
            System.out.println("Wanna do again? Enter (Y,y) or (N,n)");
            userChoice = sc.next().trim().equalsIgnoreCase("Y");
        }
        System.out.println("End");

    }


}