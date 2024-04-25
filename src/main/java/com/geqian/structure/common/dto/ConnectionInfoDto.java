package com.geqian.structure.common.dto;

import lombok.Data;

/**
 * 数据源信息
 *
 * @author Administrator
 */
@Data
public class ConnectionInfoDto {

    private String ip;

    private Integer port;

    private String username;

    private String password;

    private String databaseType;

    private String database;

}