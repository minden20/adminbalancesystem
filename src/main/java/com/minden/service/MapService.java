package com.minden.service;

import com.minden.entity.MapTile;
import java.util.List;

public interface MapService {
    void importMapFromJson(String filePath);
    List<MapTile> getAllTiles();
}
