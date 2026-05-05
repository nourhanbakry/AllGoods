package com.example.allgoods.UI.Customer.Cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.Data.repository.Card.CardRepository;
import com.example.allgoods.Data.repository.Card.CardRepositoryImpl;
import com.example.allgoods.Data.repository.Cart.CartRepositoryImpl;
import com.example.allgoods.Data.repository.User.UserRepository;
import com.example.allgoods.Data.repository.User.UserRepositoryImpl;
import com.example.allgoods.model.AddressModel;
import com.example.allgoods.model.CardModel;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.allgoods.Data.repository.Cart.CartRepository;
import com.example.allgoods.Data.repository.Order.OrderRepository;
import com.example.allgoods.Data.repository.Order.OrderRepositoryImpl;
import com.example.allgoods.model.OrderModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> orderStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<OrderModel> lastPlacedOrder = new MutableLiveData<>();
    private final MutableLiveData<AddressModel> primaryAddress = new MutableLiveData<>();
    private final MutableLiveData<AddressModel> selectedAddress = new MutableLiveData<>();
    private final MutableLiveData<CardModel> primaryCard = new MutableLiveData<>();
    private final MutableLiveData<CardModel> selectedCard = new MutableLiveData<>();
    private final CartRepository cartRepository = new CartRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final CardRepository cardRepository = new CardRepositoryImpl();
    private final OrderRepository orderRepository = new OrderRepositoryImpl();

    public LiveData<List<ProductModel>> getCartItems() {
        return cartItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getOrderStatus() {
        return orderStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void resetErrorMessage() {
        errorMessage.setValue(null);
    }

    public LiveData<OrderModel> getLastPlacedOrder() {
        return lastPlacedOrder;
    }

    public void resetOrderStatus() {
        orderStatus.setValue(null);
    }

    public LiveData<AddressModel> getPrimaryAddress() {
        return primaryAddress;
    }

    public LiveData<AddressModel> getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(AddressModel address) {
        selectedAddress.setValue(address);
    }

    public LiveData<CardModel> getPrimaryCard() {
        return primaryCard;
    }

    public LiveData<CardModel> getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(CardModel card) {
        selectedCard.setValue(card);
    }

    public void loadCartProducts() {
        isLoading.setValue(true);
        cartRepository.getCart(new CartRepository.OnCartFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> productList) {
                cartItems.setValue(productList);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                // Handle error
            }
        });
    }

    public void checkout() {
        if (cartItems.getValue() == null || cartItems.getValue().isEmpty()) {
            orderStatus.setValue("Cart is empty");
            return;
        }

        if (selectedAddress.getValue() == null) {
            orderStatus.setValue("Please select an address");
            return;
        }

        if (selectedCard.getValue() == null) {
            orderStatus.setValue("Please select a payment method");
            return;
        }

        isLoading.setValue(true);
        AddressModel address = selectedAddress.getValue();
        CardModel card = selectedCard.getValue();
        List<ProductModel> items = cartItems.getValue();
        double subtotal = calculateSubtotal();
        double shipping = 20.0;
        double total = calculateTotal();
        String userId = FirebaseAuth.getInstance().getUid();

        OrderModel order = new OrderModel(
                null,
                userId,
                address.getName(),
                address.getPhoneNumber(),
                address,
                card,
                items,
                subtotal,
                shipping,
                total,
                "pending",
                System.currentTimeMillis()
        );

        orderRepository.placeOrder(order, new OrderRepository.OnOrderChangeListener() {
            @Override
            public void onSuccess() {
                // Decrement Stock Immediately after successful payment
                for (ProductModel item : items) {
                    decrementStockAfterOrder(item);
                }

                cartRepository.clearCart(new CartRepository.OnCartChangeListener() {
                    @Override
                    public void onSuccess() {
                        isLoading.setValue(false);
                        lastPlacedOrder.setValue(order);
                        orderStatus.setValue("Success");
                    }

                    @Override
                    public void onFailure(String error) {
                        isLoading.setValue(false);
                        orderStatus.setValue("Order placed but failed to clear cart: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                orderStatus.setValue("Checkout failed: " + error);
            }
        });
    }

    private void decrementStockAfterOrder(ProductModel orderItem) {
        final com.example.allgoods.Data.repository.SellerProduct.ProductRepository productRepo = 
            new com.example.allgoods.Data.repository.SellerProduct.ProductRepositoryImpl();
        
        // In Cart, the item's 'id' might be the Firestore doc ID OR the cartItemId (docId_size)
        // We need the original product doc ID to update stock.
        String productId = orderItem.getId();
        if (productId != null && productId.contains("_")) {
            productId = productId.substring(0, productId.lastIndexOf("_"));
        }
        
        final String finalProductId = productId;
        final String selectedSize = orderItem.getSelectedSize();
        final int quantitySold = orderItem.getQuantity();

        if (finalProductId == null || selectedSize == null || selectedSize.isEmpty()) {
            android.util.Log.e("StockUpdate", "Skipping decrement: Missing info for " + orderItem.getName());
            return;
        }

        android.util.Log.d("StockUpdate", "Attempting to decrement stock for ID: " + finalProductId + ", Size: " + selectedSize + ", Qty: " + quantitySold);

        productRepo.getProductById(finalProductId, new com.example.allgoods.Data.repository.SellerProduct.ProductRepository.OnProductFetchListener() {
            @Override
            public void onSuccess(ProductModel currentProduct) {
                Map<String, Integer> sizesQuantity = currentProduct.getSizesQuantity();
                if (sizesQuantity != null && sizesQuantity.containsKey(selectedSize)) {
                    int currentQty = sizesQuantity.get(selectedSize);
                    int newQty = Math.max(0, currentQty - quantitySold);
                    sizesQuantity.put(selectedSize, newQty);

                    productRepo.updateProductQuantity(finalProductId, sizesQuantity, new com.example.allgoods.Data.repository.SellerProduct.ProductRepository.OnProductUploadListener() {
                        @Override
                        public void onSuccess() {
                            android.util.Log.d("StockUpdate", "SUCCESS: Stock updated for " + currentProduct.getName() + ". New Qty: " + newQty);
                        }

                        @Override
                        public void onFailure(String error) {
                            android.util.Log.e("StockUpdate", "FAILURE: Failed to update quantity for " + finalProductId + ": " + error);
                        }
                    });
                } else {
                    android.util.Log.e("StockUpdate", "FAILURE: Size " + selectedSize + " not found in product " + finalProductId);
                }
            }

            @Override
            public void onFailure(String error) {
                android.util.Log.e("StockUpdate", "FAILURE: Could not fetch product " + finalProductId + " to update stock: " + error);
            }
        });
    }

    public void loadPrimaryAddress() {
        userRepository.getPrimaryAddress(new UserRepository.OnAddressFetchListener() {
            @Override
            public void onSuccess(AddressModel address) {
                primaryAddress.setValue(address);
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
    }

    public void loadPrimaryCard() {
        cardRepository.getPrimaryCard(new CardRepository.OnCardFetchListener() {
            @Override
            public void onSuccess(CardModel card) {
                primaryCard.setValue(card);
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
    }

    public void removeFromCart(String cartItemId) {
        cartRepository.removeFromCart(cartItemId, new CartRepository.OnCartChangeListener() {
            @Override
            public void onSuccess() {
                loadCartProducts();
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void updateQuantity(String cartItemId, int quantity) {
        cartRepository.updateQuantity(cartItemId, quantity, new CartRepository.OnCartChangeListener() {
            @Override
            public void onSuccess() {
                loadCartProducts();
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public double calculateSubtotal() {
        double subtotal = 0;

        if (cartItems.getValue() == null) return 0;

        for (ProductModel p : cartItems.getValue()) {
            subtotal += p.getPrice() * p.getQuantity();
        }

        return subtotal;
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double shipping = 20;
        return subtotal + shipping;
    }
}