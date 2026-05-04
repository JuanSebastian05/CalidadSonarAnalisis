package com.udea.parcial.service;

import com.udea.parcial.dto.InventoryResponse;
import com.udea.parcial.entity.Almacen;
import com.udea.parcial.entity.Inventory;
import com.udea.parcial.entity.Product;
import com.udea.parcial.repository.AlmacenRepository;
import com.udea.parcial.repository.InventoryRepository;
import com.udea.parcial.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.parcial.dto.InventoryRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepo;
    @Mock
    private ProductRepository productRepo;
    @Mock
    private AlmacenRepository almacenRepo;

    @InjectMocks
    private InventoryServiceImpl service;

    @Test
    void testGetInventoryByAlmacen_retornaListaMapeadaCorrectamente() {
        // Arrange
        Almacen almacen = new Almacen();
        almacen.setId(1L);
        almacen.setNombre("Bodega Central");

        Product product = new Product("Teclado", "SKU-001", "Mecánico RGB", BigDecimal.valueOf(120000));
        product.setId(10L);

        Inventory inv = new Inventory(almacen, product, 15);
        inv.setId(100L);
        inv.setLastUpdated(LocalDateTime.of(2026, 3, 7, 10, 0));

        when(inventoryRepo.findByAlmacenId(1L)).thenReturn(List.of(inv));

        // Act
        List<InventoryResponse> result = service.getInventoryByAlmacen(1L);

        // Assert
        assertEquals(1, result.size());
        InventoryResponse dto = result.get(0);
        assertEquals(100L, dto.getInventoryId());
        assertEquals(1L, dto.getAlmacenId());
        assertEquals("Bodega Central", dto.getAlmacenNombre());
        assertEquals(10L, dto.getProductId());
        assertEquals("Teclado", dto.getProductName());
        assertEquals(15, dto.getStock());

        verify(inventoryRepo).findByAlmacenId(1L);
    }

    @Test
    void testGetInventoryByAlmacen_retornaListaVaciaSiNoHayInventario() {
        when(inventoryRepo.findByAlmacenId(99L)).thenReturn(List.of());

        List<InventoryResponse> result = service.getInventoryByAlmacen(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(inventoryRepo).findByAlmacenId(99L);
    }

    @Test
    void testCreateInventory_creaProductoEInventarioYRetornaDto() {
        // Arrange
        Almacen almacen = new Almacen();
        almacen.setId(1L);
        almacen.setNombre("Bodega Central");

        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(1L);
        request.setProductName("Mouse");
        request.setProductDescription("Inalámbrico");
        request.setSku("SKU-002");
        request.setPrice(BigDecimal.valueOf(50000));
        request.setStock(20);

        when(almacenRepo.findById(1L)).thenReturn(Optional.of(almacen));

        // Act
        InventoryResponse result = service.createInventory(request);

        // Assert
        assertNotNull(result);
        assertEquals("Bodega Central", result.getAlmacenNombre());
        assertEquals(1L, result.getAlmacenId());
        assertEquals("Mouse", result.getProductName());
        assertEquals("Inalámbrico", result.getProductDescription());
        assertEquals("SKU-002", result.getSku());
        assertEquals(BigDecimal.valueOf(50000), result.getPrice());
        assertEquals(20, result.getStock());

        verify(almacenRepo).findById(1L);
        verify(productRepo).save(any(Product.class));
        verify(inventoryRepo).save(any(Inventory.class));
    }

    @Test
    void testCreateInventory_lanzaExcepcionSiAlmacenNoExiste() {
        InventoryRequest request = new InventoryRequest();
        request.setAlmacenId(99L);

        when(almacenRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createInventory(request));

        //IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        //        () -> service.createInventory(request));

        assertEquals("El almacén no existe", ex.getMessage());
        verify(almacenRepo).findById(99L);
        verifyNoInteractions(productRepo, inventoryRepo);
    }
}