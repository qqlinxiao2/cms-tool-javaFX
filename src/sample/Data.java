package sample;

public class Data {

    private String name;
    private String code;
    private String accessUrl;
    private String cspid;
    private String original;
    private String oneMapping;
    private String twoMapping;
    private String status;

    public Data() {
    }

    public Data(String name, String code, String accessUrl, String cspid) {
        this.name = name;
        this.code = code;
        this.accessUrl = accessUrl;
        this.cspid = cspid;
        this.original = original;
        this.oneMapping = oneMapping;
        this.twoMapping = twoMapping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getCspid() {
        return cspid;
    }

    public void setCspid(String cspid) {
        this.cspid = cspid;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getOneMapping() {
        return oneMapping;
    }

    public void setOneMapping(String oneMapping) {
        this.oneMapping = oneMapping;
    }

    public String getTwoMapping() {
        return twoMapping;
    }

    public void setTwoMapping(String twoMapping) {
        this.twoMapping = twoMapping;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
