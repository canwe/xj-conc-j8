package xj_conc.ch13_atomic_variables_and_nonblocking_synchronization.solution_13_1;

import org.junit.*;
import xj_conc.util.*;

import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class BankAccountTest {
    @Test
    public void testForDeadlock() throws InterruptedException {
        BankAccount switzerland = new BankAccount(1_000_000);
        BankAccount greece = new BankAccount(1_000);

        DeadlockTester deadlockTester = new DeadlockTester();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1_000_000; i++) {
                switzerland.transferTo(greece, 100);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1_000_000; i++) {
                greece.transferTo(switzerland, 100);
            }
        });

        t1.start();
        t2.start();

        t1.join(1000);
        deadlockTester.checkThatThreadTerminates(t1);
    }

    @Test(expected = IllegalStateException.class)
    public void testWithdraw() throws InterruptedException {
        BankAccount account = new BankAccount(100);
        account.withdraw(100);
        assertEquals(0, account.getBalance());
        account.withdraw(1); // should cause IllegalStateException
    }

    @Test
    public void testTransfer() throws InterruptedException {
        BankAccount switzerland = new BankAccount(0);
        BankAccount greece = new BankAccount(1000);

        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<?> futureGreece = pool.submit(() -> {
                for (int i = 0; i < 10_000_000; i++) {
                    greece.transferTo(switzerland, 10);
                }
            }
        );
        Future<?> depositGreece = pool.submit(() -> {
                for (int i = 0; i < 10_000_000; i++) {
                    greece.deposit(10);
                    try {
                        greece.withdraw(10);
                    } catch (IllegalStateException ignored) {
                    }
                }
            }
        );

        pool.shutdown();
        try {
            depositGreece.get();
            futureGreece.get();
        } catch (ExecutionException e) {
            fail(e.toString());
        } finally {
        }

    }

    private static final int NUMBER_OF_DEPOSIT_WITHDRAWS_FOR_CORRECTNESS_TEST = 10_000_000;

    @Test
    public void testForCorrectness() throws InterruptedException {
        BankAccount account = new BankAccount(1000);
        Runnable depositWithdraw = () -> {
            for (int i = 0; i < NUMBER_OF_DEPOSIT_WITHDRAWS_FOR_CORRECTNESS_TEST; i++) {
                account.deposit(100);
                account.withdraw(100);
            }
        };

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(depositWithdraw);
        pool.submit(depositWithdraw);
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;
        assertEquals(1000, account.getBalance());
    }

    @Test
    public void testForNegativeAmounts() throws InterruptedException {
        BankAccount account = new BankAccount(1000);

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(() -> {
            for (int i = 0; i < 10_000_000; i++) {
                account.transferTo(account, 100_000_000);
            }
        });
        pool.shutdown();

        for (int i = 0; i < 10_000_000; i++) {
            int balance = account.getBalance();
            assertTrue("Balance is now " + balance, balance >= 0);
        }
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTransfer() {
        BankAccount account1 = new BankAccount(1000);
        BankAccount account2 = new BankAccount(1000);
        account1.transferTo(account2, -500);
    }

    @Test
    public void testOverdrawnTransfer() {
        BankAccount account1 = new BankAccount(1000);
        BankAccount account2 = new BankAccount(1000);
        assertFalse(account1.transferTo(account2, 10000));
    }

    @Test
    public void testForBankRobber() {
        BankAccount account = new BankAccount(1000);
        try {
            account.withdraw(1_000_000);
        } catch (IllegalStateException ignore) {
        }
        assertEquals(1000, account.getBalance());
    }
}
