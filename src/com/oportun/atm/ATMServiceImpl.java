package com.oportun.atm;

import java.util.*;

public class ATMServiceImpl {

    Comparator<Integer> denominationComparetor = Comparator.reverseOrder();
    private Map<Integer, Integer> dollarBillsTray = new TreeMap<>(denominationComparetor);
    Map<Integer, Integer> billsDepositedStats = new TreeMap<>();
    Map<Integer, Integer> billsWithdrawStats = new TreeMap<>(denominationComparetor);

    private Long totalBalance = 0l;
    Scanner scanner = new Scanner(System.in);

    public ATMServiceImpl() {
        loadDefaultTray();
        calculateBalance();
    }

    private void loadDefaultTray() {
        dollarBillsTray.put(20, 0);
        dollarBillsTray.put(10, 0);
        dollarBillsTray.put(5, 0);
        dollarBillsTray.put(1, 0);

        // For future support
        // dollarBillsTray.put(100, 0);
        // dollarBillsTray.put(50, 0);
    }

    private void calculateBalance() {
        dollarBillsTray.forEach((denomination, noOfBills) -> {
            creditAmount(Long.valueOf(denomination * noOfBills));
        });
    }

    public void deposit() {
        System.out.println("<-- Deposit Screen: Enter Denominations you have as prompted -->");

        this.billsDepositedStats.clear();
        boolean isAllNoOfBillsZero = true;

        for(Integer denomination : dollarBillsTray.keySet()) {
            System.out.print("Enter number of "+denomination+"s:");
            int noOfBillsEntered = scanner.nextInt();
            if(isNoOfBillsDepositedNegative(noOfBillsEntered))
                return;
            isAllNoOfBillsZero = isNoOfBillsDepositedZero(noOfBillsEntered);
            this.billsDepositedStats.put(denomination, noOfBillsEntered);
        }

        if(validateAllNoOfBillsForZero(isAllNoOfBillsZero))
            return;

        System.out.print("Are you sure want to deposit? Select 1. Yes 2. No :");
        int depositConfirmation = scanner.nextInt();
        if(depositConfirmation == 1) {
            this.billsDepositedStats.forEach((depositedDenomination, noOfBillsDeposited) -> {
                this.dollarBillsTray.put(
                        depositedDenomination,
                        (this.dollarBillsTray.get(depositedDenomination) + noOfBillsDeposited));
                creditAmount(Long.valueOf(depositedDenomination * noOfBillsDeposited));
            });
            displayBalanceWithDenominations();
        } else {
            System.out.println("Deposit cancelled.");
        }
    }

    public void withdraw() {
        System.out.println("<-- Withdraw Screen -->");
        System.out.print("Enter the amount you would like to withdraw:");
        int withdrawAmt = scanner.nextInt();

        if(isWithdrawAmtEnteredInvalid(withdrawAmt))
            return;

        System.out.print("Are you sure want to withdraw? Select 1. Yes 2. No :");
        int withdrawConfirmation = scanner.nextInt();
        if(withdrawConfirmation == 1) {
             this.billsWithdrawStats.clear();
             int tempWithdrawAmt = withdrawAmt;
             for(Map.Entry<Integer, Integer> denominationEntry : this.dollarBillsTray.entrySet()) {

                 Integer denomination = denominationEntry.getKey();
                 Integer noOfBillsAvailable = denominationEntry.getValue();

                 if(noOfBillsAvailable > 0) {
                     tempWithdrawAmt = dispenseDenomination(denomination, noOfBillsAvailable, tempWithdrawAmt, billsWithdrawStats);
                     if(tempWithdrawAmt <= 0) {
                          break;
                     }
                 }
             }
             if(unableToDispenseBills(tempWithdrawAmt))
                 return;

            evaluateBillsTobeDispensed(billsWithdrawStats);
            displayBalanceWithDenominations();
        } else {
            System.out.println("Withdraw cancelled.");
        }
    }

    private boolean unableToDispenseBills(int tempWithdrawAmt) {
        if(tempWithdrawAmt > 0) {
            System.out.println("Sorry, unable to dispense bills. Please try different amount");
            return true;
        } else {
            return false;
        }
    }
    private void evaluateBillsTobeDispensed(Map<Integer,Integer> billsWithdrawStats) {
        StringBuffer dispenseSummaryMsg = new StringBuffer();
        billsWithdrawStats.forEach((dispensedDenomination, noOfBillsDispensed) -> {
            this.dollarBillsTray.put(
                    dispensedDenomination,
                    (this.dollarBillsTray.get(dispensedDenomination) - noOfBillsDispensed));
            debitAmount(Long.valueOf(dispensedDenomination * noOfBillsDispensed));
            dispenseSummaryMsg.append(dispensedDenomination+"s="+noOfBillsDispensed+", ");
        });
        // debitAmount(Long.valueOf(withdrawAmt));
        String dispenseMsg = dispenseSummaryMsg.toString();
        System.out.println("Dispensed: "+dispenseMsg.substring(0, (dispenseMsg.length() - 2)));
    }

    private int dispenseDenomination(Integer denomination, Integer noOfBillsAvailable,
                                     int amount, Map<Integer,Integer> billsWithdrawStats) {
        int billsCanBeDispensed = 0;
        for(int count=1; count <= noOfBillsAvailable; count++) {
            if(denomination <= amount) {
                billsCanBeDispensed++;
                amount = amount - denomination;
            } else {
                break;
            }
        }
        if(billsCanBeDispensed > 0)
           billsWithdrawStats.put(denomination, billsCanBeDispensed);
       return amount;
    }

    private boolean isWithdrawAmtEnteredInvalid(int withdrawAmt) {
        if((withdrawAmt <= 0) || (withdrawAmt > this.totalBalance)) {
            System.out.println("Incorrect or insufficient funds.");
            return true;
        } else {
            return false;
        }
    }

    private boolean isNoOfBillsDepositedNegative(Integer noOfBills) {
       if(noOfBills < 0) {
           System.out.println("Incorrect deposit amount.");
           return true;
       } else {
           return false;
       }
    }

    private boolean isNoOfBillsDepositedZero(Integer noOfBills) {
       if(noOfBills == 0) {
           return true;
        } else {
           return false;
       }
    }

    private boolean validateAllNoOfBillsForZero(boolean isAllNoOfBillsZero) {
        if(isAllNoOfBillsZero) {
            System.out.println("Deposit amount cannot be zero.");
            return true;
        } else {
            return false;
        }
    }

    private void promptForNextTrans() {
        System.out.print("Would you like to perform another transaction? Select 1. Yes 2. No :");
        int nextTransInput = this.scanner.nextInt();
        if(nextTransInput == 2) {
            closeSession();
        }
        System.out.println("<===== Another Transaction =====>");
    }

    private void displayBalanceWithDenominations() {
        StringBuffer depositSummaryMsg = new StringBuffer();
        dollarBillsTray.forEach((denomination, noOfBills) -> {
            depositSummaryMsg.append(denomination+"s="+noOfBills+", ");
        });
        depositSummaryMsg.append("Total="+this.getTotalBalance());
        System.out.println("Balance: "+depositSummaryMsg.toString());
    }

    public static void main(String[] args) {
        ATMServiceImpl atmService = new ATMServiceImpl();
        System.out.println("Session started. ");

        while(true) {
            System.out.println("Available ATM Services");
            System.out.println("Select 1 to Withdraw");
            System.out.println("Select 2 to Deposit");
            System.out.println("Select 3 to Display Balance");
            System.out.println("Select 4 to Exit");
            System.out.print("Select the operation you want to perform:");

            int optionSelected = atmService.scanner.nextInt();

            switch (optionSelected) {
                case 1: atmService.withdraw();
                        atmService.promptForNextTrans();
                        break;
                case 2: atmService.deposit();
                        atmService.promptForNextTrans();
                        break;
                case 3: atmService.displayBalanceWithDenominations();
                        atmService.promptForNextTrans();
                        break;
                case 4: atmService.closeSession();
            }
        }
    }

    private void closeSession() {
        System.out.println("Session closed.");
        System.exit(0);
    }

    public Long getTotalBalance() {
        return totalBalance;
    }

    private void creditAmount (Long amtToCredit) {
        this.totalBalance = this.totalBalance + amtToCredit;
    }

    private void debitAmount(Long amtToDebit) {
        this.totalBalance = this.totalBalance - amtToDebit;
    }
}
