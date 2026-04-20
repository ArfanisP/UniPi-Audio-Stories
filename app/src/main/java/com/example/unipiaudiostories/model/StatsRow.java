//Μοντέλο δεδομένων για μία εγγραφή στατιστικών
package com.example.unipiaudiostories.model;

public class StatsRow {
    //id ιστορίας, τίτλος και πλήθος φορών που ακούστηκε
    public final String storyId;
    public final String title;
    public final int count;

    //Constructor για δημιουργία αντικειμένου στατιστικών
    public StatsRow(String storyId, String title, int count) {
        this.storyId = storyId;
        this.title = title;
        this.count = count;
    }
}
