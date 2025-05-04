package com.example;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;

/**
 * A complex user class with various field types for testing
 */
public class ComplexUser {
    private String username;
    private String email;
    private int age;
    private boolean active;
    private UserType type;
    private Address address;
    private List<String> tags;
    private Set<Integer> scores;
    private Map<String, String> preferences;
    private LocalDate birthDate;
    private String[] aliases;
    
    /**
     * User type enum
     */
    public enum UserType {
        ADMIN,
        REGULAR,
        GUEST
    }
    
    /**
     * Nested address class
     */
    public static class Address {
        private String street;
        private String city;
        private String zipCode;
        private Country country;
        
        // Getters and setters
        public String getStreet() {
            return street;
        }
        
        public void setStreet(String street) {
            this.street = street;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getZipCode() {
            return zipCode;
        }
        
        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
        
        public Country getCountry() {
            return country;
        }
        
        public void setCountry(Country country) {
            this.country = country;
        }
    }
    
    /**
     * Country class
     */
    public static class Country {
        private String name;
        private String code;
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
    }
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public UserType getType() {
        return type;
    }
    
    public void setType(UserType type) {
        this.type = type;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Set<Integer> getScores() {
        return scores;
    }
    
    public void setScores(Set<Integer> scores) {
        this.scores = scores;
    }
    
    public Map<String, String> getPreferences() {
        return preferences;
    }
    
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String[] getAliases() {
        return aliases;
    }
    
    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
}
