package picpay.pagamento.api.enums;

public enum UserType {
    USER_TYPE("USER"),
    STORE_TYPE("ADMIN");

    private String userType;

    UserType(String userType){
        this.userType = userType;
    }
     public String getUserType(){
        return this.userType;
    }
}
