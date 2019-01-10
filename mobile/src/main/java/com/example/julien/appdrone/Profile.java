package com.example.julien.appdrone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

class Profile implements Serializable {

    String username;
    String photoPath;

    Profile(String username) {
        // When you create a new Profile, it's good to build it based on username and password
        this.username = username;
    }
}
