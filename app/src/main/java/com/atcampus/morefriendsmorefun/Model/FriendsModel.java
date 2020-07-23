package com.atcampus.morefriendsmorefun.Model;

public class FriendsModel {

    String profileImage,profileName,profileEmail,profileCover,profilePhone,profileBio,profileSearch,profileUid;

    public FriendsModel(String profileImage, String profileName, String profileEmail, String profileCover, String profilePhone, String profileBio, String profileSearch, String profileUid) {
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.profileEmail = profileEmail;
        this.profileCover = profileCover;
        this.profilePhone = profilePhone;
        this.profileBio = profileBio;
        this.profileSearch = profileSearch;
        this.profileUid = profileUid;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getProfileCover() {
        return profileCover;
    }

    public void setProfileCover(String profileCover) {
        this.profileCover = profileCover;
    }

    public String getProfilePhone() {
        return profilePhone;
    }

    public void setProfilePhone(String profilePhone) {
        this.profilePhone = profilePhone;
    }

    public String getProfileBio() {
        return profileBio;
    }

    public void setProfileBio(String profileBio) {
        this.profileBio = profileBio;
    }

    public String getProfileSearch() {
        return profileSearch;
    }

    public void setProfileSearch(String profileSearch) {
        this.profileSearch = profileSearch;
    }

    public String getProfileUid() {
        return profileUid;
    }

    public void setProfileUid(String profileUid) {
        this.profileUid = profileUid;
    }
}
