package firstapp.com.pulse;

public class Receipt {

    public String department, doctor, charge, timeDate;

    public Receipt(){

    }

    public Receipt(String department, String doctor, String charge, String timeDate){
        this.department = department;
        this.charge = charge;
        this.doctor = doctor;
        this.timeDate = timeDate;
    }


    public void setDepartment(String department){
        this.department = department;
    }

    public void setDoctor(String doctor){
        this.doctor = doctor;
    }

    public void setCharge(String charge){
        this.charge = charge;
    }

    public void setTimeDate(String timeDate){
        this.timeDate = timeDate;

    }


    public String getDepartment(){
        return department;
    }

    public String getDoctor(){
        return doctor;
    }

    public String getCharge(){
        return charge;
    }

    public String getTimeDate(){
        return timeDate;
    }
}


