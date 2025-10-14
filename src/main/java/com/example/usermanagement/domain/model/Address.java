package com.example.usermanagement.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NullMarked
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "postal_code", nullable = false, length = 8)
    private String postalCode;
    
    @Column(name = "street", length = 200)
    private String street;
    
    @Column(name = "neighborhood", length = 100)
    private String neighborhood;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 2)
    private String state;
    
    @Column(name = "complement", length = 100)
    private String complement;
    
    protected Address() {}
    
    public Address(String postalCode, @Nullable String street, @Nullable String neighborhood, 
                   @Nullable String city, @Nullable String state, @Nullable String complement) {
        this.postalCode = Objects.requireNonNull(postalCode, "Postal code cannot be null");
        this.street = street;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.complement = complement;
    }
    
    public static Address fromViaCep(ViaCepResponse response) {
        return new Address(
            response.cep(),
            response.logradouro(),
            response.bairro(),
            response.localidade(),
            response.uf(),
            response.complemento()
        );
    }
    
    public boolean isComplete() {
        return postalCode != null && !postalCode.isBlank() &&
               city != null && !city.isBlank() &&
               state != null && !state.isBlank();
    }
    
    public AddressRecord toRecord() {
        return new AddressRecord(postalCode, street, neighborhood, city, state, complement);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Address address = (Address) obj;
        return Objects.equals(postalCode, address.postalCode) &&
               Objects.equals(city, address.city) &&
               Objects.equals(state, address.state);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(postalCode, city, state);
    }
    
    @Override
    public String toString() {
        return "Address{postalCode='%s', city='%s', state='%s'}".formatted(postalCode, city, state);
    }
    
    public record AddressRecord(
        String postalCode,
        @Nullable String street,
        @Nullable String neighborhood,
        @Nullable String city,
        @Nullable String state,
        @Nullable String complement
    ) {
        public static AddressRecord from(Address address) {
            return new AddressRecord(
                address.getPostalCode(),
                address.getStreet(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getComplement()
            );
        }
    }
    
    public record ViaCepResponse(
        String cep,
        @Nullable String logradouro,
        @Nullable String bairro,
        @Nullable String localidade,
        @Nullable String uf,
        @Nullable String complemento,
        @Nullable String erro
    ) {}
}