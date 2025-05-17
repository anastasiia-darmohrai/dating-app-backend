package com.lab1.datingapp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @NotNull(message = "Name cannot be null")
    private String name;
    @Min(value = 18, message = "Age should be at least 18")
    private int age;
    private String religion;
    private String gender;
    private String location;
    private String password;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotNull(message = "Name cannot be null") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Name cannot be null") String name) {
        this.name = name;
    }

    @Min(value = 18, message = "Age should be at least 18")
    public int getAge() {
        return age;
    }

    public void setAge(@Min(value = 18, message = "Age should be at least 18") int age) {
        this.age = age;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
