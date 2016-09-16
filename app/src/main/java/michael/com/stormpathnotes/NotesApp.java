package michael.com.stormpathnotes;

import android.app.Application;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.StormpathLogger;

import michael.com.stormpathnotes.util.Constants;

public class NotesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                .baseUrl(Constants.BASE_URL)
                .build();

        Stormpath.init(this, stormpathConfiguration);

        if (BuildConfig.DEBUG) {
            Stormpath.setLogLevel(StormpathLogger.VERBOSE);
        }
    }
}
