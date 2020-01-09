package rpc.test.core.bean;

import java.io.Serializable;

public class TestBean implements Serializable {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "value='" + value + '\'' +
                '}';
    }
}
