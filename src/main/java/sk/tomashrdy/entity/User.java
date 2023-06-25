package sk.tomashrdy.entity;


import sk.tomashrdy.dbCon.DatabaseConnection;


public class User {
    String name , lastName , email , password;
    boolean admin = false;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    //Konštruktor používany keď ťaham udaje z databázy a ukladam používatela tam mi netreba heslo
    public User(String name, String lastName, String email , boolean isAdmin) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.admin = isAdmin;
    }
    //Konštruktor používany pri vytvárani používatela tu mi netreba admina lebo je štandardne false
    public User(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    //Metoda ktorá mi pridá noveho užívatela do databázy pomocou mojej metódy executeUpdate
    public void userRegister(User user){
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.executeUpdate("INSERT INTO users (first_name, last_name, email, password, isadmin) VALUES (?, ?, ?, ?, ?)" ,
                user.getName() , user.getLastName() , user.getEmail() , user.getPassword() , user.admin);

    }
}
