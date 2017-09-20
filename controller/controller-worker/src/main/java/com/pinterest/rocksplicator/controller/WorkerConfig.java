/*
 * Copyright 2017 Pinterest, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.pinterest.rocksplicator.controller;

import com.pinterest.rocksplicator.controller.util.ZookeeperConfigParser;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains all configuration about the worker.
 *
 * @author Shu Zhang (shu@pinterest.com)
 */
public final class WorkerConfig {

  private static final Logger LOG = LoggerFactory.getLogger(WorkerService.class);

  private static final String WORKER_POOL_SIZE_KEY = "worker_pool_size";
  private static final String DISPATCHER_POLL_INTERVAL_KEY = "dispatcher_poll_interval";
  private static final String ZK_PATH_KEY = "zk_path";
  private static final String ZK_HOSTS_FILE_PATH_KEY = "zk_hosts_file";
  private static final String ZK_CLUSTER_KEY = "zk_cluster";
  private static final String JDBC_URL_KEY = "jdbc_url";
  private static final String MYSQL_USER_KEY = "mysql_user";
  private static final String MYSQL_PASSWORD_KEY = "mysql_password";
  private static final String SENDER_EMAIL_ADDRESS_KEY = "sender_email";
  private static final String RECEIVER_EMAIL_ADDRESS_KEY = "receiver_email";

  private static final String DEFAULT_ZK_PATH = "/config/services/";
  private static final String DEFAULT_ZK_ENDPOINTS = "observerzookeeper010:2181";
  private static final String DEFAULT_ZK_HOSTS_FILE_PATH = "bin/zookeeper_hosts.conf";
  private static final String DEFAULT_ZK_CLUSTER = "default";
  private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/controller";
  private static final String DEFAULT_MYSQL_USER = "root";
  private static final String DEFAULT_MYSQL_PASSWORD = "";
  private static final String DEFAULT_SENDER_EMAIL_ADDRESS = "";
  private static final String DEFAULT_RECEIVER_EMAIL_ADDRESS = "";


  private static String HOST_NAME;
  private static PropertiesConfiguration configuration;

  static {
    String workerConfig = System.getProperty("worker_config");
    configuration = new PropertiesConfiguration();
    try {
      configuration.load(new FileInputStream(workerConfig));
    } catch (Exception e) {
      LOG.error("Cannot load worker configuration", e);
      configuration = null;
    }
    LOG.info("Worker config loaded: " + workerConfig);
    try {
      HOST_NAME = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      HOST_NAME = "UnKnown";
    }
  }

  public static long getDispatcherPollIntervalSec() {
    return configuration == null ? 10 : configuration.getLong(DISPATCHER_POLL_INTERVAL_KEY);
  }

  public static int getWorkerPoolSize() {
    return configuration == null ? 10 : configuration.getInt(WORKER_POOL_SIZE_KEY);
  }

  public static String getHostName() {
    return HOST_NAME;
  }

  public static String getZKPath() {
    return configuration == null ? DEFAULT_ZK_PATH :
                                   configuration.getString(ZK_PATH_KEY, DEFAULT_ZK_PATH);
  }

  public static String getZKEndpoints() {
    if (configuration == null) {
      return DEFAULT_ZK_ENDPOINTS;
    }
    String zkHostFilePath =
        configuration.getString(ZK_HOSTS_FILE_PATH_KEY, DEFAULT_ZK_HOSTS_FILE_PATH);
    String zkCluster = configuration.getString(ZK_CLUSTER_KEY, DEFAULT_ZK_CLUSTER);
    return ZookeeperConfigParser.parseEndpoints(zkHostFilePath, zkCluster);
  }

  public static String getJdbcUrl() {
    return configuration == null ? DEFAULT_JDBC_URL :
                                   configuration.getString(JDBC_URL_KEY, DEFAULT_JDBC_URL);
  }

  public static String getMySqlUser() {
    return configuration == null ? DEFAULT_MYSQL_USER :
                                   configuration.getString(MYSQL_USER_KEY, DEFAULT_MYSQL_USER);
  }

  public static String getMySqlPassword() {
    return configuration == null ? DEFAULT_MYSQL_PASSWORD :
                                   configuration.getString(MYSQL_PASSWORD_KEY,
                                                           DEFAULT_MYSQL_PASSWORD);
  }

  public static String getSenderEmailAddress() {
    return configuration == null ? DEFAULT_SENDER_EMAIL_ADDRESS :
                                   configuration.getString(SENDER_EMAIL_ADDRESS_KEY,
                                                           DEFAULT_SENDER_EMAIL_ADDRESS);
  }

  public static String getReceiverEmailAddress() {
    return configuration == null ? DEFAULT_RECEIVER_EMAIL_ADDRESS :
                                   configuration.getString(RECEIVER_EMAIL_ADDRESS_KEY,
                                                           DEFAULT_RECEIVER_EMAIL_ADDRESS);
  }
}
