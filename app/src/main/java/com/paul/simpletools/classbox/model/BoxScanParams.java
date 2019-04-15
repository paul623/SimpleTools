package com.paul.simpletools.classbox.model;

/**
 * Created by Liu ZhuangFei on 2018/8/24.
 */
public class BoxScanParams {

    /**
     * from_guid : e44777e3-9d3e-4ecf-85e9-94ec7dd01d9a
     * device : {"agent":"Mozilla/5.0 (Linux; Android 5.0.2; vivo X5Pro D Build/LRX21M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36 classbox2/10.0.4","app_version":"10.0.4","brand":"vivo","channel":"baidu","network":"wifi","platform":"android","unit_type":"vivo X5Pro D"}
     */

    private String from_guid;
    private DeviceBean device=new DeviceBean();

    public String getFrom_guid() {
        return from_guid;
    }

    public void setFrom_guid(String from_guid) {
        this.from_guid = from_guid;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public static class DeviceBean {
        /**
         * agent : Mozilla/5.0 (Linux; Android 5.0.2; vivo X5Pro D Build/LRX21M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36 classbox2/10.0.4
         * app_version : 10.0.4
         * brand : vivo
         * channel : baidu
         * network : wifi
         * platform : android
         * unit_type : vivo X5Pro D
         */

        private String agent="Mozilla/5.0 (Linux; Android 5.0.2; vivo X5Pro D Build/LRX21M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile Safari/537.36 classbox2/10.0.4";
        private String app_version="10.0.4";
        private String brand="vivo";
        private String channel="baidu";
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
