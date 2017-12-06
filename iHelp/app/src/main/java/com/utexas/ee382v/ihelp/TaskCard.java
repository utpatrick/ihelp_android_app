package com.utexas.ee382v.ihelp;

/**
 * Created by kyle on 11/16/17.
 */

public class TaskCard {

    private String id;
    private String title;
    private String content;
    private String ownerEmail;
    private String taskLocation;
    private String taskDest;
    private String helper;
    private String helpee;
    private boolean needHelp;

    public TaskCard(String title, String content, String ownerEmail) {
        this.title = title;
        this.content = content;
        this.ownerEmail = ownerEmail;
    }

    public TaskCard(String title, String content, String iconUrl, String taskLocation,
                    String taskDest, String helper, String helpee, boolean needHelp) {
        this.title = title;
        this.content = content;
        this.ownerEmail = iconUrl;
        this.taskLocation = taskLocation;
        this.taskDest = taskDest;
        this.helper = helper;
        this.helpee = helpee;
        this.needHelp = needHelp;
    }

    public String getTaskLocation() {
        return taskLocation;
    }

    public void setTaskLocation(String taskLocation) {
        this.taskLocation = taskLocation;
    }

    public String getTaskDest() {
        return taskDest;
    }

    public void setTaskDest(String taskDest) {
        this.taskDest = taskDest;
    }

    public String getHelper() {
        return helper;
    }

    public void setHelper(String helper) {
        this.helper = helper;
    }

    public String getHelpee() {
        return helpee;
    }

    public void setHelpee(String helpee) { this.helpee = helpee;}

    public boolean isNeedHelp() {
        return needHelp;
    }

    public void setNeedHelp(boolean needHelp) {
        this.needHelp = needHelp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setTaskID(String taskID){ this.id = taskID;}

    public String getTaskID(){ return id;}
}
