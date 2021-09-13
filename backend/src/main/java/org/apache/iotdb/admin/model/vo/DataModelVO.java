package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DataModelVO implements Serializable {
    private String nodeName;

    private Boolean isGroup;

    private Boolean isDevice;

    private Boolean isMeasurement;

    private Integer groupCount;

    private Integer deviceCount;

    private Integer measurementCount;

    private DataInfo dataInfo;

    private List<DataModelVO> nodeChildren;

    public DataModelVO(String nodeName){
        this.nodeName = nodeName;
        this.isGroup = false;
        this.isDevice = false;
        this.isMeasurement = false;
    }

    public List<DataModelVO> initNodeChildren(){
        if(nodeChildren == null){
            nodeChildren = new ArrayList<>();
        }
        return nodeChildren;
    }
}
