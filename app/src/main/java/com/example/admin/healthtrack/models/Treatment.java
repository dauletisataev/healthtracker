package com.example.admin.healthtrack.models;

import java.util.ArrayList;

public class Treatment {
    public int id;
    public String name;
    public int diet;
    public ArrayList<Medicament> medicaments;
    public Treatment(){
        //for firebase
    }

    public ArrayList<Medicament> getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(ArrayList<Medicament> medicaments) {
        this.medicaments = medicaments;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", diet=" + diet +
                '}';
    }
}
