package com.dolly.instagramdemo.model;

// Pojo mimicking JSON hierarchy
// For simplicity defining some classes in one class.
// Retrofit library parses the json response in the POJO objects.
public class Data {

    private Images images;
    private User user;
    private Likes likes;
    private boolean user_has_liked;
    private String id;

    public Images getImages() {
        return images;
    }

    public User getUser() {
        return user;
    }

    public String getId() {
        return id;
    }

    public Likes getLikes() {
        return likes;
    }

    // has user liked the picture
    public boolean getUser_has_liked() {
        return user_has_liked;
    }

    public void setUser_has_liked(boolean user_has_liked) {
        this.user_has_liked = user_has_liked;
    }

    public class User {

        private String profile_picture;
        private String full_name;
        private String id;
        private boolean user_has_liked;

        public String getProfile_picture() {
            return profile_picture;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getId() {
            return id;
        }
    }

    // get images
    public class Images {

        private Standard_resolution standard_resolution;

        public Standard_resolution getStandard_resolution() {
            return standard_resolution;
        }

        public class Standard_resolution {

            private String url;

            public String getUrl() {
                return url;
            }
        }
    }

    // number of likes on a picture
    public class Likes {

        private String count;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }
    }
}
