import java.time.LocalDate;



public record InventoryLogEntry(
        String productName,
        InventoryAction action,
        int quantity,
        LocalDate date) {
}
