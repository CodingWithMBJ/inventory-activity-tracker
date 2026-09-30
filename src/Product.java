public class Product {

    private final String name;
    private final double price;
    private int availableQuantity;

    public Product(String name, double price, int availableQuantity) {
        if(price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        if(availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }

        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.availableQuantity;
    }

    public boolean isAvailable(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        return this.availableQuantity >= amount;
    }
    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.availableQuantity += amount;
    }

    public void reduceStock(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if(this.availableQuantity - amount < 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.availableQuantity -= amount;
    }
}
