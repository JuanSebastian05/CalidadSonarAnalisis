Feature: Pruebas POST del inventario

  Scenario: Crear inventario correctamente
    * def uniqueSku = 'SKU-' + java.util.UUID.randomUUID()

    Given url baseUrl
    And path 'inventory'
    And header X-API-Version = 'v1'
    And request
    """
    {
      "almacenId": 3,
      "productName": "Mouse Karate",
      "productDescription": "Mouse RGB",
      "sku": "#(uniqueSku)",
      "price": 95000,
      "stock": 12
    }
    """
    When method post
    Then status 201
    And match response.inventoryId == '#number'
    And match response.almacenId == 3
    And match response.almacenNombre == '#string'
    And match response.productId == '#number'
    And match response.productName == 'Mouse Karate'
    And match response.productDescription == 'Mouse RGB'
    And match response.sku == uniqueSku
    And match response.price == '#number'
    And match response.stock == 12
    And match response.lastUpdated == '#string'


  Scenario: Crear inventario con versión inválida
    Given url baseUrl
    And path 'inventory'
    And header X-API-Version = 'v2'
    And request
    """
    {
      "almacenId": 3,
      "productName": "Teclado Karate",
      "productDescription": "Teclado mecánico",
      "sku": "TEC-111",
      "price": 150000,
      "stock": 5
    }
    """
    When method post
    Then status 400
    And match response == 'Unsupported API version'

  Scenario: Crear inventario con almacén inexistente
    Given url baseUrl
    And path 'inventory'
    And header X-API-Version = 'v1'
    And request
    """
    {
      "almacenId": 99999,
      "productName": "Monitor Karate",
      "productDescription": "Monitor 24",
      "sku": "MON-001",
      "price": 500000,
      "stock": 3
    }
    """
    When method post
    Then status 404
    And match response == 'El almacén no existe'

  Scenario: Crear inventario sin header de versión
    Given url baseUrl
    And path 'inventory'
    And request
    """
    {
      "almacenId": 3,
      "productName": "Audifonos Karate",
      "productDescription": "Audifonos inalámbricos",
      "sku": "AUD-001",
      "price": 200000,
      "stock": 7
    }
    """
    When method post
    Then status 400