# Inventory Activity Tracker

A Java console application for managing products, tracking inventory changes, maintaining an immutable activity history, and exporting inventory events to a text file.

This project was built as a Java and object-oriented programming consolidation project. Its primary focus is not the user interface, but the design of a small application with clear responsibilities, protected state, validation, historical event tracking, and file persistence.

## Features

- Create products with a name, price, and initial quantity
- Add stock to existing products
- Remove stock from existing products
- Prevent inventory quantities from becoming negative
- Reject invalid stock operations
- Prevent duplicate product names
- Find products by name
- Record successful inventory operations
- Preserve inventory history in chronological order
- Expose product information without exposing mutable `Product` objects
- Protect internal collections from external modification
- Export inventory history to a text file
- Handle invalid operations with exceptions

## Technologies

- Java
- Java Collections Framework
- Java Records
- Java Enums
- Java Date & Time API
- Java NIO File API
- Object-Oriented Programming

No external libraries or frameworks are required.

## Project Structure

```text
inventory-activity-tracker/
├── src/
│   ├── Main.java
│   ├── Product.java
│   ├── ProductView.java
│   ├── InventoryAction.java
│   ├── InventoryLogEntry.java
│   └── InventoryService.java
├── .gitignore
└── README.md
```

## Architecture

The application separates responsibilities between several domain types.

```text
                         Main
                           │
                           ▼
                   InventoryService
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
          Product       History      ProductView
             │             │
             │             ▼
             │      InventoryLogEntry
             │             │
             │             ▼
             │       exportHistory()
             │             │
             │             ▼
             │        history.txt
             │
             ▼
       Inventory State
```

### `Product`

Represents a product and owns its mutable inventory state.

It is responsible for protecting rules such as:

- Price cannot be negative
- Available quantity cannot be negative
- Stock-change amounts must be greater than zero
- Removing stock cannot make the quantity negative

Stock changes happen through the `Product` rather than through direct field manipulation.

### `InventoryService`

Acts as the coordinator of the application.

It is responsible for:

- Maintaining the product collection
- Maintaining inventory history
- Finding products
- Preventing duplicate product names
- Coordinating product creation
- Coordinating stock additions and removals
- Recording successful inventory events
- Providing safe product views
- Providing inventory history
- Exporting history to a file

### `InventoryAction`

An enum defining the supported inventory events:

```java
PRODUCT_CREATED
STOCK_ADDED
STOCK_REMOVED
```

Using an enum restricts inventory actions to a known set of meaningful values.

### `InventoryLogEntry`

An immutable record representing a historical inventory event.

Each entry stores:

- Product name
- Inventory action
- Quantity involved in the event
- Date of the event

The quantity stored in a log entry represents the amount involved in that particular event, not the product's current quantity.

For example:

```text
Current Product Quantity: 15

Inventory Event:
STOCK_ADDED | 5
```

### `ProductView`

Provides a safe external representation of a product.

The service does not expose its internal mutable `Product` objects directly. Instead, product state is copied into immutable `ProductView` records.

```text
Internal Product
┌───────────────────────┐
│ name                  │
│ price                 │
│ availableQuantity     │
│                       │
│ addStock()            │
│ reduceStock()         │
└───────────┬───────────┘
            │ copy values
            ▼
ProductView
┌───────────────────────┐
│ name                  │
│ price                 │
│ quantity              │
│                       │
│ no stock mutation API │
└───────────────────────┘
```

This prevents callers from bypassing `InventoryService` and changing inventory without creating the appropriate history entry.

## Example Workflow

Suppose a keyboard is created with 10 units:

```text
Keyboard
Quantity: 10
```

Then five units are added:

```text
10 + 5 = 15
```

Then three units are removed:

```text
15 - 3 = 12
```

The current state becomes:

```text
Keyboard | Quantity: 12
```

While the history preserves each event:

```text
Keyboard | PRODUCT_CREATED | 10 | 2026-09-30
Keyboard | STOCK_ADDED | 5 | 2026-09-30
Keyboard | STOCK_REMOVED | 3 | 2026-09-30
```

This demonstrates an important distinction in the application:

```text
Product quantity
      =
CURRENT STATE

InventoryLogEntry quantity
      =
AMOUNT INVOLVED IN AN EVENT
```

## Validation and Failure Handling

Invalid operations fail before inventory state or history can be incorrectly modified.

For example, suppose:

```text
Keyboard quantity = 5
```

and the application attempts:

```java
reduceStock("Keyboard", 8);
```

The flow becomes:

```text
InventoryService
       │
       ▼
find Keyboard
       │
       ▼
Product.reduceStock(8)
       │
       ▼
5 - 8 < 0
       │
       ▼
IllegalArgumentException
       │
       ├── quantity remains 5
       └── no success history entry is created
```

This keeps inventory state and inventory history synchronized.

## Encapsulation

The application protects both its collections and its mutable domain objects.

Returning only:

```java
List.copyOf(products)
```

would protect the structure of the list but would still expose references to mutable `Product` objects.

Instead, the application converts products into `ProductView` records before returning them.

History can safely expose `InventoryLogEntry` objects because the entries are immutable records containing safe immutable/value-oriented components.

## File Export

Inventory history can be exported using Java's NIO API.

The export process follows this flow:

```text
Inventory History
       │
       ▼
StringBuilder
       │
       ▼
Complete History String
       │
       ▼
Files.writeString(...)
       │
       ▼
history.txt
```

`Path` represents where the file should be written.

`Files` performs the file operation.

`StringBuilder` constructs the complete text representation in memory before the file is written.

If file writing fails, an `IOException` is propagated without modifying the in-memory product or history state.

## Example Output

```text
CURRENT INVENTORY
-----------------
Keyboard | $49.99 | Quantity: 12
Mouse | $24.99 | Quantity: 16

INVENTORY HISTORY
-----------------
Keyboard | PRODUCT_CREATED | 10 | 2026-09-30
Mouse | PRODUCT_CREATED | 20 | 2026-09-30
Keyboard | STOCK_ADDED | 5 | 2026-09-30
Keyboard | STOCK_REMOVED | 3 | 2026-09-30
Mouse | STOCK_REMOVED | 4 | 2026-09-30
```

## Running the Project

### Requirements

Install a modern Java JDK.

Check your installation with:

```bash
java --version
javac --version
```

### Compile

From the project directory:

```bash
javac -d out src/*.java
```

### Run

```bash
java -cp out Main
```

After running the program, the application can generate:

```text
history.txt
```

containing the exported inventory history.

## Concepts Practiced

This project was built to consolidate several core Java concepts:

- Classes and objects
- Encapsulation
- Constructors
- Mutable vs. immutable state
- Object references
- Composition
- Responsibility-driven design
- Collections
- Generics
- Enums
- Records
- Exceptions
- Validation and invariants
- Defensive collection exposure
- Java Date & Time API
- `StringBuilder`
- File I/O
- `Path`
- `Files`
- `IOException`
- Execution flow
- State vs. event data

## Design Principles

Several design rules guided the project:

```text
Product
→ owns and protects product state

InventoryService
→ coordinates application operations

InventoryLogEntry
→ remembers historical events

ProductView
→ safely exposes current product information

InventoryAction
→ defines the valid inventory event vocabulary
```

Successful operations follow:

```text
REQUEST
   ↓
FIND PRODUCT
   ↓
VALIDATE
   ↓
CHANGE STATE
   ↓
RECORD EVENT
```

Failed operations follow:

```text
REQUEST
   ↓
VALIDATION FAILS
   ↓
EXCEPTION
   ↓
NO STATE CHANGE
   ↓
NO SUCCESS LOG
```

## Future Improvements

Possible future extensions include:

- Unit tests with JUnit
- Persistent product storage
- CSV or JSON export
- Product IDs instead of product names as identifiers
- Command-line user interaction
- Database persistence
- REST API
- Spring Boot backend
- PostgreSQL integration

These improvements are intentionally outside the current scope. The current version focuses on strengthening Java fundamentals, OOP design, collections, exception handling, and File I/O.

## Project Status

**Complete — Java Practical Toolkit / OOP Consolidation Project**

The project represents the completion of a Java consolidation milestone before moving into more advanced Java topics.