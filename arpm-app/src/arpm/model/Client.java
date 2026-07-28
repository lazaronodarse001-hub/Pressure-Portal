package arpm.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a single client / job record.
 */
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private String address;
    private double jobTotal;
    private LocalDate transactionDate;
    private LocalDate serviceDate; // nullable - assigned/cleared via the Schedule Calendar

    public Client(String name, String address, double jobTotal, LocalDate transactionDate, LocalDate serviceDate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.jobTotal = jobTotal;
        this.transactionDate = transactionDate;
        this.serviceDate = serviceDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getJobTotal() {
        return jobTotal;
    }

    public void setJobTotal(double jobTotal) {
        this.jobTotal = jobTotal;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    @Override
    public String toString() {
        return name;
    }
}
