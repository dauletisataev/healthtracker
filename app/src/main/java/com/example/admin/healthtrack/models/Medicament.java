package com.example.admin.healthtrack.models;

public class Medicament {
    public String name;
    public int count;
    public int days;
    public float dosage;
    public int every;
    public int treatmentId;
    public boolean isEqually;

    public Medicament(){

    }

    public Medicament(String name, int count, int days, float dosage, int every, int treatmentId, boolean isEqually) {

        this.name = name;
        this.count = count;
        this.days = days;
        this.dosage = dosage;
        this.every = every;
        this.treatmentId = treatmentId;
        this.isEqually = isEqually;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public float getDosage() {
        return dosage;
    }

    public void setDosage(float dosage) {
        this.dosage = dosage;
    }

    public int getEvery() {
        return every;
    }

    public void setEvery(int every) {
        this.every = every;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }
    public long getTime(int i){
        int start = 9*3600;
        int end = 22*3600;
        if(count == 1) return start;
        return start + (end-start)/(count-1)*i;
    }
    @Override
    public String toString() {
        return "Medicament{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", days=" + days +
                ", dosage=" + dosage +
                ", every=" + every +
                ", treatmentId=" + treatmentId +
                '}';
    }
}
