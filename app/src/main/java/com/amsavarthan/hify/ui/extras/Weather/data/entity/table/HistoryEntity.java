package com.amsavarthan.hify.ui.extras.Weather.data.entity.table;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;

import com.amsavarthan.hify.ui.extras.Weather.data.entity.model.History;
import com.amsavarthan.hify.ui.extras.Weather.data.entity.model.weather.Weather;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * History entity.
 */

@Entity
public class HistoryEntity {

    @Id
    public Long id;

    public String cityId;
    public String city;
    public String date;

    public int maxiTemp;
    public int miniTemp;

    @Generated(hash = 1344990835)
    public HistoryEntity(Long id, String cityId, String city, String date, int maxiTemp, int miniTemp) {
        this.id = id;
        this.cityId = cityId;
        this.city = city;
        this.date = date;
        this.maxiTemp = maxiTemp;
        this.miniTemp = miniTemp;
    }

    @Generated(hash = 1235354573)
    public HistoryEntity() {
    }

    private static HistoryEntity buildHistoryEntity(History history) {
        HistoryEntity entity = new HistoryEntity();
        entity.cityId = history.cityId;
        entity.city = history.city;
        entity.date = history.date;
        entity.maxiTemp = history.maxiTemp;
        entity.miniTemp = history.miniTemp;
        return entity;
    }

    // insert.

    public static void insertHistory(SQLiteDatabase database, Weather weather) {
        if (weather == null) {
            return;
        }

        History yesterday = searchYesterdayHistory(database, weather);
        clearLocationHistory(database, weather);

        HistoryEntityDao dao = new DaoMaster(database)
                .newSession()
                .getHistoryEntityDao();
        if (yesterday != null) {
            dao.insert(buildHistoryEntity(yesterday));
        }
        dao.insert(buildHistoryEntity(History.buildHistory(weather)));
    }

    // delete.

    private static void clearLocationHistory(SQLiteDatabase database, Weather weather) {
        if (weather == null) {
            return;
        }

        List<HistoryEntity> entityList = searchHistoryEntity(database, weather);
        HistoryEntityDao dao = new DaoMaster(database)
                .newSession()
                .getHistoryEntityDao();
        for (int i = 0; i < entityList.size(); i++) {
            dao.delete(entityList.get(i));
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static History searchYesterdayHistory(SQLiteDatabase database, Weather weather) {
        if (weather == null) {
            return null;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(weather.base.date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);

            HistoryEntityDao dao = new DaoMaster(database)
                    .newSession()
                    .getHistoryEntityDao();

            QueryBuilder<HistoryEntity> builder = dao.queryBuilder();
            builder.where(
                    HistoryEntityDao.Properties.CityId.eq(weather.base.cityId),
                    HistoryEntityDao.Properties.Date.eq(format.format(calendar.getTime())));

            List<HistoryEntity> entityList = builder.list();
            if (entityList == null || entityList.size() <= 0) {
                return null;
            } else {
                return History.buildHistory(entityList.get(0));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<HistoryEntity> searchHistoryEntity(SQLiteDatabase database, Weather weather) {
        if (weather == null) {
            return new ArrayList<>();
        }

        return new DaoMaster(database)
                .newSession()
                .getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.CityId.eq(weather.base.cityId))
                .list();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityId() {
        return this.cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMaxiTemp() {
        return this.maxiTemp;
    }

    public void setMaxiTemp(int maxiTemp) {
        this.maxiTemp = maxiTemp;
    }

    public int getMiniTemp() {
        return this.miniTemp;
    }

    public void setMiniTemp(int miniTemp) {
        this.miniTemp = miniTemp;
    }
}
