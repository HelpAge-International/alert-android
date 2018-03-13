package org.alertpreparedness.platform.alert.firebase;

/**
 * Created by Tj on 13/03/2018.
 */

public class UserProfileModel extends FirebaseModel {
    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String city;

    private String country;

    private String email;

    private String firstName;

    private String language;

    private String lastName;

    private String latestCoCAgreed;

    private String phone;

    private String postCode;

    private String title;

    public UserProfileModel() {

    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLatestCoCAgreed() {
        return latestCoCAgreed;
    }

    public void setLatestCoCAgreed(String latestCoCAgreed) {
        this.latestCoCAgreed = latestCoCAgreed;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "UserProfileModel{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", addressLine3='" + addressLine3 + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", language='" + language + '\'' +
                ", lastName='" + lastName + '\'' +
                ", latestCoCAgreed='" + latestCoCAgreed + '\'' +
                ", phone='" + phone + '\'' +
                ", postCode='" + postCode + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
