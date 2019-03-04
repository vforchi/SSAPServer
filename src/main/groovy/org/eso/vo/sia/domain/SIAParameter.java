package org.eso.vo.siap.domain;

public enum SIAPParameter {

    POS(SIAPParameterType.POS, "s_region"),
    BAND(SIAPParameterType.NUMBER, "em_min", "em_max"),
    TIME(SIAPParameterType.NUMBER, "t_min", "t_max"),
    POL(SIAPParameterType.POL, "pol_states"),
    FOV(SIAPParameterType.NUMBER, "s_fov"),
    SPATRES(SIAPParameterType.NUMBER, "s_resolution"),
    SPECRP(SIAPParameterType.NUMBER, "em_res_power"),
    EXPTIME(SIAPParameterType.NUMBER, "t_exptime"),
    TIMERES(SIAPParameterType.NUMBER, "t_resolution"),
    ID(SIAPParameterType.STRING, "obs_publisher_did"),
    COLLECTION(SIAPParameterType.STRING, "obs_collection"),
    FACILITY(SIAPParameterType.STRING, "facility_name"),
    INSTRUMENT(SIAPParameterType.STRING, "instrument_name"),
    DPTYPE(SIAPParameterType.STRING, "dataproduct_type"),
    CALIB(SIAPParameterType.NUMBER, "calib_level"),
    TARGET(SIAPParameterType.STRING, "target_name"),
    FORMAT(SIAPParameterType.STRING, "access_format");

    private SIAPParameterType type;
    private String[] columns;

    SIAPParameter(SIAPParameterType type, String... columns) {
        this.type = type;
        this.columns = columns;
    }

    public SIAPParameterType getType() {
        return type;
    }

    public String[] getColumns() {
        return columns;
    }

}
