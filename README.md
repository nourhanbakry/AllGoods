# AllGoods
<p align="center">
  <img width="7000" height="3500" alt="app_mockup" src="https://github.com/user-attachments/assets/05b346d1-6537-451a-a8d4-542855c733e0" />
</p>


AllGoods is an Android e-commerce application designed to serve both customers and sellers. It provides a platform for sellers to manage their products and orders, and for customers to browse, purchase, and manage their shopping experience. The application is built using Java and follows the MVVM architecture with Firebase as its backend.

## Features

The application is split into two main user roles: Customer and Seller.

### Authentication
- User Sign-Up
- User Login
- Forget Password functionality

### Customer Features
- **Home:** Browse a catalog of available products.
- **Cart:** Add products to a shopping cart for checkout.
- **Wishlist:** Save desired products for later.
- **Profile:** Manage user account information.

### Seller Features
- **Add Product:** List new items for sale.
- **Inventory:** View and manage existing product listings.
- **Orders:** Track and process customer orders.
- **Reviews:** See customer feedback on products.
- **Stats:** Analyze sales and performance metrics.

## Architecture and Technologies

- **Language:** Java
- **Platform:** Android
- **Architecture:** Model-View-ViewModel (MVVM) with Repository Pattern
- **Backend:** Firebase (for authentication and data storage)
- **UI:** Android XML Layouts with Material Components
- **Build Tool:** Gradle

The project structure is organized to separate concerns, making it scalable and maintainable:
- `Data/`: Manages all data operations, containing remote (Firebase) and local data sources, along with repositories that abstract the data logic from the UI.
- `UI/`: Contains all Activities and Fragments, organized by feature (Auth, Customer, Seller). Each UI component has a corresponding ViewModel to handle its logic and state.
- `model/`: Defines the data models for the application, such as `User` and `Product`.
- `utils/`: Includes helper classes and constants used throughout the app.

## Project Setup

To get the AllGoods project up and running on your local machine, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/nourhanbakry/AllGoods.git
    ```

2.  **Open in Android Studio:**
    - Open Android Studio.
    - Select `File -> Open` and navigate to the cloned repository directory.

3.  **Firebase Configuration:**
    - This project uses Firebase for its backend services. You will need to create your own Firebase project.
    - Go to the [Firebase Console](https://console.firebase.google.com/).
    - Create a new project and add an Android app with the package name `com.example.allgoods`.
    - Download the generated `google-services.json` file.
    - Place the `google-services.json` file in the `app/` directory of the project.

4.  **Sync and Run:**
    - Wait for Android Studio to sync the project with the Gradle files.
    - Build and run the application on an Android emulator or a physical device.
