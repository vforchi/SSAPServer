package org.eso.vo.sia.domain;

public enum SIAParameter {

    POS(SIAParameterType.POS, "s_region"),
    BAND(SIAParameterType.NUMBER, "em_min", "em_max"),
    TIME(SIAParameterType.NUMBER, "t_min", "t_max"),
    POL(SIAParameterType.POL, "pol_states"),
    FOV(SIAParameterType.NUMBER, "s_fov"),
    SPATRES(SIAParameterType.NUMBER, "s_resolution"),
    SPECRP(SIAParameterType.NUMBER, "em_res_power"),
    EXPTIME(SIAParameterType.NUMBER, "t_exptime"),
    TIMERES(SIAParameterType.NUMBER, "t_resolution"),
    ID(SIAParameterType.STRING, "obs_publisher_did"),
    COLLECTION(SIAParameterType.STRING, "obs_collection"),
    FACILITY(SIAParameterType.STRING, "facility_name"),
    INSTRUMENT(SIAParameterType.STRING, "instrument_name"),
    DPTYPE(SIAParameterType.STRING, "dataproduct_type"),
    CALIB(SIAParameterType.NUMBER, "calib_level"),
    TARGET(SIAParameterType.STRING, "target_name"),
    FORMAT(SIAParameterType.STRING, "access_format");

    private SIAParameterType type;
    private String[] columns;

    SIAParameter(SIAParameterType type, String... columns) {
        this.type = type;
        this.columns = columns;
    }

    public SIAParameterType getType() {
        return type;
    }

    public String[] getColumns() {
        return columns;
    }

}
