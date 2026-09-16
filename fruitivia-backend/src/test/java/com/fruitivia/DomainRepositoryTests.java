package com.fruitivia;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.buyer.BuyerRepository;
import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.supplier.Supplier;
import com.fruitivia.supplier.SupplierRepository;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.warehouse.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class DomainRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    void testUserSaveAndAudit() {
        User user = User.builder()
                .name("Test Engineer")
                .email("engineer@fruitivia.com")
                .passwordHash("hashed")
                .role(Role.ENGINEER)
                .build();
        
        User saved = userRepository.saveAndFlush(user);
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertTrue(saved.isActive());
    }

    @Test
    void testSupplierSave() {
        Supplier supplier = Supplier.builder()
                .supplierCode("SUP-TEST-123")
                .name("Farm India Co")
                .region("Maharashtra")
                .build();
        Supplier saved = supplierRepository.saveAndFlush(supplier);
        assertNotNull(saved.getId());
    }

    @Test
    void testFruitSave() {
        Fruit fruit = Fruit.builder()
                .name("Alphonso Mango")
                .description("Premium quality mango")
                .build();
        Fruit saved = fruitRepository.saveAndFlush(fruit);
        assertNotNull(saved.getId());
    }
}
