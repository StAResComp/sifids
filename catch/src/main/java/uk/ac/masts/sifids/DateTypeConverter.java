package uk.ac.masts.sifids;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by pgm5 on 19/02/2018.
 */

public class DateTypeConverter {
    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }
}

