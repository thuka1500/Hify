package com.amsavarthan.hify.ui.extras.Weather.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.amsavarthan.hify.R;
import com.amsavarthan.hify.ui.extras.Weather.basic.GeoActivity;
import com.amsavarthan.hify.ui.extras.Weather.data.entity.model.Location;
import com.amsavarthan.hify.ui.extras.Weather.ui.adapter.LocationAdapter;
import com.amsavarthan.hify.ui.extras.Weather.ui.decotarion.ListDecoration;
import com.amsavarthan.hify.ui.extras.Weather.utils.SnackbarUtils;
import com.amsavarthan.hify.ui.extras.Weather.utils.helpter.DatabaseHelper;
import com.amsavarthan.hify.ui.extras.Weather.utils.helpter.IntentHelper;
import com.amsavarthan.hify.ui.extras.Weather.utils.manager.ShortcutsManager;


/**
 * Manage activity.
 */

public class ManageActivity extends GeoActivity
        implements View.OnClickListener, LocationAdapter.OnLocationItemClickListener {

    public static final int SEARCH_ACTIVITY = 1;
    private CoordinatorLayout container;
    private CardView cardView;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initData();
            initWidget();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    this.adapter = new LocationAdapter(
                            this,
                            DatabaseHelper.getInstance(this).readLocationList(),
                            true,
                            this);
                    recyclerView.setAdapter(adapter);
                    Snackbar.make(
                            getSnackbarContainer(),
                            R.string.feedback_collect_succeed,
                            Snackbar.LENGTH_SHORT).show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    ShortcutsManager.refreshShortcuts(this, adapter.itemList);
                }
                break;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // do nothing.
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    private void initData() {
        this.adapter = new LocationAdapter(
                this,
                DatabaseHelper.getInstance(this).readLocationList(),
                true,
                this);
    }

    private void initWidget() {
        this.container = findViewById(R.id.activity_manage_container);

        this.cardView = findViewById(R.id.activity_manage_searchBar);
        cardView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardView.setTransitionName(getString(R.string.transition_activity_search_bar));
        }

        this.recyclerView = findViewById(R.id.activity_manage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new ListDecoration(this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new LocationSwipeCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteLocation(int position) {
        Location item = adapter.itemList.get(position);
        if (adapter.itemList.size() <= 1) {
            adapter.removeData(position);
            adapter.insertData(item, position);
            SnackbarUtils.showSnackbar(getString(R.string.feedback_location_list_cannot_be_null));
        } else {
            Location location = adapter.itemList.get(position);
            adapter.removeData(position);
            DatabaseHelper.getInstance(ManageActivity.this).deleteLocation(item);
            SnackbarUtils.showSnackbar(
                    getString(R.string.feedback_delete_succeed),
                    getString(R.string.cancel),
                    new CancelDeleteListener(location));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutsManager.refreshShortcuts(ManageActivity.this, adapter.itemList);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_manage_searchBar:
                IntentHelper.startSearchActivityForResult(this, cardView);
                break;
        }
    }

    // interface.

    // on click listener.


    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void onClick(View view, int position) {
        String locationName = adapter.itemList.get(position).isLocal() ?
                getString(R.string.local) : adapter.itemList.get(position).city;

        setResult(
                RESULT_OK,
                new Intent().putExtra(MainActivity.KEY_MAIN_ACTIVITY_LOCATION, locationName));
        finish();
        overridePendingTransitionExit();
    }

    @Override
    public void onDelete(View view, int position) {
        deleteLocation(position);
    }

    // on location item click listener.

    private class LocationSwipeCallback extends ItemTouchHelper.SimpleCallback {

        LocationSwipeCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            adapter.moveData(fromPosition, toPosition);
            DatabaseHelper.getInstance(ManageActivity.this).writeLocationList(adapter.itemList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutsManager.refreshShortcuts(ManageActivity.this, adapter.itemList);
            }

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            deleteLocation(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            switch (actionState) {
                case ItemTouchHelper.ACTION_STATE_SWIPE:
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                    break;
            }
        }
    }

    private class CancelDeleteListener
            implements View.OnClickListener {
        // data
        private Location location;

        CancelDeleteListener(Location l) {
            this.location = l;
        }

        @Override
        public void onClick(View view) {
            adapter.insertData(location, adapter.getItemCount());
            DatabaseHelper.getInstance(ManageActivity.this).writeLocation(location);
        }
    }
}
