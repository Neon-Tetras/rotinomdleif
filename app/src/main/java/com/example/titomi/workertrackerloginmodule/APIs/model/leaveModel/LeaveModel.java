package com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LeaveModel implements Serializable
{

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("approvedBy")
    @Expose
    private String approvedBy;
    @SerializedName("applicant")
    @Expose
    private Applicant applicant;
    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("toDate")
    @Expose
    private String toDate;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("date")
    @Expose
    private Object date;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("numDays")
    @Expose
    private Integer numDays;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("created")
    @Expose
    private Object created;
    @SerializedName("updated")
    @Expose
    private Object updated;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("id")
    @Expose
    private String id;
    private final static long serialVersionUID = -6305727652654605707L;

    /**
     * No args constructor for use in serialization
     *
     */
    public LeaveModel() {
    }

    /**
     *
     * @param status
     * @param reason
     * @param approvedBy
     * @param toDate
     * @param date
     * @param id
     * @param statusCode
     * @param message
     * @param fromDate
     * @param updated
     * @param created
     * @param description
     * @param numDays
     * @param name
     * @param userId
     * @param applicant
     * @param comment
     */
    public LeaveModel(String userId, String approvedBy, Applicant applicant, String fromDate, String toDate, String reason, Object date, String comment, Integer numDays, Integer statusCode, Object message, Object created, Object updated, String status, Object name, Object description, String id) {
        super();
        this.userId = userId;
        this.approvedBy = approvedBy;
        this.applicant = applicant;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.date = date;
        this.comment = comment;
        this.numDays = numDays;
        this.statusCode = statusCode;
        this.message = message;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getNumDays() {
        return numDays;
    }

    public void setNumDays(Integer numDays) {
        this.numDays = numDays;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getCreated() {
        return created;
    }

    public void setCreated(Object created) {
        this.created = created;
    }

    public Object getUpdated() {
        return updated;
    }

    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}