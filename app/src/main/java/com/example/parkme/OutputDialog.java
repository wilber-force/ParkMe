package com.example.parkme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class OutputDialog extends DialogFragment {
    List<String> choice;
    String results;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       if(ParkActivity.results!=null) {
           results = ParkActivity.results;
       }
       else if(ReserveActivity.results!=null){
           results = ReserveActivity.results;
       }
       else{
           results = "No parking selected";
       }
       choice = new ArrayList<>();
        choice.add("You have parked your vehicle at: ");
        choice.add(results);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Your confirmation details");
        alert.setItems(choice.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Successfully saved!!!!", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setCancelable(false);
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Details saved successfully", Toast.LENGTH_LONG).show();
                results = null;
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finishAfterTransition();
            }
        });
        return alert.create();
    }
}
