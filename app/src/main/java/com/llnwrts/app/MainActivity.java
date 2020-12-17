package com.llnwrts.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.llnwrts.streaming.config.LLNWConfiguration;
import com.llnwrts.streaming.connection.LLConnectionListener;
import com.llnwrts.streaming.connection.LLDataChannelListener;
import com.llnwrts.streaming.connection.LLPublisherClient;
import com.llnwrts.streaming.connection.LLSubscriberClient;
import com.llnwrts.streaming.exceptions.LLUnsupportedCodecException;
import com.llnwrts.streaming.types.Message.LLDataBroadcastMessage;
import com.llnwrts.streaming.types.Message.LLDataMessage;
import com.llnwrts.streaming.types.Message.LLUpdateResponseMessage;
import com.llnwrts.streaming.view.LLVideoView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String TAG = "LLWebRTC::MainActivity";
    LLSubscriberClient rtcSubscriberClient = null;
    LLPublisherClient rtcPublisherClient = null;
    private boolean playing = false;
    private Button subscribeButton;
    private Button incrementButton;
    private Button decrementButton;
    private SeekBar volumeBar;
    private LLVideoView view;
    private Button sayHelloButton;
    private TextView messageTextView;
    private String message = "";
    private Button muteButton;
    private boolean muted = false;
    private TextView counter;
    private boolean publishing;
    private Button publishButton;
    private Button configButton;
    private SharedPreferences sharedPref;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        view = (LLVideoView) findViewById(R.id.llView);

        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(view -> onSubscribeToggle());

        muteButton = (Button) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(view -> onMuteToggle());

        publishButton = (Button) findViewById(R.id.publishButton);
        publishButton.setOnClickListener(view -> onPublishToggle());

        incrementButton = (Button) findViewById(R.id.incrementButton);

        incrementButton.setOnClickListener(view -> increment(1));

        decrementButton = (Button) findViewById(R.id.decrementButton);

        decrementButton.setOnClickListener(view -> increment(-1));

        messageTextView = (TextView) findViewById(R.id.textView1);
        counter = (TextView) findViewById(R.id.textView2);


        sayHelloButton = (Button) findViewById(R.id.sayHelloButton);

        sayHelloButton.setOnClickListener(view -> sayHello());

        configButton = (Button) findViewById(R.id.configureButton);
        configButton.setOnClickListener(view -> openConfigs());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        volumeBar = findViewById(R.id.volumeBar); // initiate a volume bar

        volumeBar.setMax(10); // 10 maximum value for the volume bar

        volumeBar.setProgress(5); // 5 default volume value

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                rtcSubscriberClient.setVolume(progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rtcSubscriberClient != null) {
            rtcSubscriberClient.mute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rtcSubscriberClient != null) {
            rtcSubscriberClient.unmute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    publish();
                } else {
                    setMessageText("Camera Permission Denied");
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        rtcSubscriberClient.handleVolumeButton(keyCode);
        return true;
    }

    private void publish() {
        publishButton.setEnabled(false);
        publishButton.setText("Stop Publishing");
        subscribeButton.setEnabled(false);
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                publishButton.setEnabled(true);
            }
        }.start();

        rtcPublisherClient.publish(false);
        publishing = true;
    }

    public void setMessageText(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageTextView.setText(text);
            }
        });
    }

    private void increment(int i) {
        ArrayList count = new ArrayList();
        count.add("count");
        try {
            rtcSubscriberClient.dataObjectInc(count, i, true);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }
    }

    private void onMuteToggle() {
        if (!muted) {
            rtcSubscriberClient.mute();
            muted = true;
            muteButton.setText("Unmute");
        } else {
            rtcSubscriberClient.unmute();
            muted = false;
            muteButton.setText("Mute");
        }
    }

    public void sayHello() {
        try {
            rtcSubscriberClient.sendBroadcastMessage("Hello World");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSetQuality(String constraint) {
        if (playing) {
            rtcSubscriberClient.setVariant(constraint);
        }
    }

    private void onPressPause() {
        if (playing) {
            rtcSubscriberClient.stop();
            incrementButton.setText("Play");
            playing = false;
        } else {
            rtcSubscriberClient.play();
            incrementButton.setText("Pause");
            playing = true;
        }
    }

    public void setSubscribeButtonText(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                subscribeButton.setText(text);
            }
        });
    }

    private void initSubscriberClient() {
        if (rtcSubscriberClient != null) {
            return;
        }
        JSONObject configJsonObject = new JSONObject();
        try {
            configJsonObject.put("app", getPref(getString(R.string.app)));
            configJsonObject.put("host", getPref(getString(R.string.host)));
            configJsonObject.put("protocol", "https");
            configJsonObject.put("streamName", getPref(getString(R.string.streamName)));
//            configJsonObject.put("validationUrl", "https://subscribe-validator.rts.llnwi.net/mmddev001/auth/v2/llnw-test-001/?ci=100&cd=100&cf=1600000000&h=c0518fc5aa957b128147545dfe45cc0f");
            rtcSubscriberClient = LLSubscriberClient.init(
                    this,
                    view,
                    new LLNWConfiguration(configJsonObject)
            );
            setDefaultSubscriberListeners();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (LLUnsupportedCodecException e) {
            setMessageText(e.getMessage());
        }
    }

    private void initPublisherClient() {
        if (rtcPublisherClient != null) {
            return;
        }
        JSONObject configJsonObject = new JSONObject();
        try {
            configJsonObject.put("app", "rtsv2demo");
            configJsonObject.put("host", "rts-1-lax.llnw.net");
            configJsonObject.put("protocol", "https");
            configJsonObject.put("streamName", "poliva-1");
            configJsonObject.put("username", "poliva-1");
            configJsonObject.put("password", "p0liv4");
            configJsonObject.put("rendition", "1000");
//            configJsonObject.put("validationUrl", "https://subscribe-validator.rts.llnwi.net/mmddev001/auth/v2/llnw-test-001/?ci=100&cd=100&cf=1600000000&h=c0518fc5aa957b128147545dfe45cc0f");
            rtcPublisherClient = LLPublisherClient.init(
                    this,
                    view,
                    new LLNWConfiguration(configJsonObject)
            );
            setDefaultPublisherListeners();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (LLUnsupportedCodecException e) {
            setMessageText(e.getMessage());
        }
    }

    private void setDefaultSubscriberListeners() {
        LLDataChannelListener llDataChannelListener = getLlDataChannelListener();
        rtcSubscriberClient.setDataChanelListener(llDataChannelListener);

        LLConnectionListener llConnectionListener = getLlConnectionListener();
        rtcSubscriberClient.setConnectionListener(llConnectionListener);
    }

    private void setDefaultPublisherListeners() {
        LLDataChannelListener llDataChannelListener = getLlDataChannelListener();
        rtcPublisherClient.setDataChanelListener(llDataChannelListener);

        LLConnectionListener llConnectionListener = getLlConnectionListener();
        rtcPublisherClient.setConnectionListener(llConnectionListener);
    }

    @NotNull
    private LLDataChannelListener getLlDataChannelListener() {
        // set callbacks for data messages
        return new LLDataChannelListener() {
            @Override
            public void onDataObjectMessage(LLDataMessage msg) {
                Log.i(TAG, "onDataObjectMessage: " + msg.toString());
                String text = "\n" + msg.getSender() + ": " + msg.getMessage();
                message += text;
                setMessageText(message);
            }

            @Override
            public void onDataObjectUpdateResponse(LLUpdateResponseMessage msg) {
                Log.i(TAG, "onDataObjectUpdateResponse: " +
                        String.format(
                                "type: %s, requestResponseCorrelationId: %s, response: %s",
                                msg.getType(), msg.getRequestResponseCorrelationId(), msg.getResponse()
                        ));
            }

            @Override
            public void onDataObjectBroadcast(LLDataBroadcastMessage msg) {
                Log.i(TAG, "onDataObjectBroadcast: " + msg.toString());
                try {
                    JSONObject object = msg.getDataObject();
                    JSONObject map = object.getJSONObject("map");
                    String count = map.getString("count");
                    setCounter(count);
                } catch (JSONException e) {
                    Log.d(TAG, "Count key not found on broadcast message.");
                    return;
                }
            }
        };
    }

    @NotNull
    private LLConnectionListener getLlConnectionListener() {
        return new LLConnectionListener() {
            @Override
            public void onStreamNotFound() {
                setMessageText("Ingest not found.");
                Log.i(TAG, "Stream not found.");
            }

            @Override
            public void onConnectSuccess() {
                setSubscribeButtonText("Stop");
            }

            @Override
            public void onDisconnect() {
                setSubscribeButtonText("Start");
            }

            @Override
            public void onSubscribeStop() {
                setMessageText("");
            }

            @Override
            public void onProfilesReceived(ArrayList<String> profiles) {
                for (String profile : profiles) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.profileButtons);
                    addProfileButton(layout, profile);
                }
            }

            @Override
            public void onSetVolume(int volume) {
                volumeBar.setProgress(volume);
            }
        };
    }

    private void addProfileButton(LinearLayout layout, String id) {
        Context me = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button btnTag = new Button(me);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText("Quality " + id);
                btnTag.setId(Integer.parseInt(id));

                btnTag.setOnClickListener(view -> onSetQuality(id));

                layout.addView(btnTag);

            }
        });
    }

    private void setCounter(String count) {
        String text = "Count: " + count;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                counter.setText(text);
            }
        });
    }

    private void onSubscribeToggle() {
        initSubscriberClient();
        if (rtcSubscriberClient == null) {
            return;
        }
        subscribeButton.setEnabled(false);
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                subscribeButton.setEnabled(true);
            }
        }.start();

        if (playing) {
            rtcSubscriberClient.unsubscribe(false);
            publishButton.setEnabled(true);
            playing = false;
        } else {
            rtcSubscriberClient.subscribe(false);
            publishButton.setEnabled(false);
            playing = true;
        }
    }

    private void onPublishToggle() {
        if (!publishing) {
            initPublisherClient();
            if (rtcPublisherClient == null) {
                return;
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    200);
        } else {
            publishing = false;
            rtcPublisherClient.unpublish(false);
            publishButton.setText("Publish");
            subscribeButton.setEnabled(true);
        }
    }

    public void openConfigs() {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }


    private String getPref(String key) {
        return sharedPref.getString(key, "");
    }
}
