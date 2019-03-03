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
	        implementation 'com.github.JerryJin93:RatingView:0.0.3'
	}
```

### In layout file, for example:

```
    <com.jerryjin.ratingview.library.widget.newer.FlexibleRatingView
            android:id="@+id/ratingView"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            app:capacity="5"
            app:margins="5dp"
            app:drawing_mode="img_mode"
            app:off_image="@drawable/star2"
            app:maxScores="5"/>
```
#### `The layout_height is used to measure the single cell of the view.`

### Attributes:

### And in Activity file:
```
    private FlexibleRatingView mRatingView;

    mRatingView.setOnRatingChangeListener(new RatingView.OnRatingChangeListener(){
        @Override
        public void onRatingChange(float rating){
            Toast.makeText(MainActivity.this, "My rating is " + String.valueOf(rating), Toast.LENGTH_SHORT).show();
        }
    });
```