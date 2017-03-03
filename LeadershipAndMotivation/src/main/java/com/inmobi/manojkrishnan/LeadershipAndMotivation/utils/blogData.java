package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import java.io.Serializable;

public class blogData implements Serializable {
        String content;
        String image;

        public blogData(String content, String image) {
            this.content = content;
            this.image = image;
        }

        public String getContent() {
            return content;
        }

        public String getImage() {
            return image;
        }

    }