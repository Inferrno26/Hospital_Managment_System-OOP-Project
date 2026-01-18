package projectJava;

public class Invoice {
    private final String invoiceId;
    private final double baseAmount;
    private final double vatRate;
    private final double vatAmount;
    private final double totalAmount;

    public Invoice(String invoiceId, double baseAmount, double vatRate) {
        this.invoiceId = invoiceId;
        this.baseAmount = baseAmount;
        this.vatRate = vatRate;
        this.vatAmount = baseAmount * vatRate;
        this.totalAmount = baseAmount + vatAmount;
    }

    public String getInvoiceId() { return invoiceId; }
    public double getBaseAmount() { return baseAmount; }
    public double getVatRate() { return vatRate; }
    public double getVatAmount() { return vatAmount; }
    public double getTotalAmount() { return totalAmount; }
}
