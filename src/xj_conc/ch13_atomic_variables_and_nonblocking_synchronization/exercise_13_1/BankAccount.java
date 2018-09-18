package xj_conc.ch13_atomic_variables_and_nonblocking_synchronization.exercise_13_1;

/**
 * TODO: Change BankAccount to use atomics, rather than explicit locking.
 */
public class BankAccount {
    // INVARIANT: balance must never be negative!!!
    private int balance;

    public BankAccount(int balance) {
        if (balance < 0) throw new IllegalStateException();
        this.balance = balance;
    }

    public synchronized void deposit(int amount) {
        if (balance + amount < 0) throw new IllegalStateException();
        balance += amount;
    }

    public void withdraw(int amount) {
        deposit(-amount);
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized boolean transferTo(BankAccount other, int amount) {
        if (amount < 0) throw new IllegalArgumentException();
        if (balance < amount) {
            return false;
        }
        withdraw(amount);
        other.deposit(amount);
        return true;
    }
}