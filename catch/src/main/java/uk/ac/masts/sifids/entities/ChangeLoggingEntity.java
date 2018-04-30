package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

/**
 * Base class to be extended by entities which require "created at" and "modified at" timestamps.
 * These should be any entities which can be modified by the user. Classes extending this should
 * call updateDates() at the end of setters and constructors.
 */
public abstract class ChangeLoggingEntity extends EntityWithId {

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "modified_at")
    private Date modifiedAt;

    @Override
    public void setId(int id) {
        if (this.getId() == 0) {
            this.id = id;
            this.updateDates();
        }
    }

    /**
     * The timestamp of the creation of the entity
     * @return
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Required to be public according to the Android Room API, this setter should not be called.
     * Rather, setters for other attributes should implement calls to updateDates().
     * @param createdAt the timestamp of the creation of the entity
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * The timestamp of the last modification to the entity
     * @return
     */
    public Date getModifiedAt() {
        return modifiedAt;
    }

    /**
     * Required to be public according to the Android Room API, this setter should not be called.
     * Rather, setters for other attributes should implement calls to updateDates().
     * @param modifiedAt the timestamp of the last modification of the entity
     */
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    protected void updateDates() {
        Date now = new Date();
        if (this.getCreatedAt() == null) {
            this.setCreatedAt(now);
        }
        this.setModifiedAt(now);
    }
}
