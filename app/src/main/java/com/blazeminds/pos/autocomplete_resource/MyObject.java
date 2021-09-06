package com.blazeminds.pos.autocomplete_resource;

/**
 * Created by Saad Kalim on 13-Apr-16.
 */
public class MyObject {
    
    public String objectName,objectId;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    // constructor for adding sample data
    public MyObject(String objectName) {
        
        this.objectName = objectName;
    }

    public MyObject(String objectName, String objectId) {
        this.objectName = objectName;
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }
    
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
