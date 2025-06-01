package com.example.desguazame.access;

/**
 * Representa una pieza de automóvil con sus propiedades principales.
 *
 * @author Charlie
 */
public class CarPart {

    private int id;
    private String description;
    private int weight;
    private double price;
    private String reference;
    private String year;
    private String[] imagen;
    private String categories;
    private String sale;
    private String model;
    private String brand;
    private String company;

    /**
     * Constructor que inicializa una pieza de automóvil con todos sus
     * atributos.
     *
     * @param id Identificador único de la pieza.
     * @param description Descripción de la pieza.
     * @param weight Peso en gramos.
     * @param price Precio de la pieza.
     * @param reference Referencia o código identificativo.
     * @param year Año correspondiente.
     * @param imagen Array de imágenes asociadas.
     * @param categories Categorías de la pieza.
     * @param sale Información sobre la venta.
     * @param model Modelo del vehículo.
     * @param brand Marca del vehículo o fabricante.
     * @param company Empresa proveedora o fabricante.
     */
    public CarPart(int id, String description, int weight, double price, String reference, String year, String[] imagen, String categories, String sale, String model, String brand, String company) {
        this.id = id;
        this.description = description;
        this.weight = weight;
        this.price = price;
        this.reference = reference;
        this.year = year;
        this.imagen = imagen;
        this.categories = categories;
        this.sale = sale;
        this.model = model;
        this.brand = brand;
        this.company = company;
    }

    /**
     * Obtiene el identificador único de la pieza.
     *
     * @return id de la pieza.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único de la pieza.
     *
     * @param id nuevo identificador.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene la descripción de la pieza.
     *
     * @return descripción.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción de la pieza.
     *
     * @param description nueva descripción.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtiene el peso en gramos.
     *
     * @return peso.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Establece el peso en gramos.
     *
     * @param weight nuevo peso.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Obtiene el precio de la pieza.
     *
     * @return precio.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Establece el precio de la pieza.
     *
     * @param price nuevo precio.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Obtiene la referencia o código identificativo.
     *
     * @return referencia.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Establece la referencia o código identificativo.
     *
     * @param reference nueva referencia.
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Obtiene el año correspondiente.
     *
     * @return año.
     */
    public String getYear() {
        return year;
    }

    /**
     * Establece el año correspondiente.
     *
     * @param year nuevo año.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Obtiene el array de imágenes asociadas.
     *
     * @return array de imágenes.
     */
    public String[] getImagen() {
        return imagen;
    }

    /**
     * Establece el array de imágenes asociadas.
     *
     * @param imagen nuevo array de imágenes.
     */
    public void setImagen(String[] imagen) {
        this.imagen = imagen;
    }

    /**
     * Obtiene las categorías de la pieza.
     *
     * @return categorías.
     */
    public String getCategories() {
        return categories;
    }

    /**
     * Establece las categorías de la pieza.
     *
     * @param categories nuevas categorías.
     */
    public void setCategories(String categories) {
        this.categories = categories;
    }

    /**
     * Obtiene la información de venta o estado.
     *
     * @return información de venta.
     */
    public String getSale() {
        return sale;
    }

    /**
     * Establece la información de venta o estado.
     *
     * @param sale nueva información de venta.
     */
    public void setSale(String sale) {
        this.sale = sale;
    }

    /**
     * Obtiene el modelo del vehículo.
     *
     * @return modelo.
     */
    public String getModel() {
        return model;
    }

    /**
     * Establece el modelo del vehículo.
     *
     * @param model nuevo modelo.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Obtiene la marca del vehículo o fabricante.
     *
     * @return marca.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Establece la marca del vehículo o fabricante.
     *
     * @param brand nueva marca.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Obtiene la empresa proveedora o fabricante.
     *
     * @return empresa.
     */
    public String getCompany() {
        return company;
    }

    /**
     * Establece la empresa proveedora o fabricante.
     *
     * @param company nueva empresa.
     */
    public void setCompany(String company) {
        this.company = company;
    }
}
