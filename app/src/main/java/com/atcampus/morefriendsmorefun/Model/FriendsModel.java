package com.atcampus.morefriendsmorefun.Model;

public class FriendsModel {

    String image,name,email,cover,phone,bio,search,uid;

    public FriendsModel() {
    }

    public FriendsModel(String image, String name, String email, String cover, String phone, String bio, String search, String uid) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.cover = cover;
        this.phone = phone;
        this.bio = bio;
        this.search = search;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
