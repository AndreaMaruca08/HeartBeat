package data;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for monitoring the computer
 */
public final class Monitoring {
    private static final SystemInfo info = new SystemInfo();
    private static final OperatingSystem os = info.getOperatingSystem();
    private static final HardwareAbstractionLayer hardware = info.getHardware();

    private static final CentralProcessor cpu = hardware.getProcessor();
    private static final GlobalMemory ram = hardware.getMemory();
    private static final Sensors sensors = hardware.getSensors();

    private static long[] prevTicks = cpu.getSystemCpuLoadTicks();
    private static long[][] prevProcTicks = cpu.getProcessorCpuLoadTicks();

    private Monitoring() {}

    // CPU
    public static String getCpuName() {
        return cpu.getProcessorIdentifier().getName();
    }

    public static String getCpuVendor() {
        return cpu.getProcessorIdentifier().getVendor();
    }

    public static int getPhysicalCoreCount() {
        return cpu.getPhysicalProcessorCount();
    }

    public static int getLogicalCoreCount() {
        return cpu.getLogicalProcessorCount();
    }

    public static long getMaxFreqHz() {
        return cpu.getMaxFreq();
    }

    public static double getMaxFreqGHz() {
        return cpu.getMaxFreq() / 1_000_000_000.0;
    }

    public static long[] getCurrentFreqPerCoreHz() {
        return cpu.getCurrentFreq();
    }

    public static double getCpuLoad() {
        double load = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = cpu.getSystemCpuLoadTicks();
        return load;
    }

    public static double[] getCpuLoadPerCore() {
        double[] load = cpu.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        prevProcTicks = cpu.getProcessorCpuLoadTicks();
        return Arrays.stream(load).map(l -> l * 100).toArray();
    }

    public static double getSystemLoadAverage() {
        return cpu.getSystemLoadAverage(1)[0];
    }

    public static long getContextSwitches() {
        return cpu.getContextSwitches();
    }

    public static long getInterrupts() {
        return cpu.getInterrupts();
    }

    // RAM
    public static long getTotalRamBytes() {
        return ram.getTotal();
    }

    public static long getAvailableRamBytes() {
        return ram.getAvailable();
    }

    public static long getUsedRamBytes() {
        return ram.getTotal() - ram.getAvailable();
    }

    public static double getRamUsagePercent() {
        return (getUsedRamBytes() * 100.0) / ram.getTotal();
    }

    public static long getSwapTotalBytes() {
        return ram.getVirtualMemory().getSwapTotal();
    }

    public static long getSwapUsedBytes() {
        return ram.getVirtualMemory().getSwapUsed();
    }

    public static List<PhysicalMemory> getPhysicalMemoryBanks() {
        return ram.getPhysicalMemory();
    }

    // Discs
    public static List<HWDiskStore> getDiskStores() {
        return hardware.getDiskStores();
    }

    public static long getTotalDiskReadBytes() {
        return getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
    }

    public static long getTotalDiskWriteBytes() {
        return getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();
    }

    public static List<OSFileStore> getFileStores() {
        return os.getFileSystem().getFileStores();
    }

    public static long getTotalDiskSpaceBytes() {
        return getFileStores().stream().mapToLong(OSFileStore::getTotalSpace).sum();
    }

    public static long getUsableDiskSpaceBytes() {
        return getFileStores().stream().mapToLong(OSFileStore::getUsableSpace).sum();
    }

    // Network
    public static List<NetworkIF> getNetworkInterfaces() {
        List<NetworkIF> nets = hardware.getNetworkIFs();
        nets.forEach(NetworkIF::updateAttributes);
        return nets;
    }

    public static long getTotalBytesReceived() {
        return getNetworkInterfaces().stream().mapToLong(NetworkIF::getBytesRecv).sum();
    }

    public static long getTotalBytesSent() {
        return getNetworkInterfaces().stream().mapToLong(NetworkIF::getBytesSent).sum();
    }

    // GPU
    public static List<GraphicsCard> getGraphicsCards() {
        return hardware.getGraphicsCards();
    }

    public static List<String> getGpuNames() {
        return getGraphicsCards().stream().map(GraphicsCard::getName).toList();
    }

    public static long getGpuVRamBytes(int index) {
        return getGraphicsCards().get(index).getVRam();
    }

    // Sensors
    public static double getCpuTemperature() {
        return sensors.getCpuTemperature();
    }

    public static int[] getFanSpeeds() {
        return sensors.getFanSpeeds();
    }

    public static double getCpuVoltage() {
        return sensors.getCpuVoltage();
    }


    // Battery
    public static List<PowerSource> getPowerSources() {
        return hardware.getPowerSources();
    }

    public static double getBatteryPercent() {
        List<PowerSource> sources = getPowerSources();
        if (sources.isEmpty()) return -1;
        return sources.get(0).getRemainingCapacityPercent() * 100;
    }

    public static boolean isCharging() {
        List<PowerSource> sources = getPowerSources();
        return !sources.isEmpty() && sources.get(0).isCharging();
    }

    public static double getPowerUsageRateWatts() {
        List<PowerSource> sources = getPowerSources();
        if (sources.isEmpty()) return -1;
        return sources.get(0).getPowerUsageRate();
    }

    public static double getBatteryTimeRemainingMinutes() {
        List<PowerSource> sources = getPowerSources();
        if (sources.isEmpty()) return -1;
        return sources.get(0).getTimeRemainingEstimated() / 60.0;
    }


    // USB
    public static List<UsbDevice> getUsbDevices() {
        return hardware.getUsbDevices(true);
    }

    // Process
    public static List<OSProcess> getAllProcesses() {
        return os.getProcesses(
                OperatingSystem.ProcessFiltering.ALL_PROCESSES,
                OperatingSystem.ProcessSorting.NO_SORTING,
                0
        );
    }

    public static List<OSProcess> getTopProcessesByCpu(int limit) {
        return os.getProcesses(
                OperatingSystem.ProcessFiltering.ALL_PROCESSES,
                OperatingSystem.ProcessSorting.CPU_DESC,
                limit
        );
    }

    public static List<OSProcess> getTopProcessesByRam(int limit) {
        return os.getProcesses(
                OperatingSystem.ProcessFiltering.ALL_PROCESSES,
                OperatingSystem.ProcessSorting.RSS_DESC,
                limit
        );
    }

    public static OSProcess getCurrentProcess() {
        return os.getProcess(os.getProcessId());
    }

    public static int getProcessCount() {
        return os.getProcessCount();
    }

    public static int getThreadCount() {
        return os.getThreadCount();
    }

    public static String getOsFamily() {
        return os.getFamily();
    }

    public static String getOsVersion() {
        return os.getVersionInfo().toString();
    }

    public static String getOsFullName() {
        return os.toString();
    }

    public static int getBitness() {
        return os.getBitness();
    }

    public static long getSystemBootTimeEpoch() {
        return os.getSystemBootTime();
    }

    public static Instant getSystemBootTime() {
        return Instant.ofEpochSecond(os.getSystemBootTime());
    }

    public static long getSystemUptimeSeconds() {
        return os.getSystemUptime();
    }

    public static String getManufacturer() {
        return hardware.getComputerSystem().getManufacturer();
    }

    public static String getModel() {
        return hardware.getComputerSystem().getModel();
    }

    public static String getSerialNumber() {
        return hardware.getComputerSystem().getSerialNumber();
    }

    public static String getBiosVersion() {
        return hardware.getComputerSystem().getFirmware().getVersion();
    }

    public static String getMotherboardModel() {
        return hardware.getComputerSystem().getBaseboard().getModel();
    }
}