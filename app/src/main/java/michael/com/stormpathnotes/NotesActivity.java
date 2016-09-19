package michael.com.stormpathnotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.StormpathError;
import com.stormpath.sdk.models.UserProfile;
import com.stormpath.sdk.ui.StormpathLoginActivity;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import michael.com.stormpathnotes.util.Constants;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Callback;


public class NotesActivity extends AppCompatActivity {

//    SwipeRefreshLayout swipeContainer;
    EditText mNote, mContact;
    Context context;
    private OkHttpClient okHttpClient;
    public static final String ACTION_GET_NOTES = "notes.get";
    public static final String ACTION_POST_NOTES = "notes.post";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        context = this;

        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Stormpath.logger().d(message);
                    }
                });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();

        setFabButton();
        setEditText();
//        pullToRefresh();

    }

    private void setFabButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, getString(R.string.saving), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

//                    saveNote();
                    postData();
                }
            });
        }
    }

//    private void pullToRefresh() {
//
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                IntentFilter noteGetFilter = new IntentFilter(ACTION_GET_NOTES);
//                IntentFilter notePostFilter = new IntentFilter(ACTION_POST_NOTES);
//
//                LocalBroadcastManager.getInstance(NotesActivity.this).registerReceiver(onNoteReceived, noteGetFilter);
//                LocalBroadcastManager.getInstance(NotesActivity.this).registerReceiver(onNoteReceived, notePostFilter);
//
//                Stormpath.getUserProfile(new StormpathCallback<UserProfile>() {
//                    @Override
//                    public void onSuccess(UserProfile userProfile) {
////                getNotes();
//                        getData();
//                        swipeContainer.setRefreshing(false);
//                    }
//
//                    @Override
//                    public void onFailure(StormpathError error) {
//                        // Show login view
//                        startActivity(new Intent(context, StormpathLoginActivity.class));
//                    }
//                });            }
//        });
//    }

    private void setEditText() {
        mNote = (EditText) findViewById(R.id.note);
        mNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mContact = (EditText) findViewById(R.id.contact);
        mContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter noteGetFilter = new IntentFilter(ACTION_GET_NOTES);
        IntentFilter notePostFilter = new IntentFilter(ACTION_POST_NOTES);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNoteReceived, noteGetFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNoteReceived, notePostFilter);

        Stormpath.getUserProfile(new StormpathCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
//                getNotes();
                getData();
//                postData();
            }

            @Override
            public void onFailure(StormpathError error) {
                // Show login view
                startActivity(new Intent(context, StormpathLoginActivity.class));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNoteReceived);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            Stormpath.logout();
            startActivity(new Intent(context, StormpathLoginActivity.class));
            mNote.setText(""); //clears edit text, could alternatively save to shared preferences

            return true;
        }
        if (id == R.id.action_delete){

            deleteContact();
            Toast.makeText(this, "Contact Deleted!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteContact(){

        ApiService.ApiCall mCall = ApiService.create();


        retrofit2.Call<Contacts> makeApiCall = mCall.deleteContact("57db897933b1b30011de5c4a");
        makeApiCall.enqueue(new retrofit2.Callback<Contacts>() {
            @Override
            public void onResponse(retrofit2.Call<Contacts> call, retrofit2.Response<Contacts> response) {

            }

            @Override
            public void onFailure(retrofit2.Call<Contacts> call, Throwable t) {

                t.getMessage();
            }
        });
    }

    private BroadcastReceiver onNoteReceived = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().contentEquals(ACTION_GET_NOTES))
                mNote.setText(intent.getExtras().getString("notes"));
            else if (intent.getAction().contentEquals(ACTION_POST_NOTES))
                Snackbar.make(mNote, getString(R.string.saved), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

        }
    };

    private void saveNote() {
        RequestBody requestBody = new FormBody.Builder()
                .add("notes", mNote.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "notes")
                .headers(buildStandardHeaders((Stormpath.accessToken())))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                Intent intent = new Intent(ACTION_POST_NOTES);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    private void getNotes() {
        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "notes")
                .headers(buildStandardHeaders(Stormpath.accessToken()))
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                JSONObject mNotes;

                try {
                    mNotes = new JSONObject(response.body().string());
                    String noteCloud = mNotes.getString("notes");

                    // You can also include some extra data.
                    Intent intent = new Intent(ACTION_GET_NOTES);
                    intent.putExtra("notes", noteCloud);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } catch (JSONException e) {
                }
            }
        });
    }

    private void getData() {

        ApiService.ApiCall mCall = ApiService.create();

        retrofit2.Call<List<Contacts>> makeApiCall = mCall.getContacts();

        makeApiCall.enqueue(new retrofit2.Callback<List<Contacts>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Contacts>> call, retrofit2.Response<List<Contacts>> response) {

                List<Contacts> mContacts = response.body();
                String noteCloud = mContacts.get(0).toString();
                Log.d("ACTIVITY", noteCloud);
                // You can also include some extra data.
                Intent intent = new Intent(ACTION_GET_NOTES);
                intent.putExtra("notes", noteCloud);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }

            @Override
            public void onFailure(retrofit2.Call<List<Contacts>> call, Throwable t) {
                Log.d("MainActivity", t.toString());

            }
        });
    }

    private void postData() {

        ApiService.ApiCall mCall = ApiService.create();

        retrofit2.Call<Contacts> makeApiCall = mCall.postObject(new Contacts(mNote.getText().toString(),
                mContact.getText().toString()));

        makeApiCall.enqueue(new retrofit2.Callback<Contacts>() {
            @Override
            public void onResponse(retrofit2.Call<Contacts> call, retrofit2.Response<Contacts> response) {
                Contacts body = response.body();
//                Log.d("POSTDATA", body.toString());
            }

            @Override
            public void onFailure(retrofit2.Call<Contacts> call, Throwable t) {

            }
        });


    }

    private Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }
}
