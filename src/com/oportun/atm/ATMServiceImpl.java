package com.oportun.atm;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ATMServiceImpl {

    private Map<Integer, Integer> dollarBillsTray = new TreeMap<>();
    Map<Integer, Integer> billsDepositedStats = new TreeMap<>();
    private Long totalBalance = 0l;
    Scanner scanner = new Scanner(System.in);

    public ATMServiceImpl() {
        dollarBillsTray.put(20, 0);
        dollarBillsTray.put(10, 0);
        dollarBillsTray.put(5, 0);
        dollarBillsTray.put(1, 0);

        calculateBalance();
    }

    private void calculateBalance() {
        dollarBillsTray.forEach((denomination, noOfBills) -> {
            creditAmount(Long.valueOf(denomination * noOfBills));
        });
    }

    public void deposit() {
        System.out.println("<--Deposit Screen: Enter Denominations you have as prompted-->");

        this.billsDepositedStats.clear();
        dollarBillsTray.keySet().forEach((denomination) -> {
            System.out.print("Enter number of "+denomination+"s:");
            int noOfBillsEntered = this.scanner.nextInt();
            this.billsDepositedStats.put(denomination, noOfBillsEntered);
        });

        System.out.print("Are you sure want to deposit? Select 1. Yes 2. No :");
        int depositConfirmation = this.scanner.nextInt();
        if(depositConfirmation == 1) {
            StringBuffer depositSummaryMsg = new StringBuffer();

            this.billsDepositedStats.forEach((depositedDenomination, noOfBillsDeposited) -> {
                this.dollarBillsTray.put(
                        depositedDenomination,
                        (this.dollarBillsTray.get(depositedDenomination) + noOfBillsDeposited));
                creditAmount(Long.valueOf(depositedDenomination * noOfBillsDeposited));
                depositSummaryMsg.append(depositedDenomination+"s="+noOfBillsDeposited+", ");
            });

            depositSummaryMsg.append("Total="+this.getTotalBalance());
            System.out.println("Balance: "+depositSummaryMsg.toString());
        } else {
            System.out.println("Deposit cancelled.");
        }

        promtForNextTrans();
    }

    public void withdraw() {

    }

    private void promtForNextTrans() {
        System.out.print("Would you like to perform another transaction? Select 1. Yes 2. No :");
        int nextTransInput = this.scanner.nextInt();
        if(nextTransInput == 2) {
            System.exit(0);
        }
        System.out.println("<-----------------Another Transaction------------------->");
    }

    private void displayBalanceWithDenominations() {

    }

    public static void main(String[] args) {
        ATMServiceImpl atmService = new ATMServiceImpl();
        System.out.println("Initial Balance: "+atmService.getTotalBalance());

        while(true) {
            System.out.println("ATM Services");
            System.out.println("Select 1 to Withdraw");
            System.out.println("Select 2 to Deposit");
            System.out.println("Select 3 to EXIT");
            System.out.print("Select the operation you want to perform:");

            int optionSelected = atmService.scanner.nextInt();

            switch (optionSelected) {
                case 1: atmService.withdraw();
                        break;
                case 2: atmService.deposit();
                        break;
                case 3: System.exit(0);
            }
            System.out.println("After Balance: "+atmService.getTotalBalance());
        }
    }

    public Long getTotalBalance() {
        return totalBalance;
    }

    private void creditAmount (Long amtToCredit) {
        this.totalBalance = this.totalBalance + amtToCredit;
    }

    private void deditAmount(Long amtToDebit) {
        this.totalBalance = this.totalBalance - amtToDebit;
    }
}
