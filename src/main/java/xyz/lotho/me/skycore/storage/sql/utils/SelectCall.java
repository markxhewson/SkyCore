package xyz.lotho.me.skycore.storage.sql.utils;

import java.sql.ResultSet;

public interface SelectCall {
    void call(ResultSet resultSet);
}
