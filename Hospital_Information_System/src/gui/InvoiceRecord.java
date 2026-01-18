package gui;

public class InvoiceRecord {
	
    private final String invoiceId;
    private final String patientId;
    private final String roomNumber;
    private final String startDate;
    private final String endDate;
    private final double baseAmount;
    private final double vatRate;
    private final double vatAmount;
    private final double totalAmount;

    public InvoiceRecord(String invoiceId, String patientId, String roomNumber,
                         String startDate, String endDate,
                         double baseAmount, double vatRate, double vatAmount, double totalAmount) {
        this.invoiceId = invoiceId;
        this.patientId = patientId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseAmount = baseAmount;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.totalAmount = totalAmount;
    }

    public String getInvoiceId() { return invoiceId; }
    public String getPatientId() { return patientId; }
    public String getRoomNumber() { return roomNumber; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getBaseAmount() { return baseAmount; }
    public double getVatRate() { return vatRate; }
    public double getVatAmount() { return vatAmount; }
    public double getTotalAmount() { return totalAmount; }
}
