Feature: Pruebas GET del inventario

  Scenario: Obtener inventario correctamente
    Given url baseUrl
    And path 'inventory'
    And header X-API-Version = 'v1'
    And param almacenId = 1
    When method get
    Then status 200
    And match response == '#[]'

  Scenario: Obtener inventario con versión inválida
    Given url baseUrl
    And path 'inventory'
    And header X-API-Version = 'v2'
    And param almacenId = 1
    When method get
    Then status 400
    And match response == 'Unsupported API version'

  Scenario: Obtener inventario sin header de versión
    Given url baseUrl
    And path 'inventory'
    And param almacenId = 1
    When method get
    Then status 400

  Scenario: Obtener inventario y validar estructura cuando hay datos
    Given url baseUrl
    And path 'inventory'
    And header X-API-Version = 'v1'
    And param almacenId = 1
    When method get
    Then status 200
    And match each response contains
    """
    {
      inventoryId: '#number',
      almacenId: '#number',
      almacenNombre: '#string',
      productId: '#number',
      productName: '#string',
      productDescription: '#string',
      sku: '##string',
      price: '#number',
      stock: '#number',
      lastUpdated: '#string',
      links: '#[]'
    }
    """