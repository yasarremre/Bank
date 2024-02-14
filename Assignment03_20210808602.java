import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * @author Emre YASAR
 */

public class Assignment03_20210808602 {
}

class Bank {
    private String name,address;
    private final ArrayList<Customer> custList = new ArrayList<>();
    private final ArrayList<Company> compList = new ArrayList<>();
    private final ArrayList<Account> accttList = new ArrayList<>();

    public Bank(String name, String address) {
        this.name=name;
        this.address=address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address=address;
    }

    public void addCustomer(int id, String name, String surname) {
        custList.add(new Customer(name, surname));
        for (int i = 0; i < custList.size(); i++) {
            if(custList.get(i).getName() == name && custList.get(i).getSurname() == surname) {
                custList.get(i).setId(id);
            }
        }
    }

    public void addCompany(int id, String name) {
        compList.add(new Company(name));
        for (int i = 0; i < compList.size(); i++) {
            if(Objects.equals(compList.get(i).getName(), name)) {
                compList.get(i).setId(id);
            }
        }
    }

    public void addAccount(Account account) {accttList.add(account);}

    public Customer getCustomer(int id) {
        int custCount = 0;
        for (int i = 0; i < custList.size(); i++) {
            if(custList.get(i).getId() == id) {
                return custList.get(i);
            }
            custCount++;
        }
        if(custCount == custList.size()) {
            throw new CustomerNotFoundException(id);
        }
        return null;
    }

    public Customer getCustomer(String name, String surname) {
        for (int i = 0; i < custList.size(); i++) {
            if(Objects.equals(custList.get(i).getName(), name) && Objects.equals(custList.get(i).getSurname(), surname)) {
                return custList.get(i);
            }
        }
        throw new CustomerNotFoundException(name, surname);
    }

    public Company getCompany(int id) {
        for (int i = 0; i < compList.size(); i++) {
            if(compList.get(i).getId() == id) {
                return compList.get(i);
            }
        }
        throw new CompanyNotFoundException(id);
    }

    public Company getCompany(String name) {
        for (int i = 0; i < compList.size(); i++) {
            if (Objects.equals(compList.get(i).getName(), name)) {
                return compList.get(i);
            }
        }
        throw new CompanyNotFoundException(name);
    }

    public Account getAccount(String acctNum) {
        for (int i = 0; i < accttList.size(); i++) {
            if (Objects.equals(accttList.get(i).getAcctNum(), acctNum)) {
                return accttList.get(i);
            }
        }
        throw new AccountNotFoundException(acctNum);
    }

    public void transferFunds(String accountFrom,String accountTo,double amount) {
        Account acctFrom = getAccount(accountFrom);
        Account acctTo = getAccount(accountTo);
        if(acctFrom!= null && acctTo!= null){
            double balanceTake = acctFrom.getBalance();
            acctFrom.withdrawal(amount);
            if(amount>=0 && balanceTake>= amount){
                acctTo.deposit(amount);
            }
        }
    }

    public void processTransactions(Collection<Transaction> transactions, String outFile) throws FileNotFoundException {
        ArrayList<Transaction> transactionsList = new ArrayList<>(transactions);
        Collections.sort(transactionsList);
        ArrayList<Transaction> deposits = new ArrayList<>();
        ArrayList<Transaction> transfers = new ArrayList<>();
        ArrayList<Transaction> withdraws = new ArrayList<>();

        for (int i = 0; i < transactionsList.size(); i++) {
            if (transactionsList.get(i).getType() == 1) {
                deposits.add(transactionsList.get(i));
            }
            else if (transactionsList.get(i).getType() == 2) {
                transfers.add(transactionsList.get(i));
            } else
                withdraws.add(transactionsList.get(i));
        }

        Collections.sort(deposits, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return Integer.parseInt(t1.getTo()) - Integer.parseInt(t2.getTo());
            }
        });

        Collections.sort(transfers, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return Integer.parseInt(t1.getTo()) - Integer.parseInt(t2.getTo());
            }
        });

        Collections.sort(withdraws, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return Integer.parseInt(t1.getFrom()) - Integer.parseInt(t2.getFrom());
            }
        });

        PrintWriter printWriter = new PrintWriter(outFile);

        for (int i = 0; i < deposits.size(); i++) {
            if (deposits.get(i).getFrom() != null) {
                try {
                    if (deposits.get(i).getAmount() < 0) {
                        throw new InvalidAmountException(deposits.get(i).getAmount());
                    }
                    getAccount(deposits.get(i).getFrom());
                    getAccount(deposits.get(i).getTo());
                    getAccount(deposits.get(i).getTo()).deposit(deposits.get(i).getAmount());

                } catch (Exception e) {
                    printWriter.println("ERROR: " + e.getClass().getSimpleName() + ": " + "1\t" + deposits.get(i).getTo() + "\t" + deposits.get(i).getFrom() + "\t" + deposits.get(i).getAmount());
                    printWriter.flush();
                }
            }
            else {
                try {
                    if (deposits.get(i).getAmount() < 0) {
                        throw new InvalidAmountException(deposits.get(i).getAmount());
                    }
                    getAccount(deposits.get(i).getTo());
                    getAccount(deposits.get(i).getTo()).deposit(deposits.get(i).getAmount());
                } catch (Exception e) {
                    printWriter.println("ERROR: " + e.getClass().getSimpleName() + ": " + "1\t" + deposits.get(i).getTo() + "\t" + deposits.get(i).getFrom() + "\t" + deposits.get(i).getAmount());
                    printWriter.flush();
                }
            }
        }

        for (int i = 0; i < transfers.size(); i++) {
            try {
                if(transfers.get(i).getAmount() < 0){
                    throw new InvalidAmountException(transfers.get(i).getAmount());
                }
                getAccount(transfers.get(i).getTo());
                getAccount(transfers.get(i).getFrom());
                if(getAccount(transfers.get(i).getFrom()).getBalance() < transfers.get(i).getAmount()){
                    throw new InvalidAmountException(transfers.get(i).getAmount());
                }
                getAccount(transfers.get(i).getFrom()).withdrawal(transfers.get(i).getAmount());
                getAccount(transfers.get(i).getTo()).deposit(transfers.get(i).getAmount());

            } catch (Exception e) {
                printWriter.println("ERROR: " + e.getClass().getSimpleName() + ": " + "2\t" + transfers.get(i).getTo() + "\t" + transfers.get(i).getFrom() + "\t" + transfers.get(i).getAmount());
                printWriter.flush();
            }
        }
        printWriter.close();
    }

        for (int i = 0; i < withdraws.size(); i++) {
            if (withdraws.get(i).getTo() != null) {
                try {
                    if (withdraws.get(i).getAmount() < 0) {
                        throw new InvalidAmountException(withdraws.get(i).getAmount());
                    }
                    getAccount(withdraws.get(i).getTo());
                    getAccount(withdraws.get(i).getFrom());
                    if (getAccount(withdraws.get(i).getFrom()).getBalance() < withdraws.get(i).getAmount()) {
                        throw new InvalidAmountException(withdraws.get(i).getAmount());
                    }
                    getAccount(withdraws.get(i).getFrom()).withdrawal(withdraws.get(i).getAmount());
                    getAccount(withdraws.get(i).getTo()).deposit(withdraws.get(i).getAmount());
                } catch (Exception e) {
                    printWriter.println("ERROR: " + e.getClass().getSimpleName() + ": " + "3\t" + withdraws.get(i).getTo() + "\t" + withdraws.get(i).getFrom() + "\t" + withdraws.get(i).getAmount());
                    printWriter.flush();
                }
            }
            else {
                try {
                    if (withdraws.get(i).getAmount() < 0) {
                        throw new InvalidAmountException(withdraws.get(i).getAmount());
                    }
                    getAccount(withdraws.get(i).getFrom());
                    getAccount(withdraws.get(i).getFrom()).withdrawal(withdraws.get(i).getAmount());
                } catch (Exception e) {
                    printWriter.println("ERROR: " + e.getClass().getSimpleName() + ": " + "3\t" + withdraws.get(i).getTo() + "\t" + withdraws.get(i).getFrom() + "\t" + withdraws.get(i).getAmount());
                    printWriter.flush();
                }
            }

        }


    public String toString() {
        StringBuilder str = new StringBuilder();
        str = new StringBuilder(getName() +"\t"+ getAddress() + "\n");
        for (int i = 0; i < compList.size(); i++) {
            str.append("\t").append(compList.get(i).getName()).append("\n");
            for (int j = 0; j < compList.get(i).getBusinessAccounts().size(); j++) {
                if (compList.get(i).getBusinessAccounts().size() == (j + 1)) {
                    str.append("\t\t").append(compList.get(i).getBusinessAccounts().get(j).getAcctNum()).append("\t").append(compList.get(i).getBusinessAccounts().get(j).getRate()).append("\t").append(compList.get(i).getBusinessAccounts().get(j).getBalance()).append("\n");
                } else {
                    str.append("\t\t").append(compList.get(i).getBusinessAccounts().get(j).getAcctNum()).append("\t").append(compList.get(i).getBusinessAccounts().get(j).getRate()).append("\t").append(compList.get(i).getBusinessAccounts().get(j).getBalance()).append("\n");
                }
            }

        }
        for (int i = 0; i < custList.size(); i++) {
            str.append("\t").append(custList.get(i).getName()).append(" ").append(custList.get(i).getSurname()).append("\n");
            for (int j = 0; j < custList.get(i).getPersonalAccounts().size(); j++) {
                if (custList.get(i).getPersonalAccounts().size() == (j + 1) && custList.size() == (i + 1)) {
                    str.append("\t\t").append(custList.get(i).getPersonalAccounts().get(j).getAcctNum()).append("\t").append(custList.get(i).getPersonalAccounts().get(j).getBalance());
                } else {
                    str.append("\t\t").append(custList.get(i).getPersonalAccounts().get(j).getAcctNum()).append("\t").append(custList.get(i).getPersonalAccounts().get(j).getBalance()).append("\n");
                }
            }

        }
        return str.toString();
    }
}

class Account {
    private final String acctNum;
    private double balance;

    Account(String acctNum) {
        this.acctNum = acctNum;
        balance = 0;
    }

    Account(String acctNum, double balance) {
        this.acctNum = acctNum;
        if (balance < 0) {
            this.balance = 0;
        } else {
            this.balance = balance;
        }
    }

    public String getAcctNum() {return acctNum;}

    public double getBalance() {return balance;}

    public void deposit(double depositAmount) {
        if (depositAmount < 0) {
            throw new InvalidAmountException(depositAmount);
        } else {
            balance += depositAmount;
        }
    }

    public void withdrawal(double withdrawalAmount) {
        if(balance-withdrawalAmount < 0 || withdrawalAmount <0) {
            throw new InvalidAmountException(withdrawalAmount);
        } else {
            balance -= withdrawalAmount;
        }
    }

    public String toString() {
        return "Account "+getAcctNum()+" has "+getBalance();
    }
}

class PersonalAccount extends Account {
    private String name;
    private String surname;
    private String PIN;

    public PersonalAccount(String acctNum, String name, String surname) {
        super(acctNum);
        this.name = name;
        this.surname = surname;
        Random random = new Random();
        this.PIN = String.format("%04d", random.nextInt(10000));
    }

    public PersonalAccount(String acctNum, String name, String surname, double balance) {
        super(acctNum, balance);
        this.name = name;
        this.surname = surname;
        Random random = new Random();
        this.PIN = String.format("%04d", random.nextInt(10000));
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getPIN() {return PIN;}
    public void setPIN(String PIN) {this.PIN = PIN;}

    public String toString() {
        return "Account "+getAcctNum()+" belonging to "+getName()+" "+ getSurname().toUpperCase()+ " has "+getBalance();
    }
}

class BusinessAccount extends Account {
    private double interestRate;

    public BusinessAccount(String acctNum, double interestRate) {
        super(acctNum);
        this.interestRate = interestRate;
    }

    public BusinessAccount(String acctNum, double balance, double interestRate) {
        super(acctNum, balance);
        this.interestRate = interestRate;
    }

    public double getRate() {return interestRate;}
    public void setRate(double interestRate) {this.interestRate = interestRate;}

    public double calculateInterest() {return getBalance()*getRate();}
}

class Customer {
    private int id;
    private String name, surname;
    private ArrayList<PersonalAccount> persAcctList = new ArrayList<>();
    //private static int acctCounter = 0;

    public Customer(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public void openAccount(String acctNum) {
        persAcctList.add(new PersonalAccount(acctNum, name, surname));
        //acctCounter++;
    }

    public PersonalAccount getAccount(String acctNum) {
        for (int i = 0; i < persAcctList.size(); i++) {
            if(Objects.equals(persAcctList.get(i).getAcctNum(), acctNum)) {
                return persAcctList.get(i);
            }
        }
        throw new AccountNotFoundException(acctNum);
    }

    public void closeAccount(String acctNum) {
        if(getAccount(acctNum) != null) {
            if(getAccount(acctNum).getBalance() <= 0) {
                persAcctList.remove(getAccount(acctNum));
            }
            else if(getAccount(acctNum).getBalance() > 0) {
                throw new BalanceRemainingException(getAccount(acctNum).getBalance());
            }
        }
    }

    public ArrayList<PersonalAccount> getPersonalAccounts() {
        return persAcctList;
    }

    public String toString() {
        return getName()+" "+getSurname().toUpperCase();
    }
}

class Company {
    private int id;
    private String name;
    private ArrayList<BusinessAccount> bussAcctList = new ArrayList<>();

    public Company(String name) {this.name = name;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public void openAccount(String acctNum, double rate) {
        bussAcctList.add(new BusinessAccount(acctNum, rate));
        //acctCounter++;
    }

    public BusinessAccount getAccount(String acctNum) {
        for (int i = 0; i < bussAcctList.size(); i++) {
            if(Objects.equals(bussAcctList.get(i).getAcctNum(), acctNum)) {
                return bussAcctList.get(i);
            }
        }
        throw new AccountNotFoundException(acctNum);
    }

    public void closeAccount(String acctNum) {
        if(getAccount(acctNum) != null) {
            if(getAccount(acctNum).getBalance() <= 0) {
                bussAcctList.remove(getAccount(acctNum));
            }
            else if(getAccount(acctNum).getBalance() > 0) {
                throw new BalanceRemainingException(getAccount(acctNum).getBalance());
            }
        }
    }
    public ArrayList<BusinessAccount> getBusinessAccounts() {
        return bussAcctList;
    }

    public String toString() {return getName();}
}

class Transaction implements Comparable<Transaction> {
    private final int type;
    private String to;
    private String from;
    private final double amount;

    public Transaction(int type, String to, String from, double amount) {
        this.to = to;
        this.from = from;
        this.amount = amount;

        if (type == 1 || type == 2 || type == 3) {
            this.type = type;
        } else {
            throw new InvalidParameterException();
        }
    }

    public Transaction(int type, String acctNum, double amount) {
        if (type == 1) {
            this.type = type;
            this.to = acctNum;
            this.amount = amount;
        } else if (type == 3) {
            this.type = type;
            this.from = acctNum;
            this.amount = amount;
        } else {
            throw new InvalidParameterException();
        }
    }

    public int getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public double getAmount() {
        return amount;
    }

    public int compareTo(Transaction O) {
        if (O.getType() > this.getType()) {
            return -1;
        } else if (O.getType() < this.getType()) {
            return 1;
        } else
            return 0;
    }

    public String toString() {
        return getType() + " " + getTo();
    }
}

class InvalidAmountException extends RuntimeException {
    private final double amount;

    public InvalidAmountException(double amount) {
        super();
        this.amount = amount;
    }

    public String toString() {return "InvalidAmountException: "+amount;}
}

class BalanceRemainingException extends RuntimeException {
    private final double balance;

    public BalanceRemainingException(double balance) {
        super();
        this.balance = balance;
    }

    public String toString() {return "BalanceRemainingException: "+balance;}
}

class AccountNotFoundException extends RuntimeException {
    private final String acctNum;

    public AccountNotFoundException(String acctNum) {
        super();
        this.acctNum = acctNum;
    }

    public String toString() {
        return "AccountNotFoundException: " + acctNum;
    }
}

class CompanyNotFoundException extends RuntimeException {
    private final int id;
    private final String name;

    public CompanyNotFoundException(int id) {
        this.id = id;
        this.name = null;
    }

    public CompanyNotFoundException(String name) {
        this.name = name;

        Random random = new Random();
        this.id = random.nextInt( 1000000);
    }

    public String toString() {
        if(name != null) {
            return "CompanyNotFoundException: name - "+name;
        }
        else {
            return "CompanyNotFoundException: id - "+id;
        }
    }
}

class CustomerNotFoundException extends RuntimeException {
    private final int id;
    private final String name, surname;

    public CustomerNotFoundException(int id) {
        this.id = id;
        this.name = null;
        this.surname = null;
    }

    public CustomerNotFoundException(String name, String surname) {
        this.name = name;
        this.surname = surname;

        Random random = new Random();
        this.id = random.nextInt( 1000000);
    }

    public String toString() {
        if (name != null || surname != null) {
            return "CustomerNotFoundException: name - "+name+" "+surname;
        } else {
            return "CustomerNotFoundException: id - "+id;
        }
    }
}
