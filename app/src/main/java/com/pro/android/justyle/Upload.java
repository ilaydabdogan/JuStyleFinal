package com.pro.android.justyle;

import com.google.firebase.database.Exclude;

class Upload{
private String mName;
private String mImageUrl;
private String mDescription;
private String mKey;
private String mUserName;
private String mAction;

        /**
         * empty constructor is needed
         */
public Upload(){
 }

Upload(String name, String imageUrl, String description, String userName, String action) {
        /**
         * Upload class contains getters and setters for UserName, Name, ImageUrl, Description,
         * Action and Key
         * If there is no name and description it sets name and description as "No name" and
         * "No description"
         */

        if (name.trim().equals("")){
        name = "No name";
        }

        if (description.trim().equals("")){
                description = "No description";
        }

        mName = name;
        mImageUrl = imageUrl;
        mDescription = description;
        mUserName = userName;
        mAction = action;

        }

        String getUserName() {
                return mUserName;
        }

        public void setUserName(String userName) { mUserName = userName; }

        public String getName(){
        return mName;
        }

        public void setName (String name){
        mName = name;
        }

        String getImageUrl(){
        return mImageUrl;
        }

        public void setImageUrl (String imageUrl){ mImageUrl = imageUrl; }

        String getDescription(){
                return mDescription;
        }

       public void setDescription (String description){ mDescription = description; }

        String getAction() { return mAction;}

        public void setAction(String action) { mAction = action; }

        @Exclude // we don't want this in firebase database because we already have a key

        String getKey(){
        return mKey;
}
        @Exclude
        void setKey(String key){ mKey = key; }


}