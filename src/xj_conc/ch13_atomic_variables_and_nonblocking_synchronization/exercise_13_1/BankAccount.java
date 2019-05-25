package xj_conc.ch13_atomic_variables_and_nonblocking_synchronization.exercise_13_1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Change BankAccount to use atomics, rather than explicit locking.
 */
public class BankAccount {
    // INVARIANT: balance must never be negative!!!
    private AtomicInteger balance = new AtomicInteger();

    public BankAccount(int balance) {
        if (balance < 0) throw new IllegalStateException();
        this.balance.set(balance);
    }

    public boolean deposit(int amount) {
        int current = balance.get();
        if (current + amount < 0) throw new IllegalStateException();
        return doDeposit(amount);
    }

    private boolean doDeposit(int amount) {
        int current = balance.get();
        while (!balance.compareAndSet(current, current + amount)) {
            current = balance.get();
            if (current + amount < 0) return false;
        }
        return true;
    }

    public boolean withdraw(int amount) {
        return deposit(-amount);
    }

    public int getBalance() {
        return balance.get();
    }

    public boolean transferTo(BankAccount other, int amount) {
        if (amount < 0) throw new IllegalArgumentException();
        int current = balance.get();
        if (current < amount) {
            return false;
        }
        if (!doDeposit(-amount)) return false;
        other.deposit(amount);
        return true;
    }
}
