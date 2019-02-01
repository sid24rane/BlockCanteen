package com.example.sid24rane.blockcanteen.KeyGeneration;

public class UserModel {
        private String firstName;
        private String lastName;
        private String email;
        private String id;
        private String publicKey;

        public UserModel(String firstName, String lastName, String email, String id, String publicKey){
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.id = id;
            this.publicKey = publicKey;
        }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getLastName(){
            return lastName;
    }

    public String getId() {
        return id;
    }

}
