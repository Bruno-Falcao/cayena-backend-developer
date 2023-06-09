package com.cayena.backenddeveloper.service;

import com.cayena.backenddeveloper.exceptions.NotFoundException;
import com.cayena.backenddeveloper.model.Product;
import com.cayena.backenddeveloper.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.cayena.backenddeveloper.utils.Utils.currentDate;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    /**
     * Return a paginated list of products.
     *
     * @param page The page number, starts from 0.
     * @param pageSize Represents the max number of products in each page.
     * @return A Page object that contains the list of products for the specified page.
     * @throws NotFoundException if no products are found.
     */
    public Page<Product> findAllProducts(int page, int pageSize) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page, pageSize));
        isEmptyList(products);

        return products;
    }

    /**
     * Find a product using the id as parameter.
     *
     * @param productId The id of the product.
     * @return The specified product
     */
    public Product findProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    /**
     * Save a new product.
     *
     * @param product The product to be saved.
     * @return A Success String.
     * @throws Exception If an error occurs during the saveProduct method.
     */
    public String saveProduct(Product product) throws Exception {
        if (saveProductValidation(product)) {
            product.setDateOfCreation(currentDate());
            productRepository.save(product);

            return "Product saved successfully";
        }
        throw new Exception("Error saving product");
    }

    /**
     * Updates the fields of a Product.
     *
     * @param product
     * @return A message that indicates the success of the operation.
     * @throws Exception if the update validation fails or the product is not found.
     */
    public String updateProduct(Product product) throws Exception {
        Product existingProduct = findProductById(product.getId());

        if (updateProductValidation(product)) {
            existingProduct.setName(product.getName());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setUnitPrice(product.getUnitPrice());
            existingProduct.setSupplierId(product.getSupplierId());
            existingProduct.setDateOfLastUpdate(currentDate());
            productRepository.save(existingProduct);

            return "Product updated successfully";
        }

        throw new Exception("Product not found");
    }

    /**
     * Delete a product by its ID.
     *
     * @param id of the Product.
     * @return a success String.
     */
    public String deleteProduct(Integer id) {
        Product existingProduct = findProductById(id);

        productRepository.delete(existingProduct);
        return "Product deleted";
    }

    /**

     Updates the stock quantity of a product.

     @param productId The ID of the product.
     @param stockNumber The new stock quantity for the product.
     @return A success message if the stock is updated successfully.
     @throws IllegalArgumentException if the stock number is negative.
     @throws NotFoundException if the product with the given ID is not found.
     */
    public String updateQuantity(Integer productId , Integer stockNumber) {
        Product existingProduct = findProductById(productId);

        if (stockNumber >= 0 && existingProduct != null) {
            existingProduct.setQuantity(stockNumber);
            productRepository.save(existingProduct);
            return "Stock updated successfully";
        }
        throw new IllegalArgumentException("Stock number must not be negative");
    }

    private static boolean updateProductValidation(Product product) {
        return product.getName() != null && product.getQuantity() >= 0
                && product.getUnitPrice().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean saveProductValidation(Product product) {
        return product.getId() == null && product.getName() != null && product.getQuantity() >= 0
                && product.getUnitPrice().compareTo(BigDecimal.ZERO) > 0
                && product.getDateOfCreation() == null
                && product.getDateOfLastUpdate() == null;
    }

    private void isEmptyList(Page<Product> products) {
        if (products.isEmpty()) throw new NotFoundException("No products found");
    }
}
