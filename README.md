# ZenMatt---SmartMat

![alt tag](http://i.imgur.com/QlTRuLX.jpg?1)

This is Android source code for a Smart Mat project which was created by me during Junction 2015 Hackathon in Helsinki. The Android Application communicates with pressure sensors located under a Yoga Mat. When someone sits on the mat, the mat sends BLE notification to the mobile phone with a non-zero value. It sends another notification after the person leaves sitting on mat with a value of zero. Time duration between two action is used to calculate the mindfullness level. The more one sits, the more the mindfulness level is increased. Each time the application goes to the background (in Android Language: onStop()), the mindfullness level is decreased. This can be extended in a way that the application collects the phone usage statistics using Android Usage API (http://developer.android.com/reference/android/app/usage/package-summary.html) and decreases the mindfullness level accordingly. The idea is controlling social media addiction by doing yoga on a smart Mat.

In the Main Activity source code the UI is implemented in a really absurd way due to limited time in Hackathon. The main idea was adding a green button behind the cropped transparent man doing yoga and modifying the button size level by level. However, this idea failed due to some problems and I came up with adding 35 background images each indicates a different level. As the level increases or decreases the background image changes :)

Currently, the Custom Mat Service should have only one BluetoothGatt characteristic with Notification property which notifies the state 1 or 0 (Pressure or No Pressure). However tHe mobile app also looks for one more Bluetooth LE Characteristic to write a value to the Smart Mat! The UUID values can be found in MatProfile.java file!

As I said this was just one day hack but I added a couple of component diagrams to describe how the backened is implemented. Feel free to extend this app with your ideas!

![alt tag](http://i.imgur.com/jy8FNKN.png?1)

![alt tag](http://i.imgur.com/capALZQ.png)

![alt tag](http://i.imgur.com/FzkyADe.png?1)

![alt tag](http://i.imgur.com/NpoqlcF.png?1)
