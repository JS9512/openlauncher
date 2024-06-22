package com.benny.openlauncher.activity.weblauncher;


import static android.content.Context.AUDIO_SERVICE;
import static android.support.v4.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.SettingsActivity;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;


public class WebviewPlugin {
    public Activity activity;
    public  View rootView;
    public WebView[] webViews=new WebView[5];
    private EditText urlInput;
    private int currentWebViewIndex=0;

    public LauncherModel launcherModel;
    public ConstraintLayout container;

    public Boolean isBookmarkShowing=false;

    //for move view
    int lastAction;




    public WebviewPlugin(Activity activity,View rootView){
        this.activity=activity;
        this.rootView=rootView;
        this.launcherModel=new LauncherModel(activity.getApplicationContext());
        this.container =activity.findViewById(R.id.webcontainer);


        this.init();
    }

    public void init(){
        initView();
        AllFunctionWebviewSetter.requestPermissions(activity);
        loadWeb();
        addEventListener();

    }

    void initView(){
        //container.setY(40);

    }
    private  void  loadWeb() {
        WebView webview1 = activity.findViewById(R.id.webview1);
        WebView webview2 = activity.findViewById(R.id.webview2);
        WebView webview3 = activity.findViewById(R.id.webview3);
        WebView webview4 = activity.findViewById(R.id.webview4);
        WebView webview5 = activity.findViewById(R.id.webview5);

        webViews = new WebView[]{webview1, webview2, webview3, webview4, webview5};
        urlInput = activity.findViewById((R.id.url_input));


        // Load the initial URL into the first WebView
        webview1.setVisibility(View.VISIBLE);

        for (WebView webView :
                webViews) {
            AllFunctionWebviewSetter.setWebView(webView, activity, activity.getApplicationContext(), urlInput);
            setViewSizeByPercentageOfScreen(activity, webView,
                    launcherModel.webViewWidth,
                    launcherModel.webViewHeight);
                    webView.setWebViewClient(new WebViewClient() {
                        public void onPageFinished(WebView view, String url) {
                            // Update EditText when a new page finishes loading
                            //   urlInput.setText(url);
                            for (int i = 0; i < launcherModel.autoLoadScriptArray.length; i++) {
                                if(launcherModel.autoLoadScriptArray[i]==null  ){
                                    return;
                                }
                                String[] parts = launcherModel.autoLoadScriptArray[i].split(",", 2);

                                if (parts.length == 2) {
                                    String name = parts[0];
                                    String scriptURL = parts[1];
                                    if(scriptURL.startsWith("javascript:")){
                                        view.loadUrl(scriptURL);

                                    }

                                }
                            }

                        };
                    });


            webview1.loadUrl(launcherModel.urls[0]);

        }
    }



    private void addEventListener() {

        final EditText urlInput = activity.findViewById(R.id.url_input);
        TextView zoomtext=activity.findViewById(R.id.zoomtext);

        Button fullscreenButton = activity.findViewById(R.id.fullscreenButton);
        Button refreshButton = activity.findViewById(R.id.refresh_button);
        Button increaseHeightButton = activity.findViewById(R.id.increase_height_button);
        Button openUrlButton = activity.findViewById(R.id.open_url_button);
        Button changeWebViewButton = activity.findViewById(R.id.change_webview_button);
        Button hideUrlButton=activity.findViewById(R.id.hideUrlButton);
        Button decreaseHeightButton = activity.findViewById(R.id.decrease_height_button);
        Button menuButton = activity.findViewById(R.id.menuButton);
        Button actionButton = activity.findViewById(R.id.actionButton);
        Button favButton=activity.findViewById(R.id.favButton);
        Button scriptButton=activity.findViewById(R.id.scriptButton);

        scriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup container=activity.findViewById(R.id.favoritesView);
                if(WebviewPlugin.this.isBookmarkShowing==false){

                    createFavoriteView(launcherModel.autoLoadScriptArray,
                            activity.findViewById(R.id.favoritesView),

                            activity);
                    WebviewPlugin.this.isBookmarkShowing = true;
                }
                else{
                    container.removeAllViews();
                    WebviewPlugin.this.isBookmarkShowing = false;
                }

            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup container=activity.findViewById(R.id.favoritesView);
                if(WebviewPlugin.this.isBookmarkShowing==false){

                    createFavoriteView(launcherModel.favItemArray,
                            activity.findViewById(R.id.favoritesView),

                            activity);
                    WebviewPlugin.this.isBookmarkShowing = true;
                }
                else{
                    container.removeAllViews();
                    WebviewPlugin.this.isBookmarkShowing = false;
                }
            }
        });

        zoomtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView webView=webViews[currentWebViewIndex];
                WebSettings webSettings=webView.getSettings();
               // Toggle zoom controls
                boolean isZoomControlsEnabled = webSettings.getBuiltInZoomControls();

                if (!isZoomControlsEnabled ) {
            showtoast("enable zoom",activity);
            // Enable zoom controls
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
        } else {
            // Disable zoom controls
                    showtoast("disable zoom",activity);
                    webSettings.setBuiltInZoomControls(false);
                    webSettings.setSupportZoom(false);
        }
            }
        });


        SeekBar zoomSeekBar = activity.findViewById(R.id.zoomSeekbar);
        SeekBar textZoomSeekBar = activity.findViewById(R.id.textZoomSeekbar);
        SeekBar heightSeekbar=activity.findViewById(R.id.webViewHeight);
        SeekBar widthSeekbar=activity.findViewById(R.id.webViewWidth);
        SeekBar brightnessSeekBar = activity.findViewById(R.id.brightnessSeekBar);
        SeekBar soundSeekBar;
        AudioManager audioManager;
        soundSeekBar = activity.findViewById(R.id.soundSeekBar);


        // Get AudioManager system service
        audioManager = (AudioManager) activity.getApplicationContext().getSystemService(AUDIO_SERVICE);

        // Set the maximum value of the SeekBar to the maximum volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        soundSeekBar.setMax(maxVolume);

        // Set the current volume to the SeekBar
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        soundSeekBar.setProgress(currentVolume);

        // Set a listener to handle changes in the SeekBar
        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the volume when the SeekBar is changed
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });


        // Set the maximum value of the SeekBar to the maximum brightness value
        int maxBrightness = 255;
        brightnessSeekBar.setMax(maxBrightness);
        int currentBrightness = Settings.System.getInt(
                activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                maxBrightness);
        // Set the current brightness to the SeekBar
        brightnessSeekBar.setProgress(currentBrightness);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the screen brightness when the SeekBar is changed
                try {
                    Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                    // Apply the new brightness setting
                    WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
                    layoutParams.screenBrightness = progress / (float) brightnessSeekBar.getMax();
                    activity.getWindow().setAttributes(layoutParams);
                } catch (Error | Exception e) {
                    showtoast("set brightness: " + e.toString(),activity);
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(activity.getApplicationContext())){
                        // If the permission has already been granted, take no action
                        return;
                    }
                    // Request the WRITE_SETTINGS permission
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        widthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the initial scale based on SeekBar progress
                setWebViewSize(progress,launcherModel.webViewHeight);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        heightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the initial scale based on SeekBar progress
                setWebViewSize(100,progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        textZoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the initial scale based on SeekBar progress
                webViews[currentWebViewIndex].getSettings().setTextZoom(progress);

            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the initial scale based on SeekBar progress
                webViews[currentWebViewIndex].setInitialScale(progress);

            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtoast("long press to restart app",activity);
            }
        });
        refreshButton.setOnLongClickListener((View.OnLongClickListener) v -> {
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);
            return true;
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleViewVisibility(activity.findViewById(R.id.subBrowserMenu));
            }
        });
        actionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastAction = MotionEvent.ACTION_DOWN;
                        showtoast("drag to move",activity);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX()-100 ;
                        float y = event.getRawY()-100 ;

                        // Update the LinearLayout's position
                        container.setX(x);
                        container.setY(y);
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });


		hideUrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (urlInput.getVisibility() == View.VISIBLE) {
					// If the view is currently visible, hide it
					urlInput.setVisibility(View.GONE);
				} else {
					// If the view is currently hidden, show it
					urlInput.setVisibility(View.VISIBLE);
				}
			}
		});

        changeWebViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the current URL before switching
                launcherModel.urls[currentWebViewIndex] = urlInput.getText().toString();
                launcherModel.saveSettings();

                // Increment index to switch to the next WebView
                currentWebViewIndex = (currentWebViewIndex + 1) % webViews.length;
                changeWebViewButton.setText("▷"+currentWebViewIndex);

                // Show the next WebView and update the URL input field
                urlInput.setText(launcherModel.urls[currentWebViewIndex]);

                // Hide all WebViews
                for (WebView wv : webViews) {
                    wv.setVisibility(View.GONE);
                }
                webViews[currentWebViewIndex].setVisibility(View.VISIBLE);
            }


        });
        changeWebViewButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                launcherModel.urls[currentWebViewIndex] = urlInput.getText().toString();
                launcherModel.saveSettings();



                // Increment index to switch to the next WebView
                currentWebViewIndex = 0;
                changeWebViewButton.setText("▷"+currentWebViewIndex);

                // Show the next WebView and update the URL input field
                urlInput.setText(launcherModel.urls[currentWebViewIndex]);
                // Hide all WebViews
                for (WebView wv : webViews) {
                    wv.setVisibility(View.GONE);
                }
                webViews[currentWebViewIndex].setVisibility(View.VISIBLE);

                return true;
            }
        });


        openUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView webView = webViews[currentWebViewIndex];
                webView.setVisibility(View.VISIBLE);
                String mobileUserAgent = "Mozilla/5.0 (Linux; Android 10; Pixel 3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Mobile Safari/537.36";
                webView.getSettings().setUserAgentString(mobileUserAgent);

                String url = urlInput.getText().toString().trim();
                if (!url.isEmpty()) {

                   /* if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = launcherModel.searchEnginUrl + url; // Add scheme if missing
                    }
                    webView.clearCache(true);*/
                    if(url.startsWith("javascript:") ){
                        webView.loadUrl(url); // Load the URL in the WebView
                        urlInput.setText( webView.getUrl() );

                    }
                    else{
                        webView.loadUrl(url); // Load the URL in the WebView
                        launcherModel.urls[currentWebViewIndex] = url;
                        launcherModel.saveSettings();
                    }

                }
                showtoast("long click to change to desktop mode ", activity);
            }
        });

        openUrlButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView webView = webViews[WebviewPlugin.this.currentWebViewIndex];
                String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
                webView.getSettings().setUserAgentString(desktopUserAgent);
                webView.loadUrl(urlInput.getText().toString());
                return true;
            }
        });

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle fullscreen mode

                if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
                    // Currently in fullscreen mode, revert back to normal mode
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                } else {
                    // Currently in normal mode, switch to fullscreen mode
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setViewSizeToFullScreen(webViews[currentWebViewIndex]);

                }

                int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;



                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


                activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

            }
        });

        increaseHeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherModel.webViewHeight+=10;
                launcherModel.saveSettings();
                for (WebView webview :
                        webViews) {
                    setViewSizeByPercentageOfScreen(activity,webview,
                            launcherModel.webViewWidth,
                            launcherModel.webViewHeight
                    );
                }
            }
        });
        increaseHeightButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                launcherModel.currentScale+=20;
                launcherModel.saveSettings();
                WebView webView=webViews[currentWebViewIndex];
                webView.setInitialScale(launcherModel.currentScale);
                showtoast(String.valueOf(launcherModel.currentScale),activity);
                return true;
            }
        });

        decreaseHeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherModel.webViewHeight-=10;
                launcherModel.saveSettings();
                for (WebView webview :
                        webViews) {
                    setViewSizeByPercentageOfScreen(activity,webview,
                            launcherModel.webViewWidth,
                            launcherModel.webViewHeight
                    );
                }
            }
        });
        decreaseHeightButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                launcherModel.currentScale-=20;
                launcherModel.saveSettings();
                WebView webView=webViews[currentWebViewIndex];
                webView.setInitialScale(launcherModel.currentScale);
                showtoast(String.valueOf(launcherModel.currentScale),activity);
                return true;
            }
        });





        //for setting view

        Button settingButton=activity.findViewById(R.id.settings_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout=WebviewPlugin.this.activity.findViewById(R.id.weblauncher_settings_layout);
                toggleViewVisibility(layout);
            }
        });

        Button btnSetDefaultLauncher = activity.findViewById(R.id.btnSetDefaultLauncher);
        btnSetDefaultLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                activity.startActivity(intent);
            }
        });


        Button btnAppManage = activity.findViewById(R.id.btnAppManage);
        btnAppManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                activity.startActivity(intent);
            }
        });




        Button btnBatteryOptimization = activity.findViewById(R.id.btnBatteryOptimization);
        btnBatteryOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                activity.startActivity(intent);
            }
        });

        Button btnChangeIME = activity.findViewById(R.id.btnChangeIME);
        btnChangeIME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the Input Method Editor (IME)
                // Change the Input Method Editor (IME)
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showInputMethodPicker();
            }
        });

        Button importButton = activity.findViewById(R.id.importButton);
        Button exportButton = activity.findViewById(R.id.exportButton);

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText=activity.findViewById(R.id.settingText);
                String text = editText.getText().toString();
                LauncherModel launcherModel=new LauncherModel(activity.getApplicationContext());
                launcherModel.importFromJson(text);
                editText.setText("\nNow restart app or recheck ");
                launcherModel.saveSettings();

                Intent intent = WebviewPlugin.this.activity.getIntent();
                WebviewPlugin.this.activity.finish();
                WebviewPlugin.this.activity.startActivity(intent);

            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText=activity.findViewById(R.id.settingText);
                LauncherModel launcherModel=new LauncherModel(activity.getApplicationContext());
                try {
                    editText.setText(launcherModel.exportToJsonNice());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }



    public void setWebViewSize(int  w, int h){
        launcherModel.webViewHeight=h;
        launcherModel.webViewWidth=w;
        launcherModel.saveSettings();
        for (WebView webview :
                webViews) {
            setViewSizeByPercentageOfScreen(activity,webview,
                    launcherModel.webViewWidth,
                    launcherModel.webViewHeight
            );
        }
    }

    private void toggleViewVisibility(View yourView) {
        if (yourView.getVisibility() == View.VISIBLE) {
            yourView.setVisibility(View.INVISIBLE);
        } else {
            yourView.setVisibility(View.VISIBLE);
        }
    }


    public static void setViewSizeByPercentageOfParent(View view, int widthPercentage, int heightPercentage) {
        if (view == null || !(view.getParent() instanceof View)) {
            return;
        }

        View parentView = (View) view.getParent();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (widthPercentage > 0) {
            int parentWidth = parentView.getWidth();
            layoutParams.width = (int) (parentWidth * widthPercentage / 100.0);
        }

        if (heightPercentage > 0) {
            int parentHeight = parentView.getHeight();
            layoutParams.height = (int) (parentHeight * heightPercentage / 100.0);
        }

        view.setLayoutParams(layoutParams);
    }



    public static void setViewSizeByPercentageOfScreen(Context context, View view, int widthPercentage, int heightPercentage) {
        if (view == null) {
            return;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (widthPercentage > 0) {
            int screenWidth = displayMetrics.widthPixels;
            layoutParams.width = (int) (screenWidth * widthPercentage / 100.0);
        }

        if (heightPercentage > 0) {
            int screenHeight = displayMetrics.heightPixels;
            layoutParams.height = (int) (screenHeight * heightPercentage / 100.0);
        }

        view.setLayoutParams(layoutParams);
    }

    private void setViewSizeToFullScreen(View view) {
        // Get screen height and width
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;

        // Set the height and width of the view to 100% of the screen
        view.getLayoutParams().height = screenHeight;
        view.getLayoutParams().width = screenWidth;
    }


    public  void createFavoriteView(String[] favArray, ViewGroup containerView, Activity activity) {
        // Convert the favArray to a List for easier manipulation
        containerView.removeAllViews();
        Log.d("webcontainer", "createFavoriteView: " );
        List<String> favorites = Arrays.asList(favArray);

        // Layout to contain the UI elements
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a container for the favorite items
        LinearLayout favoritesContainer = new LinearLayout(activity);
        favoritesContainer.setOrientation(LinearLayout.VERTICAL);
        layout.addView(favoritesContainer);

        // Function to display all favorite items
        displayFavorites(favorites, favoritesContainer, activity);
        // Add the layout to the container view
        containerView.addView(layout);
    }

    // Function to display favorite items
    private  void displayFavorites(List<String> favorites, LinearLayout favoritesContainer, Activity activity) {
        //seem lagest to create 15 layout in a layout
        for (int i=0;i<Math.min(15,favorites.size());i++) {
            String favorite =favorites.get(i);
            if(favorite==null || favorite.equals("null")) {
              favorite=",";
            };
            String[] parts = favorite.split(",", 2);

            if (parts.length == 2) {
                String name = parts[0];
                String url = parts[1];
                Log.d("favorites", "displayFavorites: " + name + " " + url);

                // Create a horizontal layout for each favorite
                LinearLayout favLayout = new LinearLayout(activity);
                favLayout.setOrientation(LinearLayout.HORIZONTAL);

                EditText urlText = new EditText(activity);

                // Create a TextView for the favorite name
                EditText tvName = new EditText(activity);
                tvName.setText(name);
                urlText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                urlText.setText(url);

                // Create a button to load the favorite URL
                Button btnLoad = new Button(activity);
                btnLoad.setText("\uD83C\uDF10");

                final int finalI = i;
                btnLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = urlText.getText().toString();
                        String name = tvName.getText().toString();
                        WebviewPlugin.this.webViews[WebviewPlugin.this.currentWebViewIndex].loadUrl(url);
                        launcherModel.favItemArray[finalI]=name+","+url;
                        launcherModel.saveSettings();
                        favoritesContainer.removeAllViews();

                    }
                });
                btnLoad.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        String url = urlText.getText().toString();
                        String name = tvName.getText().toString();
                        WebviewPlugin.this.webViews[WebviewPlugin.this.currentWebViewIndex].loadUrl(url);
                        launcherModel.favItemArray[finalI]=name+","+url;
                        launcherModel.saveSettings();
                        return false;
                    }
                });

                tvName.setHint("name");
                urlText.setHint("url");
                urlText.setMaxLines(3);
                tvName.setMaxLines(3);

                btnLoad.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));


                favLayout.addView(btnLoad);
                favLayout.addView(tvName);
                favLayout.addView(urlText);
                // Add the favorite layout to the container
                favoritesContainer.addView(favLayout);




            }
        }
    }



    public static void showtoast(String msg, Activity activity){
        Toast toast = Toast.makeText(activity.getApplicationContext(), " " + msg, Toast.LENGTH_SHORT);
        toast.show();


    }

}



    // Helper method to create the favorites view

