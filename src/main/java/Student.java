public class Student {
    String name;
    int age;
    String gender;
    String id;
    String address;
    String phone;
    public Student(String name, int age, String gender, String id, String address, String phone) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.id = id;
        this.address = address;
        this.phone = phone;
    }   
    
    public String getName() {   
        return name;
    }
    public int getAge() {
        return age;
    }
    public String getGender() {
        return gender;
    }
    public String getId() {
        return id;
    }  
     

    
}
