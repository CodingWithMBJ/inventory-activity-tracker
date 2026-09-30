import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {


    private List<Product> products = new ArrayList<>();
    private List<InventoryLogEntry> history = new ArrayList<>();

    public Product findProduct(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }

        throw new IllegalArgumentException("Product not found");
    }

    public void createProduct(String name, double price, int quantity){
        for(Product product : products){
            if(product.getName().equals(name)){
                throw new IllegalArgumentException("Product already exists");
            }
        }

        Product newProduct =  new Product(name,price,quantity);
        products.add(newProduct);

        InventoryLogEntry newEntry = new InventoryLogEntry(name, InventoryAction.PRODUCT_CREATED, quantity, LocalDate.now()
        );
        history.add(newEntry);
    }

    public void addStock(String name, int quantity){
        Product product = findProduct(name);
        product.addStock(quantity);

        InventoryLogEntry newEntry = new InventoryLogEntry(name, InventoryAction.STOCK_ADDED, quantity, LocalDate.now());
        history.add(newEntry);
    }

    public void reduceStock(String name, int quantity){
        Product product = findProduct(name);
        product.reduceStock(quantity);

        InventoryLogEntry newEntry = new InventoryLogEntry(name, InventoryAction.STOCK_REMOVED, quantity, LocalDate.now());
        history.add(newEntry);


    }



    public List<ProductView> getProducts() {
        List<ProductView> productViews = new ArrayList<>();

        for (Product product : products) {
            ProductView view = new ProductView(
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity()
            );

            productViews.add(view);
        }

        return List.copyOf(productViews);
    }

    public List<InventoryLogEntry> getHistory(){
        return List.copyOf(history);
    }


    public void exportHistory(Path path) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (InventoryLogEntry entry : history) {
            builder.append(entry.productName());
            builder.append(" | ");
            builder.append(entry.action());
            builder.append(" | ");
            builder.append(entry.quantity());
            builder.append(" | ");
            builder.append(entry.date());
            builder.append("\n");



        }  Files.writeString(path, builder.toString());
    }


}

