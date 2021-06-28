package org.apache.iotdb.admin.controller;

import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @anthor fyx 2021/6/18
 */
public class TestSystemInfo {

    @Test
    public void getCpuInfo() throws UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        System.out.println(ia.getHostAddress());
        System.out.println(ia.getHostName());
        OperatingSystemMXBean osmxb =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int freeCores = osmxb.getAvailableProcessors();
        System.out.println(freeCores);
        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024);
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / 1024 / 1024;
        System.out.println(totalMemorySize);
    }
}
