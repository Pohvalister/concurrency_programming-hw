package ru.ifmo.pp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank implementation.
 *
 * @author Ignashov
 */
public class BankImpl implements Bank {
    /**
     * An array of accounts by index.
     */
    private final Account[] accounts;

    /**
     * Creates new bank instance.
     *
     * @param n the number of accounts (numbered from 0 to n-1).
     */
    public BankImpl(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = new Account();
            accounts[i].lock = new ReentrantLock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfAccounts() {
        return accounts.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getAmount(int index) {
        long result = 0;
        accounts[index].lock.lock();
        try {
            result = accounts[index].amount;
        } finally {
            accounts[index].lock.unlock();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTotalAmount() {
        long sum = 0;
        int size = getNumberOfAccounts();
        try {
            for (int i = 0; i < size; i++) {
                accounts[i].lock.lock();
                sum += accounts[i].amount;
            }
        } finally {
            for (int i = size - 1; i >= 0; i--) {
                accounts[i].lock.unlock();
            }
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long deposit(int index, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount: " + amount);
        long result = 0;
        accounts[index].lock.lock();
        try {
            Account account = accounts[index];
            if (amount > MAX_AMOUNT || account.amount + amount > MAX_AMOUNT)
                throw new IllegalStateException("Overflow");
            account.amount += amount;
            result = account.amount;
        } finally {
            accounts[index].lock.unlock();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long withdraw(int index, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount: " + amount);
        long result = 0;
        accounts[index].lock.lock();
        try {
            Account account = accounts[index];
            if (account.amount - amount < 0)
                throw new IllegalStateException("Underflow");
            account.amount -= amount;
            result = account.amount;
        } finally {
            accounts[index].lock.unlock();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transfer(int fromIndex, int toIndex, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount: " + amount);
        if (fromIndex == toIndex)
            throw new IllegalArgumentException("fromIndex == toIndex");
        int frstI, scndI;
        if (fromIndex > toIndex) {
            frstI = fromIndex;
            scndI = toIndex;
        } else {
            frstI = toIndex;
            scndI = fromIndex;
        }

        accounts[scndI].lock.lock();
        try {
            Account from = accounts[fromIndex];
            accounts[frstI].lock.lock();
            try {
                Account to = accounts[toIndex];
                if (amount > from.amount)
                    throw new IllegalStateException("Underflow");
                else if (amount > MAX_AMOUNT || to.amount + amount > MAX_AMOUNT)
                    throw new IllegalStateException("Overflow");
                from.amount -= amount;
                to.amount += amount;
            } finally {
                accounts[frstI].lock.unlock();
            }
        } finally {
            accounts[scndI].lock.unlock();
        }
    }

    /**
     * Private account data structure.
     */
    private static class Account {
        /**
         * Amount of funds in this account.
         */
        long amount;
        Lock lock;
    }
}

