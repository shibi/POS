package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.ShiftRegEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ShiftDao {


    @Insert(onConflict = REPLACE)
    void insertShift(ShiftRegEntity shift);

    @Query("SELECT * FROM shiftRegTable")
    List<ShiftRegEntity> getAllShifts();


    //@Query("SELECT * FROM shiftRegTable WHERE timestamp = (SELECT MAX(timeStamp) FROM shiftRegTable)")
    @Query("SELECT * FROM shiftRegTable ORDER BY timestamp DESC LIMIT 1")
    ShiftRegEntity getLastEntryInShift();

}
