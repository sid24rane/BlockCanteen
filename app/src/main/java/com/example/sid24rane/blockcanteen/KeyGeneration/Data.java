package com.example.sid24rane.blockcanteen.KeyGeneration;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public List<String> departmentNameData(){
        List<String>  departments = new ArrayList<String>();
        departments.add("Computer");
        departments.add("Information Tehcnology");
        departments.add("MCA");
        departments.add("Electronics");
        departments.add("Mechanical");
        departments.add("Electrical");
        departments.add("Production");
        departments.add("Textile");
        departments.add("Electronics and Telecommunication");
        return departments;
    }


    public List<String> usertypeData() {
        List<String>  departments = new ArrayList<String>();
        departments.add("Student");
        departments.add("Faculty");
        departments.add("Staff");
        departments.add("Others");
        return departments;
    }
}
