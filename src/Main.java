import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        InventoryService inventory = new InventoryService();

        try {

            // =========================
            // CREATE PRODUCTS
            // =========================

            inventory.createProduct(
                    "Keyboard",
                    49.99,
                    10
            );

            inventory.createProduct(
                    "Mouse",
                    24.99,
                    20
            );


            // =========================
            // CHANGE INVENTORY
            // =========================

            inventory.addStock(
                    "Keyboard",
                    5
            );

            inventory.reduceStock(
                    "Keyboard",
                    3
            );

            inventory.reduceStock(
                    "Mouse",
                    4
            );


            // =========================
            // VIEW CURRENT INVENTORY
            // =========================

            System.out.println("CURRENT INVENTORY");
            System.out.println("-----------------");

            for (ProductView product : inventory.getProducts()) {
                System.out.println(
                        product.name()
                                + " | $"
                                + product.price()
                                + " | Quantity: "
                                + product.quantity()
                );
            }


            // =========================
            // VIEW HISTORY
            // =========================

            System.out.println();
            System.out.println("INVENTORY HISTORY");
            System.out.println("-----------------");

            for (InventoryLogEntry entry : inventory.getHistory()) {
                System.out.println(
                        entry.productName()
                                + " | "
                                + entry.action()
                                + " | "
                                + entry.quantity()
                                + " | "
                                + entry.date()
                );
            }


            // =========================
            // EXPORT HISTORY
            // =========================

            Path historyFile = Path.of("history.txt");

            inventory.exportHistory(historyFile);

            System.out.println();
            System.out.println(
                    "History exported to: "
                            + historyFile.toAbsolutePath()
            );

        } catch (IllegalArgumentException exception) {

            System.out.println(
                    "Inventory error: "
                            + exception.getMessage()
            );

        } catch (IOException exception) {

            System.out.println(
                    "File error: "
                            + exception.getMessage()
            );
        }
    }
}