# RatingView
A useful Android widget for rating.

## How to use

### Step 1: Add it in your root build.gradle at the end of repositories.

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Step 2: Add the dependency.

```
	dependencies {
	        implementation 'com.github.JerryJin93:RatingView:Tag'
	}
```

### In layout file, for example:

```
    <com.jerryjin.ratingview.library.widget.RatingView
            android:id="@+id/ratingView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:starSize="70dp"/>
```
### And in Activity file:
```
    private RatingView mRatingView;

    mRatingView.setOnRatingChangeListener(new RatingView.OnRatingChangeListener(){
        @Override
        public void onRatingChange(float rating){
            Toast.makeText(MainActivity.this, "My rating is " + String.valueOf(rating), Toast.LENGTH_SHORT).show();
        }
    });
```