package jaid.number;

import jaid.collection.Tuples.IntIntPair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static jaid.number.IPUtil.getBroadcastAddress;
import static jaid.number.IPUtil.getNetworkAddress;
import static jaid.number.IPUtil.getNumberOfHosts;
import static jaid.number.IPUtil.ipAddressToInt;
import static jaid.number.IPUtil.isBroadcastAddress;
import static jaid.number.IPUtil.isInSubnet;
import static jaid.number.IPUtil.isNetworkAddress;
import static jaid.number.IPUtil.packAddresses;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IPUtilTest {

    @Test
    void testInSubnet() {
        int subnet = ipAddressToInt("192.168.1.0");
        int netmask = ipAddressToInt("255.255.255.0");
        int ipInSubnet = ipAddressToInt("192.168.1.123");
        int ipOutsideSubnet = ipAddressToInt("192.168.2.123");
        assertTrue(isInSubnet(ipInSubnet, subnet, netmask));
        assertFalse(isInSubnet(ipOutsideSubnet, subnet, netmask));
    }

    @Test
    public void testGetBroadcastAddress() {
        int subnet = ipAddressToInt("192.168.1.0");
        int netmask = ipAddressToInt("255.255.255.0");

        int expectedBroadcastAddress = ipAddressToInt("192.168.1.255");
        assertEquals(expectedBroadcastAddress, getBroadcastAddress(subnet, netmask));
    }

    @Test
    public void testGetNetworkAddress() {
        int ipAddress = ipAddressToInt("192.168.1.123");
        int netmask = ipAddressToInt("255.255.255.0");

        int expectedNetworkAddress = ipAddressToInt("192.168.1.0");
        assertEquals(expectedNetworkAddress, getNetworkAddress(ipAddress, netmask));
    }

    @Test
    public void testGetNumberOfHosts() {
        int netmask = ipAddressToInt("255.255.255.0");
        assertEquals(254, getNumberOfHosts(netmask));
    }

    @Test
    public void testIsBroadcastAddress() {
        int ipAddress = ipAddressToInt("192.168.1.255");
        int subnet = ipAddressToInt("192.168.1.0");
        int netmask = ipAddressToInt("255.255.255.0");

        assertTrue(isBroadcastAddress(ipAddress, subnet, netmask));

        int anotherIpAddress = ipAddressToInt("192.168.1.123");
        assertFalse(isBroadcastAddress(anotherIpAddress, subnet, netmask));
    }

    @Test
    public void testIsNetworkAddress() {
        int ipAddress = ipAddressToInt("192.168.1.0");
        int subnet = ipAddressToInt("192.168.1.0");
        int netmask = ipAddressToInt("255.255.255.0");

        assertTrue(isNetworkAddress(ipAddress, subnet, netmask));

        int anotherIpAddress = ipAddressToInt("192.168.1.123");
        assertFalse(isNetworkAddress(anotherIpAddress, subnet, netmask));
    }

    @Test
    public void testPackAddresses() {
        assertThat(packAddresses(emptyList())).isEmpty();
        // Single address without netmask
        assertThat(packAddresses(singletonList("192.168.1.10"))).containsExactly(
                new IntIntPair(ipAddressToInt("192.168.1.10"), ipAddressToInt("192.168.1.10")));
        // Single address range
        assertThat(packAddresses(asList("192.168.1.10/24"))).containsExactly(
                new IntIntPair(ipAddressToInt("192.168.1.0"), ipAddressToInt("192.168.1.255")));
        // Multiple addresses with netmasks
        assertThat(packAddresses(Arrays.asList("192.168.1.10/24", "10.0.0.5/16")))
                .containsExactly(
                        new IntIntPair(ipAddressToInt("192.168.1.0"), ipAddressToInt("192.168.1.255")),
                        new IntIntPair(ipAddressToInt("10.0.0.0"), ipAddressToInt("10.0.255.255"))
                );
    }
}