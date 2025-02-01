package de.devsnx.redisHomes.utils;


import org.bukkit.Color;

public enum ServerInfo {
    FEUERBRUNNEN("Feuerbrunnen-1", "7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef", "§2"),
    NEBELSPHERE("NebelSphere-1", "25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20", "§8"),
    PLOTSERVER("PlotServer-1", "879146316f092d70bca006d34103c5807d1c9585c1e21f6b8df21171981aedba", "§6"),
    GRUENWALD("GruenWald-1", "7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef", "§2"),
    DEFAULT("DEFAULT", "f7586e3a44e850768b39234cf7de9bc3fc117bc7e544b1e0a9bc1d9bca59f39e", "§7");

    private final String serverName;
    private final String skullTexture;
    private final String color;

    ServerInfo(String serverName, String skullTexture, String color) {
        this.serverName = serverName;
        this.skullTexture = skullTexture;
        this.color = color;
    }

    public String getServerName() {
        for (ServerInfo info : values()) {
            if (info.serverName.equalsIgnoreCase(this.serverName)) {
                return info.serverName;
            }
        }
        return DEFAULT.serverName;
    }

    public String getSkullTexture() {
        for (ServerInfo info : values()) {
            if (info.serverName.equalsIgnoreCase(this.serverName)) {
                return info.skullTexture;
            }
        }
        return DEFAULT.skullTexture;
    }

    public String getColor() {
        for (ServerInfo info : values()) {
            if (info.serverName.equalsIgnoreCase(this.serverName)) {
                return info.color;
            }
        }
        return DEFAULT.color;
    }
}
