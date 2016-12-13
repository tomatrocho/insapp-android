package fr.insapp.insapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.insapp.insapp.http.AsyncResponse;
import fr.insapp.insapp.http.HttpGet;
import fr.insapp.insapp.modeles.Club;
import fr.insapp.insapp.modeles.Event;
import fr.insapp.insapp.modeles.Post;
import fr.insapp.insapp.modeles.User;

/**
 * Created by thoma on 11/12/2016.
 */

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerViewClubs, recyclerViewPosts, recyclerViewEvents, recyclerViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // query

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Recherche: " + query, Toast.LENGTH_SHORT).show();
        }

        // toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // clubs recycler view

        this.recyclerViewClubs = (RecyclerView) findViewById(R.id.recyclerview_search_clubs);
        recyclerViewClubs.setHasFixedSize(true);
        recyclerViewClubs.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerClubs = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewClubs.setLayoutManager(layoutManagerClubs);

        ClubRecyclerViewAdapter adapterClubs = new ClubRecyclerViewAdapter(this, generateClubs());
        recyclerViewClubs.setAdapter(adapterClubs);

        // posts recycler view

        this.recyclerViewPosts = (RecyclerView) findViewById(R.id.recyclerview_search_posts);
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setNestedScrollingEnabled(false);

        GridLayoutManager layoutManagerPosts = new GridLayoutManager(this, 3);
        recyclerViewPosts.setLayoutManager(layoutManagerPosts);

        PostRecyclerViewAdapter adapterPosts = new PostRecyclerViewAdapter(this, generatePosts(), R.layout.row_post_thumb);
        recyclerViewPosts.setAdapter(adapterPosts);

        // events recycler view

        this.recyclerViewEvents = (RecyclerView) findViewById(R.id.recyclerview_search_events);
        recyclerViewEvents.setHasFixedSize(true);
        recyclerViewEvents.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManagerEvents = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewEvents.setLayoutManager(layoutManagerEvents);

        EventRecyclerViewAdapter adapterEvents = new EventRecyclerViewAdapter(generateEvents(), R.layout.row_event_with_avatars);
        recyclerViewEvents.setAdapter(adapterEvents);

        // users recycler view

        this.recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerview_search_users);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setNestedScrollingEnabled(false);

        GridLayoutManager layoutManagerUsers = new GridLayoutManager(this, 3);
        recyclerViewUsers.setLayoutManager(layoutManagerUsers);

        UserRecyclerViewAdapter adapterUsers = new UserRecyclerViewAdapter(this, generateUsers());
        recyclerViewUsers.setAdapter(adapterUsers);
    }

    private List<Club> generateClubs() {
        final List<Club> clubs = new ArrayList<>();

        HttpGet request = new HttpGet(new AsyncResponse() {

            public void processFinish(String output) {
                if (!output.isEmpty()) {
                    try {
                        JSONArray jsonarray = new JSONArray(output);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            final JSONObject jsonobject = jsonarray.getJSONObject(i);
                            Club club = new Club(jsonobject);

                            if (!club.getProfilPicture().isEmpty() && !club.getCover().isEmpty())
                                clubs.add(new Club(jsonobject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        request.execute(HttpGet.ROOTASSOCIATION + "?token=" + HttpGet.credentials.getSessionToken());
        return clubs;
    }

    private List<Post> generatePosts() {
        final List<Post> posts = new ArrayList<>();

        HttpGet request = new HttpGet(new AsyncResponse() {

            public void processFinish(String output) {
                if (!output.isEmpty()) {
                    try {
                        JSONArray jsonarray = new JSONArray(output);

                        for (int i = 0; i < jsonarray.length(); i++) {
                            final JSONObject jsonobject = jsonarray.getJSONObject(i);
                            posts.add(new Post(jsonobject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        request.execute(HttpGet.ROOTPOST + "?token=" + HttpGet.credentials.getSessionToken());

        return posts;
    }

    private List<Event> generateEvents() {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            events.add(new Event(R.drawable.sample_2, "L'événement génial"));

        return events;
    }

    private List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 17; i++)
            users.add(new User("tbouvier", "tomatrocho"));

        return users;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}