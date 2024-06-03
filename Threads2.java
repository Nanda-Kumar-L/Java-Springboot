package CollectionPractice;

class BankAccount {
    private int balance = 1000;

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println(Thread.currentThread().getName() + " deposited " + amount + ", balance: " + balance);
    }

    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println(Thread.currentThread().getName() + " withdrew " + amount + ", balance: " + balance);
        } else {
            System.out.println(Thread.currentThread().getName() + " attempted to withdraw " + amount + ", but insufficient funds. Balance: " + balance);
        }
    }

    public int getBalance() {
        return balance;
    }
}

class DepositTask implements Runnable {
    private final BankAccount account;
    private final int amount;

    public DepositTask(BankAccount account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void run() {
        account.deposit(amount);
    }
}

class WithdrawTask implements Runnable {
    private final BankAccount account;
    private final int amount;

    public WithdrawTask(BankAccount account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void run() {
        account.withdraw(amount);
    }
}

public class Threads2 {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();

        Thread depositThread1 = new Thread(new DepositTask(account, 500));
        Thread withdrawThread1 = new Thread(new WithdrawTask(account, 200));
        Thread depositThread2 = new Thread(new DepositTask(account, 300));
        Thread withdrawThread2 = new Thread(new WithdrawTask(account, 400));

        depositThread1.start();
        withdrawThread1.start();
        depositThread2.start();
        withdrawThread2.start();
    }
}
