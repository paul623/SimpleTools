package com.paul.simpletools.classbox.model;

/**
 * Created by Liu ZhuangFei on 2018/8/10.
 */

public class BoxCourseParams {

    private DeviceBean device;
    private String request_time;

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getRequest_time() {
        return request_time;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public static class DeviceBean {
        /**
         * agent : Mozilla/5.0 (Linux Android 5.0.2 vivo X5Pro D Build/LRX21M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36 classbox2/10.2.2
         * app_version : 10.2.2
         * brand : vivo
         * channel : local
         * network : wifi
         * platform : android
         * unit_type : vivo X5Pro D
         */

        private String agent="Mozilla/5.0 (Linux Android 5.0.2 vivo X5Pro D Build/LRX21M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36 classbox2/10.2.2";
        private String app_version="10.2.2";
        private String brand="vivo";
        private String channel="local";
        private String network="wifi";
        private String platform="android";
        private String unit_type="vivo X5Pro D";

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public String getApp_version() {
            return app_version;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getUnit_type() {
            return unit_type;
        }

        public void setUnit_type(String unit_type) {
            this.unit_type = unit_type;
        }
    }
}
