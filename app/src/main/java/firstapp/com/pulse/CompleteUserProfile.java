package firstapp.com.pulse;

public class CompleteUserProfile extends User{
    String name, email, phoneNumber, username;
    String dateOfBirth, age, address, gender;
    String profileImage;


    public CompleteUserProfile(){

    }

    public CompleteUserProfile(User user){
        this.name = user.name;
        this.email = user.email;
        this.username = user.username;
        this.phoneNumber = user.phone;
        this.dateOfBirth = "dd/mm/yyyy";
        this.age = "age";
        this.address = "address";
        this.gender = "male";
        this.profileImage = "null";

    }

    public CompleteUserProfile(String name, String email, String username, String phoneNumber, String dateOfBirth, String age, String address, String gender){
        this.name = name;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.profileImage = "null";


    }
}
