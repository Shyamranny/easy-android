package com.shyam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class InstantAutoComplete extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    public static final String AUTOCOMPLETE_STRINGS = "autocomplete-strings";
    public static final String AUTOCOMPLETE_SETTINGS = "autocomplete_settings";
    List<String> autocompleteStore;
    ArrayAdapter<String> adapter;

    public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    private void configure(){

        if (null != autocompleteStore){
            return;
        }

        autocompleteStore = new ArrayList<>();

        SharedPreferences prefs = this.getContext().getSharedPreferences(AUTOCOMPLETE_SETTINGS, Context.MODE_PRIVATE);
        String autoCompletePref = prefs.getString(AUTOCOMPLETE_STRINGS, null);
        if (null != autoCompletePref){
            try {
                JSONArray array = new JSONArray(autoCompletePref);
                for(int i = 0; i < array.length(); i++){
                    autocompleteStore.add(array.getString(i));
                }
            } catch (JSONException e) {}
        }
        adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, autocompleteStore);
        this.setAdapter(adapter);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        configure();
        if (focused && getFilter()!=null) {
            performFiltering(super.getText(), 0);
        }
    }

    public String getTextString() {

        String txt = super.getText().toString();
        if (null != autocompleteStore && null != txt && !txt.isEmpty() && !autocompleteStore.contains(txt)){
            autocompleteStore.add(txt);
            if (null != adapter){
                adapter.notifyDataSetChanged();
            }
            saveAutoText();
        }

        return txt;
    }

    private void saveAutoText() {

        JSONArray array = new JSONArray();
        for (String str : autocompleteStore){
            array.put(str);
        }

        SharedPreferences prefs = this.getContext().getSharedPreferences(AUTOCOMPLETE_SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().putString(AUTOCOMPLETE_STRINGS, array.toString()).commit();
    }

}
