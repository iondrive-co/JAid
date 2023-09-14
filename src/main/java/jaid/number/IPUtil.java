package jaid.number;

public class IPUtil {

    /**
     * Checks if the given IPv4 address is within the specified subnet.
     *
     * @param ipAddress the IP address as an int.
     * @param subnet    the subnet address as an int.
     * @param netmask   the netmask as an int.
     * @return true if the IP address is within the subnet, false otherwise.
     */
    public static boolean isInSubnet(int ipAddress, int subnet, int netmask) {
        // An XOR between the IP address and the subnet sets the bits that are different between the two to 1.
        // An AND operation with the netmask checks if any of the bits that are set to 1 fall outside the range
        // specified by the netmask. If none do (the result is 0) then the IP address is in the subnet.
        return ((ipAddress ^ subnet) & netmask) == 0;
    }

    /**
     * Calculates the broadcast address of a subnet.
     *
     * @param subnet  the subnet address as an int.
     * @param netmask the netmask as an int.
     * @return the broadcast address as an int.
     */
    public static int getBroadcastAddress(int subnet, int netmask) {
        return subnet | (~netmask);
    }

    /**
     * Calculates the network address of a subnet.
     *
     * @param ipAddress the IP address as an int.
     * @param netmask   the netmask as an int.
     * @return the network address as an int.
     */
    public static int getNetworkAddress(int ipAddress, int netmask) {
        return ipAddress & netmask;
    }

    /**
     * Calculates the number of hosts in a subnet.
     *
     * @param netmask the netmask as an int.
     * @return the number of hosts in the subnet.
     */
    public static int getNumberOfHosts(int netmask) {
        return (~netmask) - 1;
    }

    /**
     * Checks if the given IP address is the broadcast address of the subnet.
     *
     * @param ipAddress the IP address as an int.
     * @param subnet    the subnet address as an int.
     * @param netmask   the netmask as an int.
     * @return true if the IP address is the broadcast address of the subnet, false otherwise.
     */
    public static boolean isBroadcastAddress(int ipAddress, int subnet, int netmask) {
        return ipAddress == getBroadcastAddress(subnet, netmask);
    }

    /**
     * Checks if the given IP address is the network address of the subnet.
     *
     * @param ipAddress the IP address as an int.
     * @param subnet    the subnet address as an int.
     * @param netmask   the netmask as an int.
     * @return true if the IP address is the network address of the subnet, false otherwise.
     */
    public static boolean isNetworkAddress(int ipAddress, int subnet, int netmask) {
        return ipAddress == getNetworkAddress(subnet, netmask);
    }

    /**
     * Converts a dotted decimal string representation of an IP address to an integer.
     *
     * @param ipAddress the IP address in dotted decimal notation.
     * @return the IP address as an integer.
     */
    public static int ipAddressToInt(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        return (Integer.parseInt(parts[0]) << 24) |
                (Integer.parseInt(parts[1]) << 16) |
                (Integer.parseInt(parts[2]) << 8) |
                Integer.parseInt(parts[3]);
    }
}
