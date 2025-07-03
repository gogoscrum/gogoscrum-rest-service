package com.shimi.gogoscrum.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for IP address operations.
 */
public class IpUtil {
    private static final Logger log = LoggerFactory.getLogger(IpUtil.class);
    private static final String UNKNOWN = "unknown";
    private static final List<String> LOCAL_HOSTS = Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1");
    private static final String SEPARATOR = ",";

    private IpUtil() {
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCAL_HOSTS.contains(ipAddress)) {
                    ipAddress = getLocalHost(ipAddress);
                }
            }

            if (ipAddress != null && ipAddress.length() > 15 && ipAddress.contains(SEPARATOR)) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    private static String getLocalHost(String ipAddress) {
        InetAddress inet = null;
        try {
            inet = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("Error caught while getting local host address", e);
        }
        if (inet != null) {
            ipAddress = inet.getHostAddress();
        }
        return ipAddress;
    }

}
