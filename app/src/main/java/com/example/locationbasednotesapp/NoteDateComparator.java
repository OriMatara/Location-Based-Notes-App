package com.example.locationbasednotesapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class NoteDateComparator implements Comparator<Note>{

    private SimpleDateFormat dateFormat;

    public NoteDateComparator() {
        // Define the date format used in the Note object
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public int compare(Note note1, Note note2) {
        try {
            // Parse the date strings to Date objects
            Date date1 = dateFormat.parse(note1.getDate());
            Date date2 = dateFormat.parse(note2.getDate());

            // Compare the dates
            return date2.compareTo(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parsing exception, return 0 as default
            return 0;
        }
    }
}
