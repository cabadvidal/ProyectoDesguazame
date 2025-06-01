package com.example.desguazame.access;

/**
 * Clase que representa los datos de registro de un usuario.
 * <p>
 * Esta clase almacena la información personal, de contacto y bancaria de un
 * usuario, como su nombre, apellidos, teléfono, dirección de correo
 * electrónico, número de cuenta bancaria, y otros datos relacionados.</p>
 *
 * <p>
 * Los objetos de esta clase son útiles para representar un registro de usuario
 * en sistemas de gestión de usuarios, plataformas de autenticación, o en
 * cualquier sistema que requiera almacenar estos datos.</p>
 *
 * <p>
 * Ejemplo de uso:</p>
 * <pre>{@code
 * User usuario = new User("Carlos", "Gómez", "García", "646123456", "carlos.gomez@example.com", "ES123456789012345678901234");
 * }</pre>
 *
 * @author Charlie
 */
public class User {

    private String name;
    private String fName;
    private String sName;
    private String password;
    private String address;
    private int codePostal;
    private String city;
    private String municipality;
    private long phone;
    private long cell;
    private String dni;
    private String bank;
    private String mail;
    private long creditCard;

    /**
     * Constructor para crear un nuevo objeto {@code User} con los datos proporcionados.
     *
     * @param name Nombre del usuario
     * @param fName Primer apellido
     * @param sName Segundo apellido
     * @param password Contraseña del usuario
     * @param address Dirección del usuario
     * @param codePostal Código postal
     * @param city Ciudad
     * @param municipality Municipio
     * @param phone Teléfono fijo
     * @param cell Teléfono móvil
     * @param dni DNI del usuario
     * @param bank Nombre del banco
     * @param mail Dirección de correo eléctronico
     * @param creditCard Número de tarjeta de crédito
     */
    public User(String name, String fName, String sName, String password, String address, int codePostal, String city, String municipality, long phone, long cell, String dni, String bank, String mail, long creditCard) {
        this.name = name;
        this.fName = fName;
        this.sName = sName;
        this.password = password;
        this.address = address;
        this.codePostal = codePostal;
        this.city = city;
        this.municipality = municipality;
        this.phone = phone;
        this.cell = cell;
        this.dni = dni;
        this.bank = bank;
        this.mail = mail;
        this.creditCard = creditCard;
    }

    /**
     * @return Nombre del usuario
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Establece el nombre del usuario
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Primer apellido
     */
    public String getfName() {
        return fName;
    }

    /**
     * @param fName Establece el primer apellido
     */
    public void setfName(String fName) {
        this.fName = fName;
    }

    /**
     * @return Segundo apellido
     */
    public String getsName() {
        return sName;
    }

    /**
     * @param sName Establece el segundo apellido
     */
    public void setsName(String sName) {
        this.sName = sName;
    }

    /**
     * @return Contraseña del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password Establece la contraseña del usuario
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Dirección del usuario
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address Establece la dirección del usuario
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Código postal
     */
    public int getCodePostal() {
        return codePostal;
    }

    /**
     * @param codePostal Establece el código postal
     */
    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    /**
     * @return Ciudad
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city Establece la ciudad
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return Municipio
     */
    public String getMunicipality() {
        return municipality;
    }

    /**
     * @param municipality Establece el municipio
     */
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    /**
     * @return Teléfono fijo
     */
    public long getPhone() {
        return phone;
    }

    /**
     * @param phone Establece el teléfono fijo
     */
    public void setPhone(long phone) {
        this.phone = phone;
    }

    /**
     * @return Teléfono móvil
     */
    public long getCell() {
        return cell;
    }

    /**
     * @param cell Establece el teléfono móvil
     */
    public void setCell(long cell) {
        this.cell = cell;
    }

    /**
     * @return DNI del usuario
     */
    public String getDni() {
        return dni;
    }

    /**
     * @param dni Establece el DNI del usuario
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * @return Nombre del banco
     */
    public String getBank() {
        return bank;
    }

    /**
     * @param bank Establece el nombre del banco
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     * @return Número de tarjeta de crédito
     */
    public long getCreditCard() {
        return creditCard;
    }

    /**
     * @param creditCard Establece el número de tarjeta de crédito
     */
    public void setCreditCard(long creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * @return Correo eléctronico
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail Establece el correo eléctronico
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

}

