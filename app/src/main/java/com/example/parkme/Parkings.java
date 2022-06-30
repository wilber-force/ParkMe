package com.example.parkme;

public class Parkings {


    private String slots;
    private String description;
    private String title;
    private int allocated;


    public Parkings() {
    }


    public Parkings (String description, String slots,String title,int allocated) {

        this.description = description;
        this.slots = slots;
        this.title = title;
        this.allocated = allocated;

    }

    public int getAllocated_slots() {
        return allocated;
    }

    public void setAllocated_slots(int allocated) {
        this.allocated = allocated;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

}
