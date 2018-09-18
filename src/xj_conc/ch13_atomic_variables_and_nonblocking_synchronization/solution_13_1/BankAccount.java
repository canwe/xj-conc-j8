package xj_conc.ch13_atomic_variables_and_nonblocking_synchronization.solution_13_1;

import java.util.concurrent.atomic.*;

/**
 * Our new solution is not just simpler, it also eliminates a deadlock that
 * we had in the transferTo() method.  You did see that, no?
 */
public class BankAccount {
    // INVARIANT: balance must never be negative!!!
    private final AtomicInteger balance;

    public BankAccount(int balance) {
        if (balance < 0) throw new IllegalStateException();
        this.balance = new AtomicInteger(balance);
    }

    public void deposit(int amount) {
        if (!doActualDeposit(amount)) throw new IllegalStateException();
    }

    private boolean doActualDeposit(int amount) {
        int current, next;
        do {
            current = balance.get();
            next = current + amount;
            if (next < 0) return false;
        } while (!balance.compareAndSet(current, next));
        return true;
    }

    public void withdraw(int amount) {
        deposit(-amount);
    }

    public int getBalance() {
        return balance.intValue();
    }

    public boolean transferTo(BankAccount other, int amount) {
        if (amount < 0) throw new IllegalArgumentException();
        if (!doActualDeposit(-amount)) return false;
        other.deposit(amount);
        return true;
    }
}