package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public abstract class Entity implements Serializable {

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = -1;
    public static final int TEST_USER_ID = 5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    public static String toDateString(Date date){
        DateFormat dtf = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        return dtf.format(date);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private int statusCode;
    private Date created;
    protected String name;
    protected String featuredImage;
    protected long id;
    protected final long serialVersionUID = 1l;
}