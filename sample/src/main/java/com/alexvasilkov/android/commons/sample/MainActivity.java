package com.alexvasilkov.android.commons.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.alexvasilkov.android.commons.state.InstanceStateGson;
import com.alexvasilkov.android.commons.state.InstanceStateManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    @InstanceStateGson
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SubItem s = new SubItem();
        s.name = "SubItem 1";
        Item i = new Item();
        i.list = Arrays.asList(s);
        items = Arrays.asList(i);

        Bundle b = new Bundle();
        InstanceStateManager.saveInstanceState(this, b);
        InstanceStateManager.restoreInstanceState(this, b);

        Log.d("Test", "SubItem = " + items.get(0).list.get(0).name);
    }

    private static class Item {
        List<SubItem> list;
    }

    private static class SubItem {
        String name;
    }

}
