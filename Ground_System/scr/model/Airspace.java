package model;

import fr.enac.sita.visuradar.model.IBaseMap;
import fr.enac.sita.visuradar.model.IBeacon;
import fr.enac.sita.visuradar.model.ICartographyManager;
import fr.enac.sita.visuradar.model.ISector;

import java.util.*;

/**
 * This class implements the different airspace data such as the France Map and the beacons of this zone
 * @see BeaconData
 * @see FlightData
 */
public class Airspace {
    private final Map<String, IBeacon> beaconsByNameMap;
    private final Map<String, ISector> sectorsByNameMap;
    private final IBaseMap baseMap;

    public Airspace(ICartographyManager cartographyManager){
        beaconsByNameMap = new HashMap<>();
        sectorsByNameMap = new HashMap<>();
        for(IBeacon b : cartographyManager.loadBeacons()) beaconsByNameMap.put(b.getCode(), b);
        for(ISector s : cartographyManager.loadSectors()) sectorsByNameMap.put(s.getName(), s);
        baseMap = cartographyManager.loadBaseMap();
    }

    public boolean containsSector(String sector){
        return sectorsByNameMap.containsKey(sector);
    }

    public boolean containsBeacon(String beacon){
        return beaconsByNameMap.containsKey(beacon);
    }

    public Map<String, ISector> getSectorsByNameMap(){
        Map<String, ISector> sectorMap = new HashMap<>();
        for(ISector s : sectorsByNameMap.values()) sectorMap.put(s.getName(), s);
        return sectorMap;
    }

    public Map<String, IBeacon> getBeaconsByNameMap(){
        Map<String, IBeacon> sectorMap = new HashMap<>();
        for(IBeacon b : beaconsByNameMap.values()) sectorMap.put(b.getCode(), b);
        return sectorMap;
    }

    public List<IBeacon> getPublishedBeacons(){
        List<IBeacon> publishedBeacons = new ArrayList<>();
        for(IBeacon b : beaconsByNameMap.values()){
            if(Objects.equals(b.getType(), "published")) publishedBeacons.add(b);
        }
        return publishedBeacons;
    }

    public IBaseMap getBaseMap() {
        return baseMap;
    }

}
